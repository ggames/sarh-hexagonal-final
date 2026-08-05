package com.fich.sarh.auth.Infrastructure.adapter.inbound.rest.controller;

import com.fich.sarh.auth.Domain.ports.inbound.UserApiPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/admin")
@RestController
@RequiredArgsConstructor
public class AdminController {


    // private final UserResetPasswordSpiPort userResetPasswordSpiPort;
    private final UserApiPort userApiPort;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("user/{id}/reset-password")
    public ResponseEntity<?> resetPassword(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(
                userApiPort.resetPasswordByAdmin(id)
        );
        //return userApiPort.resetPasswordByAdmin(id)
        //        .map(temp ->

        //            ResponseEntity.ok().body("TempPassword: " + temp)  )
        //        .orElseGet(()-> ResponseEntity.notFound().build());
    }


}
