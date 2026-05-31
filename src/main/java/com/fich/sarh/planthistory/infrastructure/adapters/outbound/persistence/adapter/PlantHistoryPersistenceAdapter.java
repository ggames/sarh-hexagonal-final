package com.fich.sarh.planthistory.infrastructure.adapters.outbound.persistence.adapter;

import com.fich.sarh.common.PersistenceAdapter;
import com.fich.sarh.common.exceptions.BusinessRuleViolationException;
import com.fich.sarh.planthistory.domain.model.PlantHistory;
import com.fich.sarh.planthistory.domain.ports.outbound.PlantHistorySpiPort;
import com.fich.sarh.planthistory.infrastructure.adapters.outbound.persistence.entity.PlantHistoryEntity;
import com.fich.sarh.planthistory.infrastructure.adapters.outbound.persistence.mapper.PlantHistoryMapper;
import com.fich.sarh.planthistory.infrastructure.adapters.outbound.persistence.repository.PlantHistoryRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@PersistenceAdapter
@RequiredArgsConstructor
public class PlantHistoryPersistenceAdapter implements PlantHistorySpiPort {

    private final PlantHistoryRepository historyRepository;
    private final PlantHistoryMapper mapperRest;



    @Override
    public List<PlantHistory> findAllPlantHistory() {
        return historyRepository.findAll().stream().map(
                mapperRest::toPlantHistory
        ).toList();
    }

    @Override
    public List<PlantHistory> findPlantHistoryByPlantId(Long id) {
        return historyRepository.findPlantHistoryByPlantId(id).stream().map(
                mapperRest::toPlantHistory
        ).toList();
    }

    @Override
    public Optional<PlantHistory> findPlantHistoryById(Long id) {
        return Optional.of(mapperRest.toPlantHistory(historyRepository.findById(id).get()));
    }



    @Override
    public Optional<PlantHistory> findPlantByIdActive(Long plantId) {
        return historyRepository.
                findPlantByIdActive(plantId).map(mapperRest::toPlantHistory);
    }

    @Override
    public PlantHistory findTopByPlantIdOrderHistoryIdDesc(Long plantId) {
        PlantHistory plantHistory = mapperRest.toPlantHistory(historyRepository
                .findFirstByPlantOfPosition_IdOrderByIdDesc(plantId));

        if (plantHistory == null) {
            throw new BusinessRuleViolationException("No existe el elemento con el ID %s");
        }
        return plantHistory;
    }

    @Override
    public PlantHistory savePlantHistory(PlantHistory plantHistory) {

        PlantHistoryEntity entity = mapperRest.toPlantHistoryEntity(plantHistory);


        return mapperRest.toPlantHistory(historyRepository.save(entity)) ;
    }

    @Override
    public PlantHistory updatePlantHistory(PlantHistory plantHistory) {
        var entity = mapperRest.toPlantHistoryEntity(plantHistory);

        var saved = historyRepository.save(entity);
        return mapperRest.toPlantHistory(saved) ;
    }


}
