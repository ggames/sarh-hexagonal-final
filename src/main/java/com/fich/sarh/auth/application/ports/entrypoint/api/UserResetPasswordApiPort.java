package com.fich.sarh.auth.application.ports.entrypoint.api;

import java.util.Optional;

public interface UserResetPasswordApiPort {
    Optional<String> resetPasswordByAdmin(Long userId);
    boolean changePassword(Long userId, String currentPassword, String newPassword);
}
