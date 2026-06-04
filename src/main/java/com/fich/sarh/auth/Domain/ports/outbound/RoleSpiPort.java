package com.fich.sarh.auth.Domain.ports.outbound;

import com.fich.sarh.auth.Domain.model.RoleDTO;
import com.fich.sarh.auth.Infrastructure.adapter.outbound.persistence.entities.RoleEnum;

import java.util.List;
import java.util.Set;

public interface RoleSpiPort {
    RoleDTO findByRoleEnum(RoleEnum name);
    List<RoleDTO> findAllRole();
    Set<RoleDTO> findRoleEntitiesByRoleEnumIn(Set<String> roleNames);

}
