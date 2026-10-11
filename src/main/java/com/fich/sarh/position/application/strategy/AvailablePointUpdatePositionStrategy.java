package com.fich.sarh.position.application.strategy;

import com.fich.sarh.common.exceptions.ResourceNotFoundException;
import com.fich.sarh.position.domain.model.Position;
import com.fich.sarh.position.domain.ports.outbound.PositionSpiPort;
import com.fich.sarh.transformation.domain.model.Transformation;
import com.fich.sarh.transformation.domain.ports.inbound.TransformationApiPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AvailablePointUpdatePositionStrategy implements UpdatePositionStrategy<Position>{

    private final PositionSpiPort positionSpiPort;
    private final TransformationApiPort transformationApiPort;

    @Override
    public Position update(Long id, Position command) {

        Position position = positionSpiPort.findPositionById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontro el cargo"));

        position.setPointsAvailable(command.getPointsAvailable());
        position.setPositionStatus(command.getPositionStatus());

        if(command.getResolutionSuppression() != null){
            Transformation t = transformationApiPort
                    .findTransformationById(command.getResolutionSuppression().getId());

            position.setResolutionSuppression(t);
        }
        return positionSpiPort.savePosition(position);
    }


}
