package com.fich.sarh.auth.infrastructure.adapter.input.rest.controller;

import com.fich.sarh.auth.application.ports.entrypoint.api.RoleRetrieveApiPort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("role")
public class RoleController {

    private final RoleRetrieveApiPort roleRetrieveApiPort;

    public RoleController(RoleRetrieveApiPort roleRetrieveApiPort) {
        this.roleRetrieveApiPort = roleRetrieveApiPort;
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("all")
    ResponseEntity<?> findAll() {
       return ResponseEntity.ok().body(this.roleRetrieveApiPort.fetchAllRole());
    }
}
