package com.fich.sarh.movement.infrastructure.adapters.outbound.persistence.adapter;

import com.fich.sarh.common.PersistenceAdapter;
import com.fich.sarh.common.exceptions.ResourceNotFoundException;
import com.fich.sarh.movement.domain.model.Movement;
import com.fich.sarh.movement.domain.ports.outbound.MovementSpiPort;
import com.fich.sarh.movement.infrastructure.adapters.outbound.persistence.entity.MovementEntity;
import com.fich.sarh.movement.infrastructure.adapters.outbound.persistence.mapper.MovementMapper;
import com.fich.sarh.movement.infrastructure.adapters.outbound.persistence.repository.MovementRepository;
import com.fich.sarh.plantofpositions.domain.model.PlantOfPosition;
import com.fich.sarh.plantofpositions.domain.ports.outbound.PlantPositionSpiPort;
import com.fich.sarh.plantofpositions.infrastructure.adapters.outbound.persistence.entity.PlantOfPositionEntity;
import com.fich.sarh.plantofpositions.infrastructure.adapters.outbound.persistence.mapper.PlantOfPositionMapper;
import com.fich.sarh.position.domain.model.Position;
import com.fich.sarh.position.domain.ports.outbound.PositionSpiPort;
import com.fich.sarh.position.infrastructure.adapters.output.persistence.entity.PositionEntity;
import com.fich.sarh.position.infrastructure.adapters.output.persistence.mapper.PositionMapper;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@PersistenceAdapter
@RequiredArgsConstructor
public class MovementPersistenceAdapter implements MovementSpiPort {


    private final MovementRepository movementRepository;
    private final PlantPositionSpiPort plantRetrieve;
    private final PositionSpiPort positionApiPort;

    private final MovementMapper mapper;




    @Override
    public List<Movement> findAllMovements() {

        return  movementRepository.findAll().stream().map(mapper::toMovement).toList();
    }

    @Override
    public Optional<Movement> findMovementById(Long id) {
        MovementEntity entity = movementRepository.findById(id)
                .orElseThrow( () -> new ResourceNotFoundException("El movimiento indicado no existe"));

        return Optional.of(mapper.toMovement(entity)) ;
    }



    @Override
    public Movement findByPlant(PlantOfPosition plant) {
        return mapper.toMovement(movementRepository.fetchMovementByPlant(
                PlantOfPositionMapper.INSTANCE.toPlantOfPositionEntity(plant)));
    }




    @Override
    public void saveMovement(Movement movement) {

        MovementEntity entity = mapper.toMovementEntity(movement);
        Position position = positionApiPort.findPositionById(movement.getPositionId()).orElseThrow(()-> new RuntimeException("No existe el cargo"));
        PlantOfPosition plant = plantRetrieve.findPlantPositionById(movement.getPlantId()).orElseThrow(()-> new RuntimeException("No existe la planta de cargo"));

        PositionEntity positionEntity = PositionMapper.INSTANCE.toEntity(position);
        PlantOfPositionEntity plantEntity = PlantOfPositionMapper.INSTANCE.toPlantOfPositionEntity(plant);
        entity.setPlant(plantEntity);
        entity.setPosition(positionEntity);



    }


}
