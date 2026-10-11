package com.fich.sarh.auth.application.services;

import com.fich.sarh.auth.application.ports.entrypoint.api.RoleRetrieveApiPort;
import com.fich.sarh.auth.application.ports.output.persistence.RoleRetrieveSpiPort;
import com.fich.sarh.auth.domain.model.RoleDTO;
import com.fich.sarh.auth.infrastructure.adapter.output.persistence.entities.RoleEnum;
import com.fich.sarh.common.UseCase;

import java.util.List;

@UseCase
public class RoleRetrieveUseCase implements RoleRetrieveApiPort {

    private final RoleRetrieveSpiPort roleRetrieveSpiPort;

    public RoleRetrieveUseCase(RoleRetrieveSpiPort roleRetrieveSpiPort) {
        this.roleRetrieveSpiPort = roleRetrieveSpiPort;
    }

    @Override
    public RoleDTO fetchByRoleEnum(RoleEnum name) {
        return roleRetrieveSpiPort.fetchByRoleEnum(name);
    }

    @Override
    public List<RoleDTO> fetchAllRole() {
        return roleRetrieveSpiPort.fetchAllRole();
    }
}
