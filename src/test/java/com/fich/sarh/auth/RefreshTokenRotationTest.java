package com.fich.sarh.auth;

import com.fich.sarh.auth.application.model.AuthResponse;
import com.fich.sarh.auth.application.model.RefreshTokenClaims;
import com.fich.sarh.auth.application.ports.output.persistence.RefreshTokenSpiPort;
import com.fich.sarh.auth.application.ports.output.persistence.TokenProviderPort;
import com.fich.sarh.auth.application.services.AuthServiceUseCase;
import com.fich.sarh.auth.domain.model.RefreshToken;
import com.fich.sarh.auth.infrastructure.adapter.configuration.security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Seguridad — Rotación de refresh tokens por familia")
class RefreshTokenRotationTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenProviderPort tokenProvider;

    @Mock
    private UserDetailsService userDetailService;

    @Mock
    private RefreshTokenSpiPort refreshTokenPort;

    @InjectMocks
    private AuthServiceUseCase useCase;

    private Instant now;
    private CustomUserDetails user;

    @BeforeEach
    void setUp() {
        now = Instant.now();
        user = new CustomUserDetails(
                1L,
                "admin",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")),
                true, true, true, true, false
        );
    }

    private RefreshTokenClaims claims(String jti, String subject, Instant expiresAt) {
        return new RefreshTokenClaims(jti, subject, expiresAt);
    }

    private RefreshToken storedToken() {
        return RefreshToken.builder()
                .jti("jti-old")
                .familyId("family-1")
                .userId(1L)
                .expiresAt(now.plusSeconds(600))
                .createdAt(now.minusSeconds(60))
                .build();
    }

    @Test
    @DisplayName("Renovar consume el jti anterior y emite uno nuevo de la misma familia")
    void renovarRotaElJtiDentroDeLaMismaFamilia() {
        when(tokenProvider.validateRefreshToken("old-token")).thenReturn(claims("jti-old", "admin", now.plusSeconds(600)));
        when(refreshTokenPort.findByJtiForUpdate("jti-old"))
                .thenReturn(Optional.of(storedToken()));
        when(userDetailService.loadUserByUsername("admin")).thenReturn(user);
        when(tokenProvider.createAccessToken(any(), anyBoolean())).thenReturn("new-access");
        when(tokenProvider.createRefreshToken(any())).thenReturn("new-refresh");
        when(tokenProvider.validateRefreshToken("new-refresh"))
                .thenReturn(claims("jti-new", "admin", now.plusSeconds(3600)));

        AuthResponse response = useCase.refreshToken("old-token");

        assertThat(response.getAccessToken()).isEqualTo("new-access");
        assertThat(response.getRefreshToken()).isEqualTo("new-refresh");

        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenPort, times(2)).save(captor.capture());
        List<RefreshToken> guardados = captor.getAllValues();

        assertThat(guardados.get(0).getJti()).isEqualTo("jti-old");
        assertThat(guardados.get(0).getUsedAt()).isNotNull();
        assertThat(guardados.get(1).getJti()).isEqualTo("jti-new");
        assertThat(guardados.get(1).getFamilyId()).isEqualTo("family-1");
        assertThat(guardados.get(1).getUsedAt()).isNull();
        verify(refreshTokenPort, never()).revokeFamily(anyString(), any());
    }

    @Test
    @DisplayName("Reutilizar un jti consumido revoca toda la familia")
    void reutilizarJtiConsumidoRevocaLaFamilia() {
        RefreshToken consumido = storedToken().toBuilder()
                .usedAt(now.minusSeconds(5))
                .build();

        when(tokenProvider.validateRefreshToken("old-token")).thenReturn(claims("jti-old", "admin", now.plusSeconds(600)));
        when(refreshTokenPort.findByJtiForUpdate("jti-old"))
                .thenReturn(Optional.of(consumido));

        assertThatThrownBy(() -> useCase.refreshToken("old-token"))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Reutilización");

        verify(refreshTokenPort).revokeFamily(eq("family-1"), any(Instant.class));
        verify(tokenProvider, never()).createRefreshToken(any());
    }

    @Test
    @DisplayName("Logout revoca la familia asociada al refresh token")
    void logoutRevocaLaFamilia() {
        when(tokenProvider.validateRefreshToken("old-token")).thenReturn(claims("jti-old", "admin", now.plusSeconds(600)));
        when(refreshTokenPort.findByJtiForUpdate("jti-old"))
                .thenReturn(Optional.of(storedToken()));

        useCase.logout("old-token");

        verify(refreshTokenPort).revokeFamily(eq("family-1"), any(Instant.class));
    }
}
