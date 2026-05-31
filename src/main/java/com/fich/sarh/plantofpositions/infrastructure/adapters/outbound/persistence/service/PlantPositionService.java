package com.fich.sarh.plantofpositions.infrastructure.adapters.outbound.persistence.service;

import com.fich.sarh.common.exceptions.BusinessRuleViolationException;
import com.fich.sarh.plantofpositions.domain.model.PlantOfPositionCommand;
import com.fich.sarh.plantofpositions.domain.ports.outbound.PlantPositionSpiPort;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlantPositionService {

    private final PlantPositionSpiPort plantSpiPort;
    public void validateActivePlant(PlantOfPositionCommand command) {

        boolean exists = plantSpiPort.existsByPositionAndAgent(
                command.getPositionId(),
                command.getAgentId()
        );

        if (exists) {
            throw new BusinessRuleViolationException(
                    "Ya hay un agente activo en el cargo"
            );
        }
    }
}
