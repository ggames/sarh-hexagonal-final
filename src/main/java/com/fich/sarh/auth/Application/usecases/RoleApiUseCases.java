package com.fich.sarh.auth.Application.usecases;

import com.fich.sarh.auth.Domain.model.RoleDTO;
import com.fich.sarh.auth.Domain.ports.inbound.RoleApiPort;
import com.fich.sarh.auth.Domain.ports.outbound.RoleSpiPort;
import com.fich.sarh.auth.Infrastructure.adapter.outbound.persistence.entities.RoleEnum;
import com.fich.sarh.common.UseCase;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;

@UseCase
@RequiredArgsConstructor
public class RoleApiUseCases implements RoleApiPort {

    private final RoleSpiPort roleSpiPort;

    @Override
    public RoleDTO findByRoleEnum(RoleEnum name) {
        return roleSpiPort.findByRoleEnum(name);
    }

    @Override
    public List<RoleDTO> findAllRole() {
        return roleSpiPort.findAllRole();
    }

    @Override
    public Set<RoleDTO> findRoleEntitiesByRoleEnumIn(Set<String> roleNames) {
        return roleSpiPort.findRoleEntitiesByRoleEnumIn(roleNames);
    }
}
