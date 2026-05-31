package com.fich.sarh.plantofpositions.domain.ports.outbound;

import com.fich.sarh.plantofpositions.domain.model.PlantFilter;
import com.fich.sarh.plantofpositions.domain.model.PlantOfPosition;
import com.fich.sarh.plantofpositions.domain.model.PlantOfPositionDto;
import com.fich.sarh.plantofpositions.domain.model.PlantProjectionDTO;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;

public interface PlantPositionSpiPort {
    ByteArrayInputStream createExcel(List<PlantProjectionDTO> plants);
    List<PlantOfPositionDto> findAllPlantPositions();
    Optional<PlantOfPosition> findPlantPositionById(Long id);
    PlantOfPosition savePlantOfPosition(PlantOfPosition request);
    List<PlantProjectionDTO> search(PlantFilter filter);
    PlantOfPosition updatePlantOfPosition(PlantOfPosition request);
    boolean existsByPositionAndAgent(Long positionId, Long agentId);

}
