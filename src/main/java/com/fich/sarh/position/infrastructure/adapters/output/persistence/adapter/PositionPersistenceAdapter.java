package com.fich.sarh.position.infrastructure.adapters.output.persistence.adapter;

import com.fich.sarh.common.StatusOfPositions;
import com.fich.sarh.common.WebAdapter;
import com.fich.sarh.common.exceptions.ResourceNotFoundException;
import com.fich.sarh.point.infrastructure.adapter.output.persistence.mapper.PointMapper;
import com.fich.sarh.position.domain.model.Position;
import com.fich.sarh.position.domain.model.PositionDto;
import com.fich.sarh.position.domain.ports.outbound.PositionSpiPort;
import com.fich.sarh.position.infrastructure.adapters.output.persistence.entity.PositionEntity;
import com.fich.sarh.position.infrastructure.adapters.output.persistence.mapper.PositionMapper;
import com.fich.sarh.position.infrastructure.adapters.output.persistence.repository.PositionRepository;
import com.fich.sarh.transformation.infrastructure.adapters.outbound.persistence.mapper.TransformationMapper;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@WebAdapter
@RequiredArgsConstructor
public class PositionPersistenceAdapter implements PositionSpiPort {

    private final PositionRepository positionRepository;

    private final PositionMapper positionMapper;

    private final PointMapper pointMapper;

    private final TransformationMapper transformationMapper;


    @Override
    public List<PositionDto> findOriginPositions(Long id_generatePosition) {

        return positionRepository.findOriginPosition(id_generatePosition);
    }

    @Override
    public List<PositionDto> findAllPositions() {

        return  positionRepository.findAllPosition(); //List.of();
    }

    @Override
    public List<Position> findAllPosition() {
        return   positionMapper.toDtoList(positionRepository.findAll()) ; //List.of();
    }

    @Override
    public List<PositionDto> findVacantPositions() {

        return  positionRepository.findVacantPositions(); //List.of();
    }

    @Override
    public List<PositionDto> findFreePositions() {

        return  positionRepository.findFreePosition(); //List.of();
    }

    @Override
    public List<Position> findAllByIdIn(List<Long> ids) {
        return  positionMapper.toDtoList(positionRepository.findAllByIdIn(ids)) ; //List.of();
    }

    @Override
    public List<Position> findAvailablePosition(StatusOfPositions status) {
        return  positionMapper.toDtoList(positionRepository.findAvailablePosition(status));  //List.of();
    }

    @Override
    public Optional<Position> findPositionById(Long id) {
        Optional<Position> position = positionRepository.findById(id).map(positionMapper::toDto);

        return position;
    }


    @Override
    public Position savePosition(Position position) {

        var entity = positionMapper.toEntity(position);

        if(entity.getParentRelations() == null) {
            entity.setParentRelations(new ArrayList<>());
        }

        var saved = positionRepository.save(entity);
        return  positionMapper.toDto(saved);

    }

    @Override
    public Position updatePosition(Position position) {
        PositionEntity entity = positionRepository.findById(position.getId())
                .orElseThrow(()-> new ResourceNotFoundException("Cargo no encontrado"));

        entity.setActive(position.isActive());
        entity.setPoint( pointMapper.toEntity(position.getPoint()));
        entity.setCreationResolution(
                transformationMapper.toEntity(position.getCreationResolution()));
        entity.setResolutionSuppression(
                transformationMapper.toEntity(position.getResolutionSuppression())
        );

        // =============================================================
        //   RELACIONES
        // =============================================================

        entity.clearParents();
        if(position.getParents() != null){
            for (Position parent: position.getParents()){
                var parentEntity = positionRepository.findById(parent.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Cargo padre no encontrado"));

                entity.addParent(parentEntity);
            }

        }

        var saved = positionRepository.save(entity);
        return positionMapper.toDto(saved);
    }


    @Override
    public void deletePosition(Long id) {

    }
}
