package com.fich.sarh.planthistory.domain.ports.inbound;

import com.fich.sarh.planthistory.domain.model.PlantHistory;

import java.util.List;
import java.util.Optional;

public interface PlantHistoryApiPort {
    List<PlantHistory> findAllPlantHistory();
    List<PlantHistory> findPlantHistoryByPlantId(Long id);
    Optional<PlantHistory> findPlantHistoryById(Long id);
    Optional<PlantHistory> findPlantByIdActive(Long plantId);
    PlantHistory  findTopByPlantIdOrderHistoryIdDesc(Long plantId);
    PlantHistory savePlantHistory(PlantHistory plantHistory);
    PlantHistory updatePlantHistory(Long id, PlantHistory plantHistory);
    boolean existsByPlantId(Long plantId);
    void deletePlantHistory(Long id);
}
