package com.fich.sarh.organizationalunit.application.usecases;

import com.fich.sarh.common.UseCase;
import com.fich.sarh.common.exceptions.BusinessRuleViolationException;
import com.fich.sarh.common.exceptions.ResourceNotFoundException;
import com.fich.sarh.organizationalunit.domain.model.OrganizationalDTO;
import com.fich.sarh.organizationalunit.domain.model.OrganizationalUnit;
import com.fich.sarh.organizationalunit.domain.ports.inbound.OrganizationalunitApiPort;
import com.fich.sarh.organizationalunit.domain.ports.outbound.OrganizationalunitSpiPort;
import com.fich.sarh.organizationalsubunit.domain.ports.inbound.OrganizationalSubunitApiPort;
import com.fich.sarh.position.domain.ports.inbound.PositionApiPort;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Lazy;

import java.util.List;
import java.util.Optional;

@UseCase
public class OrganizationalunitUseCases implements OrganizationalunitApiPort {

    private final OrganizationalunitSpiPort organizationalSpiPort;
    private final OrganizationalSubunitApiPort subunitApiPort;
    private final PositionApiPort positionApiPort;

    public OrganizationalunitUseCases(
            OrganizationalunitSpiPort organizationalSpiPort,
            @Lazy OrganizationalSubunitApiPort subunitApiPort,
            @Lazy PositionApiPort positionApiPort) {
        this.organizationalSpiPort = organizationalSpiPort;
        this.subunitApiPort = subunitApiPort;
        this.positionApiPort = positionApiPort;
    }

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
    @Transactional
    public OrganizationalUnit saveOrganizationUnit(OrganizationalUnit organizational) {
        if(organizational == null){
            throw new ResourceNotFoundException("No hay información del departamento");
        }
        return organizationalSpiPort.saveOrganizationUnit(organizational);
    }

    @Override
    @Transactional
    public OrganizationalUnit updateOrganizationUnit(Long id, OrganizationalUnit organizational) {
        OrganizationalUnit  organizationalUnit = organizationalSpiPort.findOrganizationalunitById(id)
                .orElseThrow(()-> new ResourceNotFoundException("No hay información de departamento"));
        organizationalUnit.setNameUnit(organizational.getNameUnit());
        organizationalUnit.setDirector(organizational.getDirector());
        organizationalUnit.setViceDirector(organizational.getViceDirector());
        organizationalUnit.validateDirectors();

        return organizationalSpiPort.saveOrganizationUnit(organizationalUnit);
    }

    @Override
    public boolean existsByDirectorOrViceDirectorId(Long agentId) {
        return organizationalSpiPort.existsByDirectorOrViceDirectorId(agentId);
    }

    @Override
    @Transactional
    public void deleteOrganizationalUnit(Long id) {
        organizationalSpiPort.findOrganizationalunitById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Departamento no encontrado con ID: " + id));

        if (subunitApiPort.existsByOrganizationalUnitId(id)) {
            throw new BusinessRuleViolationException("No se puede eliminar el departamento porque tiene materias/subunidades asociadas.");
        }

        if (positionApiPort.existsByOrganizationalUnitId(id)) {
            throw new BusinessRuleViolationException("No se puede eliminar el departamento porque tiene cargos asociados.");
        }

        organizationalSpiPort.deleteOrganizationalUnit(id);
    }
}
