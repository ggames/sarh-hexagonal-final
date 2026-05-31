package com.fich.sarh.plantofpositions.application.usecases;

import com.fich.sarh.agent.domain.ports.outbound.AgentSpiPort;
import com.fich.sarh.common.UseCase;
import com.fich.sarh.common.exceptions.ResourceNotFoundException;
import com.fich.sarh.organizationalsubunit.domain.ports.outbound.OrganizationalSubunitSpiPort;
import com.fich.sarh.plantofpositions.application.factory.PlantPositionFactory;
import com.fich.sarh.plantofpositions.application.handlers.MovementHandler;
import com.fich.sarh.plantofpositions.application.handlers.PlantHistoryHandler;
import com.fich.sarh.plantofpositions.domain.model.*;
import com.fich.sarh.plantofpositions.domain.ports.inbound.PlantPositionApiPort;
import com.fich.sarh.plantofpositions.domain.ports.outbound.PlantPositionSpiPort;
import com.fich.sarh.plantofpositions.infrastructure.adapters.inbound.rest.mapper.PlantOfPositionRestMapper;
import com.fich.sarh.plantofpositions.infrastructure.adapters.inbound.rest.model.request.PlantOfPositionRequest;
import com.fich.sarh.plantofpositions.infrastructure.adapters.inbound.rest.model.response.PlantOfPositionResponse;
import com.fich.sarh.plantofpositions.infrastructure.adapters.outbound.persistence.service.PlantPositionService;
import com.fich.sarh.position.domain.ports.outbound.PositionSpiPort;
import lombok.RequiredArgsConstructor;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;

@UseCase
@RequiredArgsConstructor
public class PlantPositionUseCases implements PlantPositionApiPort {

    private final PlantPositionSpiPort plantPositionSpiPort;
    private final PositionSpiPort positionSpiPort;
    private final AgentSpiPort agentSpiPort;
    private final OrganizationalSubunitSpiPort subunitSpiPort;

    private final PlantOfPositionRestMapper restMapper;
    private final PlantPositionService plantPositionService;

    private final PlantPositionFactory factory;
    private final MovementHandler movementHandler;
    private final PlantHistoryHandler plantHistoryHandler;

    @Override
    public ByteArrayInputStream createExcel(List<PlantProjectionDTO> plants) {

        return plantPositionSpiPort.createExcel(plants);
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
    public PlantOfPositionResponse addPlantOfPosition(PlantOfPositionCommand request) {

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

        return restMapper.toPlantOfPositionResponse(plant);
    }

    @Override
    public List<PlantProjectionDTO> search(PlantFilter filter) {
        return plantPositionSpiPort.search(filter);
    }

    @Override
    public PlantOfPositionResponse updatePlantOfPosition(Long id, PlantOfPositionRequest request) {
        var entity = plantPositionSpiPort.findPlantPositionById(id)
                .orElseThrow(()-> new ResourceNotFoundException("La plaza seleccionada no fue encontrada"));
        entity.setPosition(request.getPosition());
        entity.setAgent(request.getAgent());
        entity.setCharacterplantID(request.getCharacterplantID());
        entity.setCurrentStatusID(request.getCurrentStatusID());
        entity.setOrganizationalSubUnit(request.getOrganizationalSubUnit());
        return null;
    }
}
