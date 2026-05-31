package com.fich.sarh.position.application.usecases;

import com.fich.sarh.common.StatusOfPositions;
import com.fich.sarh.common.UseCase;
import com.fich.sarh.common.exceptions.ResourceNotFoundException;
import com.fich.sarh.organizationalunit.domain.model.OrganizationalUnit;
import com.fich.sarh.organizationalunit.domain.ports.outbound.OrganizationalunitSpiPort;
import com.fich.sarh.point.domain.model.Point;
import com.fich.sarh.point.domain.ports.outbound.PointSpiPort;
import com.fich.sarh.position.application.strategy.AvailablePointUpdatePositionStrategy;
import com.fich.sarh.position.application.strategy.FullUpdatePositionStrategy;
import com.fich.sarh.position.application.strategy.OriginatorUpdateStrategy;
import com.fich.sarh.position.domain.model.Position;
import com.fich.sarh.position.domain.model.PositionCommand;
import com.fich.sarh.position.domain.model.PositionDto;
import com.fich.sarh.position.domain.ports.inbound.PositionApiPort;
import com.fich.sarh.position.domain.ports.outbound.PositionSpiPort;
import com.fich.sarh.position.domain.service.PositionDomainService;
import com.fich.sarh.relationshipofposition.domain.service.RelationshipPositionDomainService;
import com.fich.sarh.transformation.application.ports.persistence.TransformationRetrieveSpiPort;
import com.fich.sarh.transformation.domain.model.Transformation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@UseCase
@RequiredArgsConstructor
@Log4j2
class PositionApiPortUseCases implements PositionApiPort {

    private final PositionSpiPort positionSpiPort;
    private final PositionDomainService positionService;
    private final RelationshipPositionDomainService relationService;

    // REEMPLAZAR CUANDO SE FACTORICE POINT
    private final PointSpiPort pointSpiPort;
    private final TransformationRetrieveSpiPort transformationSpiPort;
    private final OrganizationalunitSpiPort organizationalUnitSpiPort;

    private final FullUpdatePositionStrategy fullStrategy;
    private final AvailablePointUpdatePositionStrategy availableStrategy;
    private final OriginatorUpdateStrategy originatorStrategy;

/*    @Override
    public List<PositionDto> findOriginPositions(Long id_generatePosition) {
        return positionSpiPort.findOriginPositions(id_generatePosition);

    }*/

    @Override
    public List<PositionDto> findOriginPositions(Long id_generatePosition) {

        return positionSpiPort.findOriginPositions(id_generatePosition);
    }

    @Override
    public List<PositionDto> findAllPositions() {
        return positionSpiPort.findAllPositions();
    }

    @Override
    public List<Position> findAllPosition() {
        return positionSpiPort.findAllPosition();
    }

    @Override
    public List<PositionDto> findVacantPositions() {

        return positionSpiPort.findVacantPositions();
    }

    @Override
    public List<PositionDto> findFreePositions() {

        return positionSpiPort.findFreePositions();
    }

    @Override
    public List<Position> findAllByIdIn(List<Long> ids) {
        return positionSpiPort.findAllByIdIn(ids);
    }

    @Override
    public List<Position> findAvailablePosition(StatusOfPositions status) {
        return positionSpiPort.findAvailablePosition(status);
    }

    @Override
    public Optional<Position> findPositionById(Long id) {
        return positionSpiPort.findPositionById(id);
    }

    @Override
    public Position updateFullPosition(Long id, PositionCommand command) {

        var position = fullStrategy.update(id, command);

        return positionSpiPort.savePosition(position);
    }

    @Override
    public Position updateAvailablePoint(Long id, Position command) {

        return availableStrategy.update(id, command);
    }

    @Override
    public Position updateOriginatorRelation(Long id, Position command) {
        var position = positionSpiPort.findPositionById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Cargo no encontrado"));

        position.getParents().clear();

        if(command.getParents() != null){
            position.getParents().addAll(command.getParents());
        }

        return originatorStrategy.update(id, position);
    }

    @Override
    public Position addPosition(PositionCommand command) {

        log.info("Datos de cargos de ORIGEN "+ command.toString());

        Point pointFound = pointSpiPort.findPointById(command.getPoint())
                .orElseThrow(() -> new ResourceNotFoundException("No existe el tipo de cargo"));

        Transformation transformation = transformationSpiPort
                .findById(command.getResolutionTransformation())
                .orElseThrow(() -> new ResourceNotFoundException("No existe el resolución de creación del cargo"));

        OrganizationalUnit organizationalUnit = organizationalUnitSpiPort
                .findOrganizationalunitById(command.getOrganizational())
                .orElseThrow(() -> new ResourceNotFoundException("No existe el departamento asociado al cargo"));

        List<Position> originPositions = command.getOriginPositionIds() == null ?
                Collections.emptyList():
                positionSpiPort.findAllByIdIn(command.getOriginPositionIds());

        log.info("NUEVO CARGOO ADD " + originPositions.toString());
        // 2. Crear dominio
        Position newPosition = Position.builder()
                .point(pointFound)
                .active(true)
                .organizationalUnit(organizationalUnit)
                .positionStatus(command.getPositionStatus())
                .pointsAvailable(100L)
                .creationResolution(transformation)
                .build();


        if(!originPositions.isEmpty()){
            newPosition.setParents(originPositions);
        }


        // relaciones (dominio)


        Position savedPosition = positionSpiPort.savePosition(newPosition);
        // relationService.processRelationship(savedPosition, originPositions);

        if (!originPositions.isEmpty()) {
            List<Position> calculated = positionService
                    .calculatePosition(originPositions, pointFound.getAmountPoint());
            for (Position origin : calculated) {
                if (origin.getPositionStatus() != StatusOfPositions.SUPRIMIDO) {
                    origin.setPositionStatus(StatusOfPositions.SUPRIMIDO);
                    origin.setResolutionSuppression(transformation);
                }
                // relacion hijo -> padre

                 availableStrategy.update(origin.getId(), origin);

            }
        }

        return savedPosition;
    }

    @Override
    public void deletePosition(Long id) {

    }
}
