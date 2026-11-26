package com.fich.sarh.plantofpositions.application.usecases;

import com.fich.sarh.agent.domain.ports.outbound.AgentSpiPort;
import com.fich.sarh.common.PlantStatus;
import com.fich.sarh.common.UseCase;
import com.fich.sarh.common.exceptions.BusinessRuleViolationException;
import com.fich.sarh.common.exceptions.ResourceNotFoundException;
import com.fich.sarh.movement.domain.ports.inbound.MovementApiPort;
import com.fich.sarh.organizationalsubunit.domain.ports.outbound.OrganizationalSubunitSpiPort;
import com.fich.sarh.planthistory.domain.ports.inbound.PlantHistoryApiPort;
import com.fich.sarh.plantofpositions.application.factory.PlantPositionFactory;
import com.fich.sarh.plantofpositions.application.handlers.MovementHandler;
import com.fich.sarh.plantofpositions.application.handlers.PlantHistoryHandler;
import com.fich.sarh.plantofpositions.domain.model.*;
import com.fich.sarh.plantofpositions.domain.ports.inbound.PlantPositionApiPort;
import com.fich.sarh.plantofpositions.domain.ports.outbound.PlantExportReportPort;
import com.fich.sarh.plantofpositions.domain.ports.outbound.PlantPositionSpiPort;
import com.fich.sarh.plantofpositions.domain.service.PlantPositionService;
import com.fich.sarh.position.domain.ports.outbound.PositionSpiPort;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Lazy;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;

@UseCase
public class PlantPositionUseCases implements PlantPositionApiPort {

    private final PlantPositionSpiPort plantPositionSpiPort;
    private final PositionSpiPort positionSpiPort;
    private final AgentSpiPort agentSpiPort;
    private final OrganizationalSubunitSpiPort subunitSpiPort;

    private final PlantPositionService plantPositionService;

    private final PlantPositionFactory factory;
    private final MovementHandler movementHandler;
    private final PlantHistoryHandler plantHistoryHandler;

    private final MovementApiPort movementApiPort;
    private final PlantHistoryApiPort plantHistoryApiPort;
    private final PlantExportReportPort plantExportReportPort;

    @org.springframework.beans.factory.annotation.Autowired
    public PlantPositionUseCases(
            PlantPositionSpiPort plantPositionSpiPort,
            PositionSpiPort positionSpiPort,
            AgentSpiPort agentSpiPort,
            OrganizationalSubunitSpiPort subunitSpiPort,
            PlantPositionService plantPositionService,
            PlantPositionFactory factory,
            MovementHandler movementHandler,
            PlantHistoryHandler plantHistoryHandler,
            @Lazy MovementApiPort movementApiPort,
            @Lazy PlantHistoryApiPort plantHistoryApiPort,
            java.util.Optional<PlantExportReportPort> plantExportReportPort) {
        this.plantPositionSpiPort = plantPositionSpiPort;
        this.positionSpiPort = positionSpiPort;
        this.agentSpiPort = agentSpiPort;
        this.subunitSpiPort = subunitSpiPort;
        this.plantPositionService = plantPositionService;
        this.factory = factory;
        this.movementHandler = movementHandler;
        this.plantHistoryHandler = plantHistoryHandler;
        this.movementApiPort = movementApiPort;
        this.plantHistoryApiPort = plantHistoryApiPort;
        this.plantExportReportPort = plantExportReportPort != null ? plantExportReportPort.orElse(null) : null;
    }

    public PlantPositionUseCases(
            PlantPositionSpiPort plantPositionSpiPort,
            PositionSpiPort positionSpiPort,
            AgentSpiPort agentSpiPort,
            OrganizationalSubunitSpiPort subunitSpiPort,
            PlantPositionService plantPositionService,
            PlantPositionFactory factory,
            MovementHandler movementHandler,
            PlantHistoryHandler plantHistoryHandler,
            MovementApiPort movementApiPort,
            PlantHistoryApiPort plantHistoryApiPort,
            PlantExportReportPort plantExportReportPort) {
        this(plantPositionSpiPort, positionSpiPort, agentSpiPort, subunitSpiPort,
             plantPositionService, factory, movementHandler, plantHistoryHandler,
             movementApiPort, plantHistoryApiPort, java.util.Optional.ofNullable(plantExportReportPort));
    }

