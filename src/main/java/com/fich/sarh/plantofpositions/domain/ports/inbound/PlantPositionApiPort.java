package com.fich.sarh.plantofpositions.domain.ports.inbound;

import com.fich.sarh.plantofpositions.domain.model.*;
import com.fich.sarh.plantofpositions.infrastructure.adapters.inbound.rest.model.request.PlantOfPositionRequest;
import com.fich.sarh.plantofpositions.infrastructure.adapters.inbound.rest.model.response.PlantOfPositionResponse;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;

public interface PlantPositionApiPort {
    ByteArrayInputStream createExcel(List<PlantProjectionDTO> plants);
    List<PlantOfPositionDto> findAllPlantPositions();
    Optional<PlantOfPosition> findPlantPositionById(Long id);
    PlantOfPositionResponse addPlantOfPosition(PlantOfPositionCommand request);
    List<PlantProjectionDTO> search(PlantFilter filter);
    PlantOfPositionResponse updatePlantOfPosition(Long id, PlantOfPositionRequest request);

}
