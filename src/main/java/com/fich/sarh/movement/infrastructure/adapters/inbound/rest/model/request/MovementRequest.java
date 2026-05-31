package com.fich.sarh.movement.infrastructure.adapters.inbound.rest.model.request;

import com.fich.sarh.plantofpositions.domain.model.PlantOfPosition;
import com.fich.sarh.position.domain.model.Position;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovementRequest {

    Date movementDate;

    String reasonForMovement;

    PlantOfPosition plant;

    Position position;

}
