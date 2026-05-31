package com.fich.sarh.planthistory.domain.ports.inbound;

import com.fich.sarh.planthistory.domain.model.PlantHistory;
import com.fich.sarh.planthistory.infrastructure.adapters.inbound.rest.model.request.PlantHistoryRequest;

import java.util.List;
import java.util.Optional;

public interface PlantHistoryApiPort {
    List<PlantHistory> findAllPlantHistory();
    List<PlantHistory> findPlantHistoryByPlantId(Long id);
    Optional<PlantHistory> findPlantHistoryById(Long id);
    Optional<PlantHistory> findPlantByIdActive(Long plantId);
    PlantHistory  findTopByPlantIdOrderHistoryIdDesc(Long plantId);
    PlantHistory savePlantHistory(PlantHistoryRequest plantHistory);
    PlantHistory updatePlantHistory(Long id, PlantHistoryRequest plantHistory);
}
