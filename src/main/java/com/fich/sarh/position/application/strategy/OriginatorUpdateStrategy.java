package com.fich.sarh.position.application.strategy;

import com.fich.sarh.common.exceptions.ResourceNotFoundException;
import com.fich.sarh.position.domain.model.Position;
import com.fich.sarh.position.domain.ports.outbound.PositionSpiPort;
import com.fich.sarh.relationshipofposition.domain.service.RelationshipPositionDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OriginatorUpdateStrategy implements UpdatePositionStrategy<Position>{

    private final PositionSpiPort positionSpiPort;
    private final RelationshipPositionDomainService relationshipDomainService;

    @Override
    public Position update(Long id, Position command) {

        Position position = positionSpiPort.findPositionById(id)
                            .orElseThrow(()-> new ResourceNotFoundException("Cargo no encontrado"));

        // limpiar relaciones anteriores
        position.getParents().clear();

        // Relaciones nuevas
        List<Position> newParents = command.getParents() == null?
                      new ArrayList<>()
                     : command.getParents();

        // Validar + crear relaciones
        relationshipDomainService.processRelationship(
                position,
                newParents
        );

        return positionSpiPort.savePosition(position);

    }
}
