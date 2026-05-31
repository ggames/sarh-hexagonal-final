package com.fich.sarh.plantofpositions.infrastructure.adapters.outbound.persistence.adapters;

import com.fich.sarh.common.WebAdapter;
import com.fich.sarh.plantofpositions.domain.model.PlantFilter;
import com.fich.sarh.plantofpositions.domain.model.PlantOfPosition;
import com.fich.sarh.plantofpositions.domain.model.PlantOfPositionDto;
import com.fich.sarh.plantofpositions.domain.model.PlantProjectionDTO;
import com.fich.sarh.plantofpositions.domain.ports.outbound.PlantPositionSpiPort;
import com.fich.sarh.plantofpositions.infrastructure.adapters.outbound.persistence.mapper.PlantOfPositionMapper;
import com.fich.sarh.plantofpositions.infrastructure.adapters.outbound.persistence.repository.PlantOfPositionCustomRepository;
import com.fich.sarh.plantofpositions.infrastructure.adapters.outbound.persistence.repository.PlantOfPositionRepository;
import com.fich.sarh.plantofpositions.infrastructure.adapters.outbound.persistence.service.IExcelPlantReport;
import lombok.RequiredArgsConstructor;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;

@WebAdapter
@RequiredArgsConstructor
public class PlantPositionPersistenceAdapter implements PlantPositionSpiPort {

    private final PlantOfPositionRepository plantRepository;
    private final PlantOfPositionCustomRepository customRepository;
    private final IExcelPlantReport excelReport;

    private final PlantOfPositionMapper mapper;

    @Override
    public ByteArrayInputStream createExcel(List<PlantProjectionDTO> plants) {

        return excelReport.createExcel(plants);
    }

    @Override
    public List<PlantOfPositionDto> findAllPlantPositions() {
        return plantRepository.findAllPlantOfPosition();
    }

    @Override
    public Optional<PlantOfPosition> findPlantPositionById(Long id) {
        return plantRepository.findById(id).map(mapper::toPlantOfPosition) ;
    }

    @Override
    public PlantOfPosition savePlantOfPosition(PlantOfPosition request) {
        var entity = mapper.toPlantOfPositionEntity(request);
        return mapper.toPlantOfPosition(plantRepository.save(entity));
    }

    @Override
    public List<PlantProjectionDTO> search(PlantFilter filter) {
        return customRepository.findAllProjection(filter);
    }

    @Override
    public PlantOfPosition updatePlantOfPosition(PlantOfPosition request) {
        var entity = mapper.toPlantOfPositionEntity(request);
        return mapper.toPlantOfPosition(plantRepository.save(entity));
    }

    @Override
    public boolean existsByPositionAndAgent(Long position, Long agent) {
        return plantRepository.existsByAgentAndPosition(position, agent);
    }
}
