package com.fich.sarh.organizationalsubunit.application.usecases;

import com.fich.sarh.common.UseCase;
import com.fich.sarh.common.exceptions.ResourceNotFoundException;
import com.fich.sarh.organizationalsubunit.domain.model.OrganizationalSubUnit;
import com.fich.sarh.organizationalsubunit.domain.model.OrganizationalSubUnitDTO;
import com.fich.sarh.organizationalsubunit.domain.ports.inbound.OrganizationalSubunitApiPort;
import com.fich.sarh.organizationalsubunit.domain.ports.outbound.OrganizationalSubunitSpiPort;
import com.fich.sarh.organizationalsubunit.infrastructure.adapter.inbound.rest.mapper.OrganizationalSubUnitRestMapper;
import com.fich.sarh.organizationalsubunit.infrastructure.adapter.inbound.rest.model.request.OrganizationalSubunitRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@UseCase
@RequiredArgsConstructor
public class OrganizationalSubunitUseCases implements OrganizationalSubunitApiPort {

    private final OrganizationalSubunitSpiPort subunitSpiPort;
    private final OrganizationalSubUnitRestMapper mapperRest;

    @Override
    public List<OrganizationalSubUnitDTO> findAllOrganizationalSubUnitDTOs() {

        return subunitSpiPort.findAllOrganizationalSubUnitDTOs();
    }

    @Override
    public List<OrganizationalSubUnit> findAllOrganizationalSubUnits() {
        return subunitSpiPort.findAllOrganizationalSubUnits();
    }

    @Override
    public Optional<OrganizationalSubUnit> findOrganizationalSubunitById(Long id) {
        return subunitSpiPort.findOrganizationalSubunitById(id);
    }

    @Override
    public List<OrganizationalSubUnit> findByName(String unit) {

        return subunitSpiPort.findByName(unit);

    }


    @Override
    public OrganizationalSubUnit saveOrganizationSubunit(OrganizationalSubunitRequest request) {
        OrganizationalSubUnit organizationalSubUnit = mapperRest.toOrganizationalSubUnit(request);
        return subunitSpiPort.saveOrganizationSubunit(organizationalSubUnit);
    }

    @Override
    @Transactional
    public OrganizationalSubUnit updateOrganizationSubunit(Long id, OrganizationalSubUnit request) {
            OrganizationalSubUnit subUnit = subunitSpiPort.findOrganizationalSubunitById(id).orElseThrow(()->
                    new ResourceNotFoundException("No existe el departamento seleccionado"));

            subUnit.setOrganizationalUnit(request.getOrganizationalUnit());
            subUnit.setGuaraniCode(request.getGuaraniCode());
            subUnit.setNameSubUnit(request.getNameSubUnit());


            return subunitSpiPort.saveOrganizationSubunit(subUnit);
    }
}
