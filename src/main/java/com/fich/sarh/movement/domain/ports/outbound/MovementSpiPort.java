package com.fich.sarh.movement.domain.ports.outbound;

import com.fich.sarh.movement.domain.model.Movement;
import com.fich.sarh.plantofpositions.domain.model.PlantOfPosition;

import java.util.List;
import java.util.Optional;

public interface MovementSpiPort {

    List<Movement> findAllMovements();
    Optional<Movement> findMovementById(Long id);
    Movement findByPlant(PlantOfPosition plant);
    void saveMovement(Movement movement);


}
