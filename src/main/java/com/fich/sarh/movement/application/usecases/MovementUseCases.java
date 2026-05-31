package com.fich.sarh.movement.application.usecases;

import com.fich.sarh.common.UseCase;
import com.fich.sarh.movement.domain.model.Movement;
import com.fich.sarh.movement.domain.ports.inbound.MovementApiPort;
import com.fich.sarh.movement.domain.ports.outbound.MovementSpiPort;
import com.fich.sarh.plantofpositions.domain.model.PlantOfPosition;
import lombok.RequiredArgsConstructor;

import java.util.List;

@UseCase
@RequiredArgsConstructor
public class MovementUseCases implements MovementApiPort {

    private final MovementSpiPort movementSpiPort;

    @Override
    public List<Movement> findAllMovements() {

        return movementSpiPort.findAllMovements();
    }

    @Override
    public Movement findMovementById(Long id) {

        return movementSpiPort.findMovementById(id).get();
    }

    @Override
    public Movement findByPlant(PlantOfPosition plant) {
        return movementSpiPort.findByPlant(plant);
    }

    @Override
    public void saveMovement(Movement movement) {

       movementSpiPort.saveMovement(movement);
    }

    @Override
    public void updateMovement(Long id, Movement movement) {
        movementSpiPort.findMovementById(id).map(
                mov -> {
                    mov.setReasonForMovement(movement.getReasonForMovement());
                    mov.setMovementDate(movement.getMovementDate());
                    mov.setPlantId(movement.getPlantId());
                    mov.setPositionId(movement.getPositionId());

                    movementSpiPort.saveMovement(mov);
                    return null;
                }
        );
    }
}
