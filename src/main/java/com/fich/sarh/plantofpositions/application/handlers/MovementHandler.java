package com.fich.sarh.plantofpositions.application.handlers;

import com.fich.sarh.movement.domain.model.Movement;
import com.fich.sarh.movement.domain.ports.outbound.MovementSpiPort;
import com.fich.sarh.plantofpositions.domain.model.PlantOfPosition;
import com.fich.sarh.position.domain.model.Position;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class MovementHandler {

    private final MovementSpiPort movementSpiPort;

    public void register(PlantOfPosition plant,
                          Position position,
                          LocalDate dateFrom,
                          String reason){
        var movement = Movement.builder()
                .plantId(plant.getId())
                .positionId(position.getId())
                .movementDate(dateFrom)
                .reasonForMovement(reason)
                .build();

         movementSpiPort.saveMovement(movement);
    }
}
