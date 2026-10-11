package com.fich.sarh.plantofpositions.domain.ports.inbound;

import com.fich.sarh.plantofpositions.domain.model.*;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;

public interface PlantPositionApiPort {
    ByteArrayInputStream createExcel(List<PlantProjectionDTO> plants);
    List<PlantOfPositionDto> findAllPlantPositions();
    Optional<PlantOfPosition> findPlantPositionById(Long id);
    PlantOfPosition addPlantOfPosition(PlantOfPositionCommand request);
    List<PlantProjectionDTO> search(PlantFilter filter);
    PlantOfPosition updatePlantOfPosition(Long id, PlantOfPosition request);

}
