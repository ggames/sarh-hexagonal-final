package com.fich.sarh.auth.application.services;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fich.sarh.auth.application.model.AuthResponse;
import com.fich.sarh.auth.application.model.AuthenticatedUser;
import com.fich.sarh.auth.application.model.LoginRequest;
import com.fich.sarh.auth.application.model.RefreshTokenClaims;
import com.fich.sarh.auth.application.ports.entrypoint.api.AuthApiPort;
import com.fich.sarh.auth.application.ports.output.persistence.RefreshTokenSpiPort;
import com.fich.sarh.auth.application.ports.output.persistence.TokenProviderPort;
import com.fich.sarh.auth.domain.model.RefreshToken;
import com.fich.sarh.common.UseCase;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@UseCase
@RequiredArgsConstructor
public class AuthServiceUseCase implements AuthApiPort {

    Logger logger = LoggerFactory.getLogger(getClass());

    private final AuthenticationManager authenticationManager;
    private final TokenProviderPort tokenProvider;
    private final UserDetailsService userDetailService;
    private final RefreshTokenSpiPort refreshTokenPort;

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();

        boolean mustChangePassword = user.getMustChangePassword();
        String accessToken = tokenProvider.createAccessToken(authentication, mustChangePassword);
        String refreshToken = tokenProvider.createRefreshToken(authentication);
        RefreshTokenClaims decodedRefresh = tokenProvider.validateRefreshToken(refreshToken);

        refreshTokenPort.save(RefreshToken.builder()
                .jti(decodedRefresh.jti())
                .familyId(UUID.randomUUID().toString())
                .userId(user.getId())
                .expiresAt(decodedRefresh.expiresAt())
                .createdAt(Instant.now())
                .build());

        Set<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return new AuthResponse(
                user.getId(),
                user.getUsername(),
                mustChangePassword ? "PASSWORD_CHANGE_REQUIRED" : "LOGIN_OK",
                accessToken,
                refreshToken,
                mustChangePassword,
                authorities,
                true
        );
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(String refreshToken) {

        RefreshTokenClaims decoded = tokenProvider.validateRefreshToken(refreshToken);
        String jti = decoded.jti();

        if (jti == null || jti.isBlank()) {
            throw new BadCredentialsException("Refresh token inválido");
        }

        RefreshToken stored = refreshTokenPort.findByJtiForUpdate(jti)
                .orElseThrow(() -> new BadCredentialsException("Refresh token no reconocido"));

        Instant now = Instant.now();

        if (stored.wasUsed()) {
            refreshTokenPort.revokeFamily(stored.getFamilyId(), now);
            throw new BadCredentialsException("Reutilización de refresh token detectada");
        }

        if (!stored.isUsable(now)) {
            throw new BadCredentialsException("Refresh token expirado o revocado");
        }

        refreshTokenPort.save(stored.toBuilder().usedAt(now).build());

        String username = decoded.username();
        UserDetails userDetails = userDetailService.loadUserByUsername(username);

        boolean mustChangePassword = userDetails instanceof AuthenticatedUser authenticatedUser
                && authenticatedUser.getMustChangePassword();

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );

        String newAccessToken = tokenProvider.createAccessToken(authentication, mustChangePassword);
        String newRefreshToken = tokenProvider.createRefreshToken(authentication);
        RefreshTokenClaims decodedNewRefresh = tokenProvider.validateRefreshToken(newRefreshToken);

        refreshTokenPort.save(RefreshToken.builder()
                .jti(decodedNewRefresh.jti())
                .familyId(stored.getFamilyId())
                .userId(stored.getUserId())
                .expiresAt(decodedNewRefresh.expiresAt())
                .createdAt(now)
                .build());

        Set<String> authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toSet());

        return new AuthResponse(
                null,
                username,
                "ACCESS_TOKEN_REFRESHED",
                newAccessToken,
                newRefreshToken,
                mustChangePassword,
                authorities,
                true
        );
    }

    @Override
    @Transactional
    public void logout(String refreshToken) {

        if (refreshToken == null || refreshToken.isBlank()) {
            return;
        }

        try {
            RefreshTokenClaims decoded = tokenProvider.validateRefreshToken(refreshToken);
            if (decoded.jti() == null) {
                return;
            }
            refreshTokenPort.findByJtiForUpdate(decoded.jti())
                    .ifPresent(stored -> refreshTokenPort.revokeFamily(
                            stored.getFamilyId(), Instant.now()));
        } catch (JWTVerificationException e) {
            logger.debug("Logout con refresh token inválido: {}", e.getMessage());
        }
    }

}
