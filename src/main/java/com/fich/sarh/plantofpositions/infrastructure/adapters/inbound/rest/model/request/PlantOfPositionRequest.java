package com.fich.sarh.plantofpositions.infrastructure.adapters.inbound.rest.model.request;

import com.fich.sarh.agent.domain.model.Agent;
import com.fich.sarh.common.CharacterPlant;
import com.fich.sarh.common.PlantStatus;
import com.fich.sarh.organizationalsubunit.domain.model.OrganizationalSubUnit;
import com.fich.sarh.position.domain.model.Position;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlantOfPositionRequest {

    Position position;

    Agent agent;

    CharacterPlant characterplantID;

    PlantStatus currentStatusID;

    OrganizationalSubUnit organizationalSubUnit;

    LocalDate dateFrom;

    LocalDate dateTo;

    String reasonForMovement;
}
