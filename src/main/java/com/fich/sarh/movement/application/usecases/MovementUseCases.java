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
        return movementSpiPort.findMovementById(id)
                .orElseThrow(() -> new com.fich.sarh.common.exceptions.ResourceNotFoundException(
                        "Movimiento con ID " + id + " no encontrado"));
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
        movementSpiPort.findMovementById(id).ifPresent(mov -> {
            mov.setReasonForMovement(movement.getReasonForMovement());
            mov.setMovementDate(movement.getMovementDate());
            mov.setPlantId(movement.getPlantId());
            mov.setPositionId(movement.getPositionId());
            movementSpiPort.saveMovement(mov);
        });
    }

    @Override
    public boolean existsByPositionId(Long positionId) {
        return movementSpiPort.existsByPositionId(positionId);
    }

    @Override
    public boolean existsByPlantId(Long plantId) {
        return movementSpiPort.existsByPlantId(plantId);
    }

    @Override
    public void deleteMovement(Long id) {
        movementSpiPort.findMovementById(id)
                .orElseThrow(() -> new com.fich.sarh.common.exceptions.ResourceNotFoundException(
                        "Movimiento con ID " + id + " no encontrado"));
        movementSpiPort.deleteMovement(id);
    }
}
