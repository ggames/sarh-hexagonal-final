package com.fich.sarh.auth;

import com.fich.sarh.auth.application.ports.output.persistence.UserRetrieveSpiPort;
import com.fich.sarh.auth.application.ports.output.persistence.UserSaveSpiPort;
import com.fich.sarh.auth.application.services.UserUpdateUseCase;
import com.fich.sarh.auth.domain.model.RoleDTO;
import com.fich.sarh.auth.domain.model.RoleEnum;
import com.fich.sarh.auth.domain.model.UserDTO;
import com.fich.sarh.common.exceptions.BusinessRuleViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Seguridad — Mitigación de Escalada de Privilegios en UserUpdateUseCase")
class UserUpdateUseCaseSecurityTest {

    @Mock
    private UserRetrieveSpiPort userRetrieveSpiPort;

    @Mock
    private UserSaveSpiPort userSaveSpiPort;

    @InjectMocks
    private UserUpdateUseCase useCase;

    private UserDTO existingUser;

    @BeforeEach
    void setUp() {
        existingUser = UserDTO.builder()
                .id(1L)
                .username("juan")
                .email("juan@test.com")
                .roles(Set.of(RoleDTO.builder().id(1L).roleEnum(RoleEnum.USER).build()))
                .build();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "juan",
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                )
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Un usuario intentando modificar sus roles debe ser rechazado")
    void intentoDeModificarRolesDebeLanzarExcepcion() {
        when(userRetrieveSpiPort.findUserById(1L)).thenReturn(Optional.of(existingUser));

        UserDTO dtoConRoles = UserDTO.builder()
                .id(1L)
                .username("juan")
                .email("juan@test.com")
                .roles(Set.of(RoleDTO.builder().id(2L).roleEnum(RoleEnum.ADMIN).build()))
                .build();

        assertThatThrownBy(() -> useCase.updateUser(1L, dtoConRoles))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageStartingWith("No tiene permisos para modificar los roles asignados");

        verify(userSaveSpiPort, never()).saveUsername(any());
    }

    @Test
    @DisplayName("Un usuario puede actualizar su email y perfil sin tocar roles")
    void actualizacionValidaSinRolesDebeTenerExito() {
        when(userRetrieveSpiPort.findUserById(1L)).thenReturn(Optional.of(existingUser));
        when(userRetrieveSpiPort.existsByEmailAndIdNot("nuevo@test.com", 1L)).thenReturn(false);
        when(userSaveSpiPort.saveUsername(any(UserDTO.class))).thenAnswer(inv -> inv.getArgument(0));

        UserDTO dtoValido = UserDTO.builder()
                .id(1L)
                .email("nuevo@test.com")
                .profilePicturePath("/uploads/profile-pictures/foto.jpg")
                .build();

        UserDTO resultado = useCase.updateUser(1L, dtoValido);

        assertThat(resultado).isNotNull();
        assertThat(existingUser.getEmail()).isEqualTo("nuevo@test.com");
        assertThat(existingUser.getProfilePicturePath()).isEqualTo("/uploads/profile-pictures/foto.jpg");
        verify(userSaveSpiPort).saveUsername(existingUser);
    }
}
