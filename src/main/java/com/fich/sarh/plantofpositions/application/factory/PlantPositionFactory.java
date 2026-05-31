package com.fich.sarh.plantofpositions.application.factory;

import com.fich.sarh.agent.domain.model.Agent;
import com.fich.sarh.common.CharacterPlant;
import com.fich.sarh.common.PlantStatus;
import com.fich.sarh.organizationalsubunit.domain.model.OrganizationalSubUnit;
import com.fich.sarh.plantofpositions.domain.model.PlantOfPosition;
import com.fich.sarh.position.domain.model.Position;
import org.springframework.stereotype.Component;

@Component
public class PlantPositionFactory {

    public PlantOfPosition build(
            Agent agent,
            Position position,
            OrganizationalSubUnit subUnit,
            PlantStatus status,
            CharacterPlant characterPlant
    ){
           return PlantOfPosition.builder()
                   .agent(agent)
                   .position(position)
                   .organizationalSubUnit(subUnit)
                   .currentStatusID(status)
                   .characterplantID(characterPlant)
                   .build();
    }
}
