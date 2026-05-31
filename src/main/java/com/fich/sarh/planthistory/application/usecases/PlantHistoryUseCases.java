package com.fich.sarh.planthistory.application.usecases;

import com.fich.sarh.common.UseCase;
import com.fich.sarh.common.exceptions.ResourceNotFoundException;
import com.fich.sarh.planthistory.domain.model.PlantHistory;
import com.fich.sarh.planthistory.domain.ports.inbound.PlantHistoryApiPort;
import com.fich.sarh.planthistory.domain.ports.outbound.PlantHistorySpiPort;
import com.fich.sarh.planthistory.infrastructure.adapters.inbound.rest.mapper.PlantHistoryRestMapper;
import com.fich.sarh.planthistory.infrastructure.adapters.inbound.rest.model.request.PlantHistoryRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@UseCase
@RequiredArgsConstructor
public class PlantHistoryUseCases implements PlantHistoryApiPort {

    private final PlantHistorySpiPort plantHistorySpiPort;
    private final PlantHistoryRestMapper mapperRest;

    @Override
    public List<PlantHistory> findAllPlantHistory() {
        return plantHistorySpiPort.findAllPlantHistory();
    }

    @Override
    public List<PlantHistory> findPlantHistoryByPlantId(Long id) {

        return plantHistorySpiPort.findPlantHistoryByPlantId(id);
    }

    @Override
    public Optional<PlantHistory> findPlantHistoryById(Long id) {

        return plantHistorySpiPort.findPlantHistoryById(id);
    }

    @Override
    public Optional<PlantHistory> findPlantByIdActive(Long plantId) {

        return plantHistorySpiPort.findPlantByIdActive(plantId);
    }

    @Override
    public PlantHistory findTopByPlantIdOrderHistoryIdDesc(Long plantId) {

        return plantHistorySpiPort.findTopByPlantIdOrderHistoryIdDesc(plantId);
    }

    @Override
    public PlantHistory savePlantHistory(PlantHistoryRequest plantHistory) {
        PlantHistory history = mapperRest.toPlantHistory(plantHistory);
        return plantHistorySpiPort.savePlantHistory(history);
    }

    @Override @Transactional
    public PlantHistory updatePlantHistory(Long id, PlantHistoryRequest plantHistory) {
        PlantHistory history = plantHistorySpiPort.findPlantHistoryById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Historia de planta no encontrada"));

        history.setPlantStatus(plantHistory.getPlantStatus());
        history.setPlantOfPosition(plantHistory.getPlantOfPosition());
        history.setDateFrom(plantHistory.getDateFrom());
        history.setDateTo(plantHistory.getDateTo());
        return plantHistorySpiPort.savePlantHistory(history);
    }
}
