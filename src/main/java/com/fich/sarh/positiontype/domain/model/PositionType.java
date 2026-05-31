package com.fich.sarh.positiontype.domain.model;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PositionType {
    Long id;
    String namePosition;
    Double basicSalary;
    int amountOfPointPerPosition;

}
