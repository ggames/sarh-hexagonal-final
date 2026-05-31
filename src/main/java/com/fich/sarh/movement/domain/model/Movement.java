package com.fich.sarh.movement.domain.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class Movement {

    Long id;

    Long plantId;

    Long positionId;

    LocalDate movementDate;

    String reasonForMovement;


}
