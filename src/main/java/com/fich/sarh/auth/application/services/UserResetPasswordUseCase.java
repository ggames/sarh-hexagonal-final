package com.fich.sarh.auth.application.services;

import com.fich.sarh.auth.application.ports.entrypoint.api.UserResetPasswordApiPort;
import com.fich.sarh.auth.application.ports.output.persistence.UserResetPasswordSpiPort;
import com.fich.sarh.common.UseCase;

import java.util.Optional;

@UseCase
public class UserResetPasswordUseCase implements UserResetPasswordApiPort {

    private final UserResetPasswordSpiPort userResetPasswordSpiPort;

    public UserResetPasswordUseCase(UserResetPasswordSpiPort userResetPasswordSpiPort) {
        this.userResetPasswordSpiPort = userResetPasswordSpiPort;
    }

    @Override
    public Optional<String> resetPasswordByAdmin(Long userId) {
        return userResetPasswordSpiPort.resetPasswordByAdmin(userId);
    }

    @Override
    public boolean changePassword(Long userId, String currentPassword, String newPassword) {
        return userResetPasswordSpiPort.changePassword(userId, currentPassword, newPassword);
    }
}
