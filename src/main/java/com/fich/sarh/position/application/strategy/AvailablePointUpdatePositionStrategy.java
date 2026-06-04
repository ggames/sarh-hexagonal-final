package com.fich.sarh.position.application.strategy;

import com.fich.sarh.common.exceptions.ResourceNotFoundException;
import com.fich.sarh.organizationalunit.domain.ports.inbound.OrganizationalunitApiPort;
import com.fich.sarh.point.domain.ports.outbound.PointSpiPort;
import com.fich.sarh.position.domain.model.Position;
import com.fich.sarh.position.domain.ports.outbound.PositionSpiPort;
import com.fich.sarh.transformation.domain.model.Transformation;
import com.fich.sarh.transformation.domain.ports.outbound.TransformationSpiPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AvailablePointUpdatePositionStrategy implements UpdatePositionStrategy<Position>{

    private final PositionSpiPort positionSpiPort;
    private final OrganizationalunitApiPort organizationalunitApiPort;

    // REEMPLAZAR CUANDO SE FACTORICE POINT
    private final PointSpiPort pointSpiPort;
    private final TransformationSpiPort transformationSpiPort;

    @Override
    public Position update(Long id, Position command) {

        Position position = positionSpiPort.findPositionById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontro el cargo"));

        position.setPointsAvailable(command.getPointsAvailable());
        position.setPositionStatus(command.getPositionStatus());

        if(command.getResolutionSuppression() != null){
            Transformation t = transformationSpiPort.findTransformationById(command.getResolutionSuppression().getId())
                    .orElseThrow(()-> new ResourceNotFoundException("No se encontro la transformación de supresión"));

            position.setResolutionSuppression(t);
        }
        return positionSpiPort.savePosition(position);
    }


}
