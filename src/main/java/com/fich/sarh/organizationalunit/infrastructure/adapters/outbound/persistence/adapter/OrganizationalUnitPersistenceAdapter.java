package com.fich.sarh.organizationalunit.infrastructure.adapters.outbound.persistence.adapter;

import com.fich.sarh.common.PersistenceAdapter;
import com.fich.sarh.common.exceptions.ResourceNotFoundException;
import com.fich.sarh.organizationalunit.domain.model.OrganizationalDTO;
import com.fich.sarh.organizationalunit.domain.model.OrganizationalUnit;
import com.fich.sarh.organizationalunit.domain.ports.outbound.OrganizationalunitSpiPort;
import com.fich.sarh.organizationalunit.infrastructure.adapters.outbound.persistence.entity.OrganizationalUnitEntity;
import com.fich.sarh.organizationalunit.infrastructure.adapters.outbound.persistence.mapper.OrganizationalUnitMapper;
import com.fich.sarh.organizationalunit.infrastructure.adapters.outbound.persistence.repository.OrganizationalUnitRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@PersistenceAdapter
@RequiredArgsConstructor
public class OrganizationalUnitPersistenceAdapter implements OrganizationalunitSpiPort {

    private final OrganizationalUnitRepository organizationalRepository;
    private final OrganizationalUnitMapper mapper;



    @Override
    public List<OrganizationalUnit> findAllOrganizationalUnits() {
        return organizationalRepository.findAll()
                 .stream().map(mapper::toDto).toList();
    }

    @Override
    public Optional<OrganizationalUnit> findOrganizationalunitById(Long id) {
        OrganizationalUnitEntity entity = organizationalRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Departamento de materia no encontrado"));
        return Optional.of( mapper.toDto(entity));
    }

    @Override
    public List<OrganizationalDTO> findAllOrganizationDto() {
        return organizationalRepository.findOrganizationalAll();
    }

    @Override
    public List<OrganizationalUnit> findByNameUnit(String unit) {

        return organizationalRepository.findByNameUnit(unit)
                .stream().map(mapper::toDto).toList();
    }

    @Override
    public OrganizationalUnit saveOrganizationUnit(OrganizationalUnit organizational) {
        OrganizationalUnitEntity entity = mapper.toEntity(organizational);
        return  mapper.toDto(organizationalRepository.save(entity));
    }

}
