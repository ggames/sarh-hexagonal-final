package com.fich.sarh.movement.domain.ports.inbound;

import com.fich.sarh.movement.domain.model.Movement;
import com.fich.sarh.plantofpositions.domain.model.PlantOfPosition;

import java.util.List;

public interface MovementApiPort {
    List<Movement> findAllMovements();
    Movement findMovementById(Long id);
    Movement findByPlant(PlantOfPosition plant);
    void saveMovement(Movement movement);
    void updateMovement(Long id, Movement movement);
}
