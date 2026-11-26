package com.fich.sarh.auth.application.services;

import com.fich.sarh.auth.application.ports.entrypoint.api.UserUpdateApiPort;
import com.fich.sarh.auth.application.ports.output.persistence.UserRetrieveSpiPort;
import com.fich.sarh.auth.application.ports.output.persistence.UserSaveSpiPort;
import com.fich.sarh.auth.domain.model.UserDTO;
import com.fich.sarh.common.SecurityUtils;
import com.fich.sarh.common.UseCase;
import com.fich.sarh.common.exceptions.BusinessRuleViolationException;
import org.springframework.transaction.annotation.Transactional;

@UseCase
public class UserUpdateUseCase implements UserUpdateApiPort {

    private final UserRetrieveSpiPort userRetrieveSpiPort;
    private final UserSaveSpiPort userSaveSpiPort;

    public UserUpdateUseCase(UserRetrieveSpiPort userRetrieveSpiPort, UserSaveSpiPort userSaveSpiPort) {
        this.userRetrieveSpiPort = userRetrieveSpiPort;
        this.userSaveSpiPort = userSaveSpiPort;
    }

    @Override
    @Transactional
    public UserDTO updateUser(Long userId, UserDTO dto) {
        UserDTO existing = userRetrieveSpiPort.findUserById(userId).orElseThrow(
                () -> new BusinessRuleViolationException("Usuario no encontrado"));

        String loggedUsername = SecurityUtils.getCurrentUsername();

        if (loggedUsername == null) {
            throw new BusinessRuleViolationException("No hay sesión activa");
        }

        boolean isSameUser = existing.getUsername().equalsIgnoreCase(loggedUsername);

        if (!isSameUser) {
            throw new BusinessRuleViolationException(
                    "No tiene permisos para modificar este usuario"
            );
        }

        if (dto.getEmail() != null &&
                !dto.getEmail().equalsIgnoreCase(existing.getEmail()) &&
                userRetrieveSpiPort.existsByEmailAndIdNot(dto.getEmail(), userId)){
            throw new BusinessRuleViolationException("El email ya se encuentra registrado");
        }
        if (dto.getUsername() != null) {
            String newUsername = dto.getUsername().trim();
            String currentUsername = existing.getUsername();

            if (!newUsername.equalsIgnoreCase(currentUsername) &&
                    userRetrieveSpiPort.existsByUsernameIgnoreCaseAndIdNot(newUsername, userId)) {
                throw new BusinessRuleViolationException("El nombre de usuario ya se encuentra registrado");
            }
            existing.setUsername(newUsername);
        }

        if (dto.getRoles() != null && !dto.getRoles().isEmpty()) {
            throw new BusinessRuleViolationException("No tiene permisos para modificar los roles asignados");
        }

        if (dto.getEmail() != null) {
            existing.setEmail(dto.getEmail().trim());
        }

        if (dto.getProfilePicturePath() != null && !dto.getProfilePicturePath().isBlank()) {
            existing.setProfilePicturePath(dto.getProfilePicturePath());
        }

        return userSaveSpiPort.saveUsername(existing);
    }
}
