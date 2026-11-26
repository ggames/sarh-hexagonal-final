package com.fich.sarh.auth.application.services;

import com.fich.sarh.auth.application.ports.entrypoint.api.UserManagerApiPort;
import com.fich.sarh.auth.application.ports.output.persistence.UserSaveSpiPort;
import com.fich.sarh.auth.domain.model.RoleDTO;
import com.fich.sarh.auth.domain.model.UserDTO;
import com.fich.sarh.auth.application.model.UserRequest;
import com.fich.sarh.auth.application.model.AuthResponse;
import com.fich.sarh.common.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@UseCase
@RequiredArgsConstructor
public class UserManagerUseCase implements UserManagerApiPort {

    private final UserSaveSpiPort userSave;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse createUser(UserRequest request) {
        Set<RoleDTO> roles = request.getRoles() != null
                ? request.getRoles().stream()
                    .map(r -> RoleDTO.builder().id(r.getId()).roleEnum(r.getRoleEnum()).build())
                    .collect(Collectors.toSet())
                : Collections.emptySet();

        Set<String> roleNames = roles.stream()
                .map(r -> r.getRoleEnum().name())
                .collect(Collectors.toSet());

        UserDTO user = UserDTO.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .roles(roles)
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialNonExpired(true)
                .build();

        UserDTO savedUser = userSave.saveUsername(user);

        return new AuthResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                "USER_CREATED",
                null,
                null,
                true,
                roleNames,
                true
        );
    }
}
