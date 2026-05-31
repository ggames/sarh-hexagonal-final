package com.fich.sarh.position.infrastructure.adapters.output.persistence.mapper;

import com.fich.sarh.position.domain.model.Position;
import com.fich.sarh.position.infrastructure.adapters.output.persistence.entity.PositionEntity;
import com.fich.sarh.position.infrastructure.adapters.output.persistence.repository.PositionRepository;
import com.fich.sarh.relationshipofposition.infrastructure.adapters.output.persistence.entity.RelationshipPositionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PositionMapperCustom {

    private PositionRepository positionRepository;

    public PositionEntity toEntity(Position domain){
        PositionEntity entity = new PositionEntity();
        List<RelationshipPositionEntity> relations = domain.getParents().stream()
                .map(parent -> {
                       RelationshipPositionEntity rel = new RelationshipPositionEntity();
                       rel.setParent(positionRepository.getReferenceById(rel.getId()));
                       rel.setChild(entity);
                       return rel;
                }).toList();

        entity.setParentRelations(relations);
        return entity;
    }
}
