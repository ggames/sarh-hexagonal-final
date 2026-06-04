package com.fich.sarh.auth.Domain.ports.inbound;

import com.fich.sarh.auth.Domain.model.RoleDTO;
import com.fich.sarh.auth.Infrastructure.adapter.outbound.persistence.entities.RoleEnum;

import java.util.List;
import java.util.Set;

public interface RoleApiPort {
    RoleDTO findByRoleEnum(RoleEnum name);

    List<RoleDTO> findAllRole();

    Set<RoleDTO> findRoleEntitiesByRoleEnumIn(Set<String> roleNames);

}
