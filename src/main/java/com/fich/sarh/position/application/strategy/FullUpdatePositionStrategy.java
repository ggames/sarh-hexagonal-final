package com.fich.sarh.position.application.strategy;

import com.fich.sarh.common.exceptions.ResourceNotFoundException;
import com.fich.sarh.organizationalunit.domain.model.OrganizationalUnit;
import com.fich.sarh.organizationalunit.domain.ports.inbound.OrganizationalunitApiPort;
import com.fich.sarh.point.domain.model.Point;
import com.fich.sarh.point.domain.ports.inbound.PointApiPort;
import com.fich.sarh.position.domain.model.Position;
import com.fich.sarh.position.domain.model.PositionCommand;
import com.fich.sarh.position.domain.ports.outbound.PositionSpiPort;
import com.fich.sarh.transformation.domain.model.Transformation;
import com.fich.sarh.transformation.domain.ports.inbound.TransformationApiPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class FullUpdatePositionStrategy implements UpdatePositionStrategy<PositionCommand>{

    private final PositionSpiPort positionSpiPort;
    private final PointApiPort pointApiPort;
    private final TransformationApiPort transformationApiPort;
    private final OrganizationalunitApiPort organizationalUnitApiPort;



    @Override
    public Position update(Long id, PositionCommand command) {
    /*    if(positionSpiPort.existsOriginPositionId(id)){
            throw new BusinessRuleViolationException("El cargo ya fue ocupado para crear nuevos cargos");
        }*/
        var position = positionSpiPort.findPositionById(id)
                     .orElseThrow( ()-> new ResourceNotFoundException("No se encontro el cargo"));



        var point = pointApiPort.findPointById(command.getPoint())
                .orElseThrow(()->  new ResourceNotFoundException("El Tipo de cargo no existe"));

        // findTransformationById lanza ResourceNotFoundException internamente si no existe
        var transformation = transformationApiPort
                .findTransformationById(command.getResolutionTransformation());

        var organizationunit = organizationalUnitApiPort
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
