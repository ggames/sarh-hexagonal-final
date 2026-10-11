package com.fich.sarh.auth.application.ports.output.persistence;

import java.util.Optional;

public interface UserResetPasswordSpiPort {
    Optional<String> resetPasswordByAdmin(Long userId);
    boolean changePassword(Long userId, String currentPassword, String newPassword);

}
