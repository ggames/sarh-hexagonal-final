package com.fich.sarh.auth.infrastructure.adapter.output.persistence.adapter;

import com.fich.sarh.auth.application.ports.output.persistence.RoleRetrieveSpiPort;
import com.fich.sarh.auth.domain.model.RoleDTO;
import com.fich.sarh.auth.domain.model.RoleEnum;
import com.fich.sarh.auth.infrastructure.adapter.output.persistence.mapper.RoleMapper;
import com.fich.sarh.auth.infrastructure.adapter.output.persistence.repository.RoleRepository;
import com.fich.sarh.common.PersistenceAdapter;

import java.util.List;

@PersistenceAdapter
public class RolePersistenceAdapter implements RoleRetrieveSpiPort {

    private final RoleRepository roleRepository;

    public RolePersistenceAdapter(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public RoleDTO  fetchByRoleEnum(RoleEnum name) {

        return RoleMapper.INSTANCE.toDto(this.roleRepository.findByRoleEnum(name));
    }

    @Override
    public List<RoleDTO> fetchAllRole() {
        return RoleMapper.INSTANCE.toDtoList(this.roleRepository.findAll());
    }
}
