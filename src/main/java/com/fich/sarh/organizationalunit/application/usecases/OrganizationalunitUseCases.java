package com.fich.sarh.organizationalunit.application.usecases;

import com.fich.sarh.common.UseCase;
import com.fich.sarh.common.exceptions.ResourceNotFoundException;
import com.fich.sarh.organizationalunit.domain.model.OrganizationalDTO;
import com.fich.sarh.organizationalunit.domain.model.OrganizationalUnit;
import com.fich.sarh.organizationalunit.domain.ports.inbound.OrganizationalunitApiPort;
import com.fich.sarh.organizationalunit.domain.ports.outbound.OrganizationalunitSpiPort;
import com.fich.sarh.organizationalunit.infrastructure.adapters.inbound.rest.mapper.OrganizationalUnitRestMapper;
import com.fich.sarh.organizationalunit.infrastructure.adapters.inbound.rest.model.request.OrganizationalUnitRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@UseCase
@RequiredArgsConstructor
public class OrganizationalunitUseCases implements OrganizationalunitApiPort {

    private final OrganizationalunitSpiPort organizationalSpiPort;
    private final OrganizationalUnitRestMapper mapperRest;

    @Override
    public List<OrganizationalUnit> findAllOrganizationalUnits() {
        return organizationalSpiPort.findAllOrganizationalUnits();
    }

    @Override
    public Optional<OrganizationalUnit> findOrganizationalunitById(Long id) {
        return organizationalSpiPort.findOrganizationalunitById(id);
    }

    @Override
    public List<OrganizationalDTO> findAllOrganizationDto() {
        return organizationalSpiPort.findAllOrganizationDto();
    }

    @Override
    public List<OrganizationalUnit> findByNameUnit(String unit) {
        return organizationalSpiPort.findByNameUnit(unit);
    }

    @Override
    public OrganizationalUnit saveOrganizationUnit(OrganizationalUnitRequest organizational) {
        if(organizational == null){
            throw new ResourceNotFoundException("No hay información del departamento");
        }

        OrganizationalUnit organizationalUnit = mapperRest.toOrganizationalUnit(organizational);
        return organizationalSpiPort.saveOrganizationUnit(organizationalUnit);
    }

    @Override @Transactional
    public OrganizationalUnit updateOrganizationUnit(Long id, OrganizationalUnitRequest organizational) {
        OrganizationalUnit  organizationalUnit = organizationalSpiPort.findOrganizationalunitById(id)
                .orElseThrow(()-> new ResourceNotFoundException("No hay información de departamento"));
        organizationalUnit.setNameUnit(organizational.getNameUnit());
        organizationalUnit.setDirector(organizational.getDirector());
        organizationalUnit.setViceDirector(organizational.getViceDirector());

        return organizationalSpiPort.saveOrganizationUnit(organizationalUnit);
    }
}
