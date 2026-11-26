package com.fich.sarh.position.application.usecases;

import com.fich.sarh.common.StatusOfPositions;
import com.fich.sarh.common.UseCase;
import com.fich.sarh.common.exceptions.BusinessRuleViolationException;
import com.fich.sarh.common.exceptions.ResourceNotFoundException;
import com.fich.sarh.movement.domain.ports.inbound.MovementApiPort;
import com.fich.sarh.organizationalunit.domain.model.OrganizationalUnit;
import com.fich.sarh.organizationalunit.domain.ports.inbound.OrganizationalunitApiPort;
import com.fich.sarh.plantofpositions.domain.ports.inbound.PlantPositionApiPort;
import com.fich.sarh.point.domain.model.Point;
import com.fich.sarh.point.domain.ports.inbound.PointApiPort;
import com.fich.sarh.position.application.strategy.AvailablePointUpdatePositionStrategy;
import com.fich.sarh.position.application.strategy.FullUpdatePositionStrategy;
import com.fich.sarh.position.application.strategy.OriginatorUpdateStrategy;
import com.fich.sarh.position.domain.model.Position;
import com.fich.sarh.position.domain.model.PositionCommand;
import com.fich.sarh.position.domain.model.PositionDto;
import com.fich.sarh.position.domain.ports.inbound.PositionApiPort;
import com.fich.sarh.position.domain.ports.outbound.PositionSpiPort;
import com.fich.sarh.position.domain.service.PositionDomainService;
import com.fich.sarh.relationshipofposition.domain.ports.inbound.RelationshipPositionApiPort;
import com.fich.sarh.relationshipofposition.domain.service.RelationshipPositionDomainService;
import com.fich.sarh.transformation.domain.ports.inbound.TransformationApiPort;
import com.fich.sarh.transformation.domain.model.Transformation;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Lazy;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@UseCase
@Log4j2
class PositionApiPortUseCases implements PositionApiPort {

    private final PositionSpiPort positionSpiPort;
    private final PositionDomainService positionService;
    private final RelationshipPositionDomainService relationService;

    private final PointApiPort pointApiPort;
    private final TransformationApiPort transformationApiPort;
    private final OrganizationalunitApiPort organizationalUnitApiPort;

    private final FullUpdatePositionStrategy fullStrategy;
    private final AvailablePointUpdatePositionStrategy availableStrategy;
    private final OriginatorUpdateStrategy originatorStrategy;

    private final PlantPositionApiPort plantPositionApiPort;
    private final RelationshipPositionApiPort relationshipPositionApiPort;
    private final MovementApiPort movementApiPort;

    public PositionApiPortUseCases(
            PositionSpiPort positionSpiPort,
            PositionDomainService positionService,
            RelationshipPositionDomainService relationService,
            PointApiPort pointApiPort,
            TransformationApiPort transformationApiPort,
            OrganizationalunitApiPort organizationalUnitApiPort,
            FullUpdatePositionStrategy fullStrategy,
            AvailablePointUpdatePositionStrategy availableStrategy,
            OriginatorUpdateStrategy originatorStrategy,
            @Lazy PlantPositionApiPort plantPositionApiPort,
            @Lazy RelationshipPositionApiPort relationshipPositionApiPort,
            @Lazy MovementApiPort movementApiPort) {
        this.positionSpiPort = positionSpiPort;
        this.positionService = positionService;
        this.relationService = relationService;
        this.pointApiPort = pointApiPort;
        this.transformationApiPort = transformationApiPort;
        this.organizationalUnitApiPort = organizationalUnitApiPort;
        this.fullStrategy = fullStrategy;
        this.availableStrategy = availableStrategy;
        this.originatorStrategy = originatorStrategy;
        this.plantPositionApiPort = plantPositionApiPort;
        this.relationshipPositionApiPort = relationshipPositionApiPort;
        this.movementApiPort = movementApiPort;
    }

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
    public List<Position> findAllPosition(int page, int size) {
        return positionSpiPort.findAllPosition(page, size);
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
    @Transactional
    public Position updateFullPosition(Long id, PositionCommand command) {

        var position = fullStrategy.update(id, command);

        return positionSpiPort.savePosition(position);
    }

    @Override
    @Transactional
    public Position updateAvailablePoint(Long id, Position command) {

        return availableStrategy.update(id, command);
    }

    @Override
    @Transactional
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
    @Transactional
    public Position addPosition(PositionCommand command) {

        log.info("Datos de cargos de ORIGEN "+ command.toString());

        Point pointFound = pointApiPort.findPointById(command.getPoint())
                .orElseThrow(() -> new ResourceNotFoundException("No existe el tipo de cargo"));

        // findTransformationById lanza ResourceNotFoundException internamente si no existe
        Transformation transformation = transformationApiPort
                .findTransformationById(command.getResolutionTransformation());

        OrganizationalUnit organizationalUnit = organizationalUnitApiPort
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
    @Transactional
    public void deletePosition(Long id) {
        positionSpiPort.findPositionById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cargo no encontrado con ID: " + id));

        if (plantPositionApiPort.existsByPositionId(id)) {
            throw new BusinessRuleViolationException("No se puede eliminar el cargo porque forma parte de la planta de cargos.");
        }

        if (relationshipPositionApiPort.existsByPositionId(id)) {
            throw new BusinessRuleViolationException("No se puede eliminar el cargo porque está asociado a una transformación o relación con otros cargos.");
        }

        if (movementApiPort.existsByPositionId(id)) {
            throw new BusinessRuleViolationException("No se puede eliminar el cargo porque posee movimientos registrados.");
        }

        positionSpiPort.deletePosition(id);
    }

    @Override
    public boolean existsByOrganizationalUnitId(Long organizationalUnitId) {
        return positionSpiPort.existsByOrganizationalUnitId(organizationalUnitId);
    }

    @Override
    public boolean existsByPointId(Long pointId) {
        return positionSpiPort.existsByPointId(pointId);
    }

    @Override
    public boolean existsByTransformationId(Long transformationId) {
        return positionSpiPort.existsByTransformationId(transformationId);
    }
}
