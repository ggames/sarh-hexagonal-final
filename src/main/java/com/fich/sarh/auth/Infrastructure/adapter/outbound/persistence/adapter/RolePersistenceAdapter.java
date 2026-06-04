package com.fich.sarh.auth.Infrastructure.adapter.outbound.persistence.adapter;

import com.fich.sarh.auth.Domain.model.RoleDTO;
import com.fich.sarh.auth.Domain.ports.outbound.RoleSpiPort;
import com.fich.sarh.auth.Infrastructure.adapter.outbound.persistence.entities.RoleEnum;
import com.fich.sarh.auth.Infrastructure.adapter.outbound.persistence.mapper.RoleMapper;
import com.fich.sarh.auth.Infrastructure.adapter.outbound.persistence.repository.RoleRepository;
import com.fich.sarh.common.WebAdapter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@WebAdapter
@RequiredArgsConstructor
public class RolePersistenceAdapter implements RoleSpiPort {

    private final RoleRepository roleRepository;
    private final RoleMapper mapper;

    @Override
    public RoleDTO findByRoleEnum(RoleEnum name) {

        return mapper.toDto(roleRepository.findByRoleEnum(name));
    }

    @Override
    public List<RoleDTO> findAllRole() {
        return roleRepository.findAll().stream()
                .map(mapper::toDto).toList();
    }

    @Override
    public Set<RoleDTO> findRoleEntitiesByRoleEnumIn(Set<String> roleNames) {
        return roleRepository.findRoleEntitiesByRoleEnumIn(roleNames)
                .stream().map(mapper::toDto).collect(Collectors.toSet());
    }
}
