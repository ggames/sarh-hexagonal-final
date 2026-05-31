package com.fich.sarh.relationshipofposition.infrastructure.adapters.output.persistence.mapper;

import com.fich.sarh.relationshipofposition.domain.model.RelationshipPosition;
import com.fich.sarh.relationshipofposition.infrastructure.adapters.output.persistence.entity.RelationshipPositionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface RelationshipPositionMapper {

    RelationshipPositionEntity toEntity(RelationshipPosition dto);

    RelationshipPosition toDto(RelationshipPositionEntity entity);

    List<RelationshipPosition> toDtoList(List<RelationshipPositionEntity> entities);

    List<RelationshipPositionEntity> toEntities(List<RelationshipPosition> dtos);
}