    public PlantPositionUseCases(
            PlantPositionSpiPort plantPositionSpiPort,
            PositionSpiPort positionSpiPort,
            AgentSpiPort agentSpiPort,
            OrganizationalSubunitSpiPort subunitSpiPort,
            PlantPositionService plantPositionService,
            PlantPositionFactory factory,
            MovementHandler movementHandler,
            PlantHistoryHandler plantHistoryHandler,
            MovementApiPort movementApiPort,
            PlantHistoryApiPort plantHistoryApiPort) {
        this(plantPositionSpiPort, positionSpiPort, agentSpiPort, subunitSpiPort,
             plantPositionService, factory, movementHandler, plantHistoryHandler,
             movementApiPort, plantHistoryApiPort, (PlantExportReportPort) null);
    }

    @Override
    public ByteArrayInputStream createExcel(List<PlantProjectionDTO> plants) {
        if (plantExportReportPort != null) {
            return plantExportReportPort.createExcel(plants);
        }
        return new ByteArrayInputStream(new byte[0]);
    }

    @Override
    public void writeExcel(List<PlantProjectionDTO> plants, java.io.OutputStream out) throws java.io.IOException {
        if (plantExportReportPort == null) {
            throw new IllegalStateException("No hay un servicio de exportación de plantas configurado");
        }
        plantExportReportPort.writeExcel(plants, out);
    }

    @Override
    public List<PlantOfPositionDto> findAllPlantPositions() {

        return plantPositionSpiPort.findAllPlantPositions();
    }

    @Override
    public Optional<PlantOfPosition> findPlantPositionById(Long id) {

        return plantPositionSpiPort.findPlantPositionById(id);
    }

    @Override
    @Transactional
    public PlantOfPosition addPlantOfPosition(PlantOfPositionCommand request) {

       plantPositionService.validateActivePlant(request);

       var position = positionSpiPort.findPositionById(request.getPositionId())
               .orElseThrow(()-> new ResourceNotFoundException("No existe al cargo seleccionado"));

       var agent = agentSpiPort.findAgentById(request.getAgentId())
               .orElseThrow(()-> new ResourceNotFoundException("No existe el agente seleccionado"));

       var subunit = subunitSpiPort.findOrganizationalSubunitById(request.getOrganizationalSubUnit())
               .orElseThrow(()-> new ResourceNotFoundException("No existe la materia seleccionada"));

       var plant = factory.build(agent, position, subunit, request.getCurrentStatusID(),request.getCharacterplantID());

       plant = plantPositionSpiPort.savePlantOfPosition(plant);

       movementHandler.register(plant, position, request.getDateFrom(), request.getReasonForMovement());

       plantHistoryHandler.register(plant, request.getDateFrom(), request.getCurrentStatusID());

       return plant;
    }

    @Override
    public List<PlantProjectionDTO> search(PlantFilter filter) {
        return plantPositionSpiPort.search(filter);
    }

    @Override
    @Transactional
    public PlantOfPosition updatePlantOfPosition(Long id, PlantOfPosition request) {
        var entity = plantPositionSpiPort.findPlantPositionById(id)
                .orElseThrow(()-> new ResourceNotFoundException("La plaza seleccionada no fue encontrada"));

        if (entity.getCurrentStatusID() == PlantStatus.FINALIZADO && request.getCurrentStatusID() != PlantStatus.FINALIZADO) {
            throw new BusinessRuleViolationException("No se puede modificar una designación en estado finalizado");
        }

        entity.setPosition(request.getPosition());
        entity.setAgent(request.getAgent());
        entity.setCharacterplantID(request.getCharacterplantID());
        entity.setCurrentStatusID(request.getCurrentStatusID());
        entity.setOrganizationalSubUnit(request.getOrganizationalSubUnit());
        return plantPositionSpiPort.savePlantOfPosition(entity);
    }

    @Override
    public boolean existsByAgentId(Long agentId) {
        return plantPositionSpiPort.existsByAgentId(agentId);
    }

    @Override
    public boolean existsByPositionId(Long positionId) {
        return plantPositionSpiPort.existsByPositionId(positionId);
    }

    @Override
    public boolean existsBySubUnitId(Long subUnitId) {
        return plantPositionSpiPort.existsBySubUnitId(subUnitId);
    }

    @Override
    @Transactional
    public void deletePlantPosition(Long id) {
        plantPositionSpiPort.findPlantPositionById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Planta de cargo no encontrada con ID: " + id));

        if (movementApiPort.existsByPlantId(id)) {
            throw new BusinessRuleViolationException("No se puede eliminar el registro de planta porque tiene movimientos asociados.");
        }

        if (plantHistoryApiPort.existsByPlantId(id)) {
            throw new BusinessRuleViolationException("No se puede eliminar el registro de planta porque tiene historial de revista asociado.");
        }

        plantPositionSpiPort.deletePlantPosition(id);
    }
}
