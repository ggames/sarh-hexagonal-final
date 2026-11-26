package com.fich.sarh.organizationalsubunit.application.usecases;

import com.fich.sarh.common.UseCase;
import com.fich.sarh.common.exceptions.BusinessRuleViolationException;
import com.fich.sarh.common.exceptions.ResourceNotFoundException;
import com.fich.sarh.organizationalsubunit.domain.model.OrganizationalSubUnit;
import com.fich.sarh.organizationalsubunit.domain.model.OrganizationalSubUnitDTO;
import com.fich.sarh.organizationalsubunit.domain.ports.inbound.OrganizationalSubunitApiPort;
import com.fich.sarh.organizationalsubunit.domain.ports.outbound.OrganizationalSubunitSpiPort;
import com.fich.sarh.plantofpositions.domain.ports.inbound.PlantPositionApiPort;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Lazy;

import java.util.List;
import java.util.Optional;

@UseCase
public class OrganizationalSubunitUseCases implements OrganizationalSubunitApiPort {

    private final OrganizationalSubunitSpiPort subunitSpiPort;
    private final PlantPositionApiPort plantPositionApiPort;

    public OrganizationalSubunitUseCases(
            OrganizationalSubunitSpiPort subunitSpiPort,
            @Lazy PlantPositionApiPort plantPositionApiPort) {
        this.subunitSpiPort = subunitSpiPort;
        this.plantPositionApiPort = plantPositionApiPort;
    }

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
    @Transactional
    public OrganizationalSubUnit saveOrganizationSubunit(OrganizationalSubUnit request) {
        return subunitSpiPort.saveOrganizationSubunit(request);
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

    @Override
    public boolean existsByOrganizationalUnitId(Long organizationalUnitId) {
        return subunitSpiPort.existsByOrganizationalUnitId(organizationalUnitId);
    }

    @Override
    @Transactional
    public void deleteOrganizationalSubunit(Long id) {
        subunitSpiPort.findOrganizationalSubunitById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subunidad/Materia no encontrada con ID: " + id));

        if (plantPositionApiPort.existsBySubUnitId(id)) {
            throw new BusinessRuleViolationException("No se puede eliminar la materia/subunidad porque está asignada a cargos en la planta.");
        }

        subunitSpiPort.deleteOrganizationalSubunit(id);
    }
}
