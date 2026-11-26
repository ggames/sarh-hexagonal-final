package com.fich.sarh.plantofpositions.domain.ports.inbound;

import com.fich.sarh.plantofpositions.domain.model.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;

public interface PlantPositionApiPort {
    ByteArrayInputStream createExcel(List<PlantProjectionDTO> plants);
    void writeExcel(List<PlantProjectionDTO> plants, OutputStream out) throws IOException;
    List<PlantOfPositionDto> findAllPlantPositions();
    Optional<PlantOfPosition> findPlantPositionById(Long id);
    PlantOfPosition addPlantOfPosition(PlantOfPositionCommand request);
    List<PlantProjectionDTO> search(PlantFilter filter);
    PlantOfPosition updatePlantOfPosition(Long id, PlantOfPosition request);
    boolean existsByAgentId(Long agentId);
    boolean existsByPositionId(Long positionId);
    boolean existsBySubUnitId(Long subUnitId);
    void deletePlantPosition(Long id);

}
