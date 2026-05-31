package com.fich.sarh.organizationalsubunit.infrastructure.adapter.outbound.persistence.adapter;

import com.fich.sarh.common.PersistenceAdapter;
import com.fich.sarh.organizationalsubunit.domain.model.OrganizationalSubUnit;
import com.fich.sarh.organizationalsubunit.domain.model.OrganizationalSubUnitDTO;
import com.fich.sarh.organizationalsubunit.domain.ports.outbound.OrganizationalSubunitSpiPort;
import com.fich.sarh.organizationalsubunit.infrastructure.adapter.outbound.persistence.entity.OrganizationalSubUnitEntity;
import com.fich.sarh.organizationalsubunit.infrastructure.adapter.outbound.persistence.mapper.OrganizationalSubUnitMapper;
import com.fich.sarh.organizationalsubunit.infrastructure.adapter.outbound.persistence.repository.OrganizationalSubUnitRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@PersistenceAdapter
@RequiredArgsConstructor
public class OrganizationalSubUnitPersistenceAdapter implements OrganizationalSubunitSpiPort {

    private final OrganizationalSubUnitRepository subunitRepository;
    private final OrganizationalSubUnitMapper mapper;


    Logger logger = LoggerFactory.getLogger(OrganizationalSubUnitPersistenceAdapter.class);


    @Override
    public List<OrganizationalSubUnitDTO> findAllOrganizationalSubUnitDTOs() {
        return subunitRepository.findOrganizationalSubUnitDTO();
    }

    @Override
    public List<OrganizationalSubUnit> findAllOrganizationalSubUnits() {
        return subunitRepository.findAll().stream()
                 .map(mapper::toOrganizationalSubUnit).toList();
    }

    @Override
    public Optional<OrganizationalSubUnit> findOrganizationalSubunitById(Long id) {
        return subunitRepository.findById(id).map(mapper::toOrganizationalSubUnit);
    }

    @Override
    public List<OrganizationalSubUnit> findByName(String unit) {

        return subunitRepository.findByNameSubUnit(unit).stream()
                .map(mapper::toOrganizationalSubUnit).toList();
    }

    @Override
    public OrganizationalSubUnit saveOrganizationSubunit(OrganizationalSubUnit request) {
        OrganizationalSubUnitEntity entity = mapper.toOrganizationalSubUnitEntity(request);
        return mapper.toOrganizationalSubUnit(subunitRepository.save(entity));
    }




}
