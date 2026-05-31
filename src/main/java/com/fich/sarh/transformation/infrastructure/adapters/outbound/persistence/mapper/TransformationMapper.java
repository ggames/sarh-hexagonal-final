package com.fich.sarh.transformation.infrastructure.adapters.outbound.persistence.mapper;

import com.fich.sarh.transformation.domain.model.Transformation;
import com.fich.sarh.transformation.infrastructure.adapters.outbound.persistence.entity.TransformationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface TransformationMapper {
    TransformationMapper INSTANCE = Mappers.getMapper(TransformationMapper.class);
    Transformation toDto(TransformationEntity entity);
    TransformationEntity toEntity(Transformation dto);
}
