package com.fich.sarh.position.application.strategy;

import com.fich.sarh.common.exceptions.ResourceNotFoundException;
import com.fich.sarh.organizationalunit.domain.model.OrganizationalUnit;
import com.fich.sarh.organizationalunit.domain.ports.outbound.OrganizationalunitSpiPort;
import com.fich.sarh.point.domain.model.Point;
import com.fich.sarh.point.domain.ports.outbound.PointSpiPort;
import com.fich.sarh.position.domain.model.Position;
import com.fich.sarh.position.domain.model.PositionCommand;
import com.fich.sarh.position.domain.ports.outbound.PositionSpiPort;
import com.fich.sarh.transformation.application.ports.persistence.TransformationRetrieveSpiPort;
import com.fich.sarh.transformation.domain.model.Transformation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class FullUpdatePositionStrategy implements UpdatePositionStrategy<PositionCommand>{

    private final PositionSpiPort positionSpiPort;

    // REEMPLAZAR CUANDO SE FACTORICE POINT
    private final PointSpiPort pointSpiPort;
    private final TransformationRetrieveSpiPort transformationSpiPort;
    private final OrganizationalunitSpiPort organizationalUnitSpiPort;



    @Override
    public Position update(Long id, PositionCommand command) {
    /*    if(positionSpiPort.existsOriginPositionId(id)){
            throw new BusinessRuleViolationException("El cargo ya fue ocupado para crear nuevos cargos");
        }*/
        var position = positionSpiPort.findPositionById(id)
                     .orElseThrow( ()-> new ResourceNotFoundException("No se encontro el cargo"));



        var point = pointSpiPort.findPointById(command.getPoint())
                .orElseThrow(()->  new ResourceNotFoundException("El Tipo de cargo no existe"));


        var transformation = transformationSpiPort
                .findById(command.getResolutionTransformation())
                .orElseThrow(()-> new ResourceNotFoundException("La resolución de transformación no existe"));

        var organizationunit = organizationalUnitSpiPort
                .findOrganizationalunitById(command.getResolutionTransformation())
        .orElseThrow(()-> new ResourceNotFoundException("No existe la unidad Organizativa indicada"));


        position.setPositionStatus(command.getPositionStatus());
        position.setPoint(point);
        position.setOrganizationalUnit(organizationunit);
        position.setCreationResolution(transformation);

          /* positionSpiPort.savePosition(position.get()); */
        return position;

    }
}
