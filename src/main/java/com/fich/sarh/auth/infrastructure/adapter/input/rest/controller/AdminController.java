package com.fich.sarh.auth.infrastructure.adapter.input.rest.controller;

import com.fich.sarh.auth.application.ports.entrypoint.api.UserResetPasswordApiPort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/admin")
@RestController
public class AdminController {

    private final UserResetPasswordApiPort userResetPasswordApiPort;

    public AdminController(UserResetPasswordApiPort userResetPasswordApiPort) {
        this.userResetPasswordApiPort = userResetPasswordApiPort;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("user/{id}/reset-password")
    public ResponseEntity<?> resetPassword(@PathVariable Long id){
        return userResetPasswordApiPort.resetPasswordByAdmin(id)
                .map(temp ->
                    ResponseEntity.ok().body(java.util.Map.of("message", "Contraseña restablecida con éxito")))
                .orElseGet(()-> ResponseEntity.notFound().build());
    }
}
