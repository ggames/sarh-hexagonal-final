package com.fich.sarh.auth.Infrastructure.adapter.inbound.rest.controller;

import com.fich.sarh.auth.Domain.ports.inbound.RoleApiPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("role")
@RequiredArgsConstructor
public class RoleController {

    //private final RoleRetrieveSpiPort roleRetrieveSpiPort;
      private final RoleApiPort roleApiPort;

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("all")
    ResponseEntity<?> findAll() {

        return ResponseEntity.ok().body(this.roleApiPort.findAllRole());
    }
}
