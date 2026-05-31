package com.fich.sarh.positiontype.infrastructure.adapter.outbound.persistence.mapper;

import com.fich.sarh.positiontype.domain.model.PositionType;
import com.fich.sarh.positiontype.infrastructure.adapter.outbound.persistence.entity.PositionTypeEntity;
import org.apache.poi.ss.formula.functions.MultiOperandNumericFunction;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface PositionTypeMapper {

    PositionTypeMapper INSTANCE = Mappers.getMapper(PositionTypeMapper.class);

    PositionType toPositionType(PositionTypeEntity entity);

    PositionTypeEntity toPositionTypeEntity(PositionType position);
}
