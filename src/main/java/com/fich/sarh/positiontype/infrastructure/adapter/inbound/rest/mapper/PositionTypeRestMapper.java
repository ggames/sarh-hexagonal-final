package com.fich.sarh.positiontype.infrastructure.adapter.inbound.rest.mapper;

import com.fich.sarh.positiontype.domain.model.PositionType;
import com.fich.sarh.positiontype.infrastructure.adapter.inbound.rest.model.request.PositionTypeRequest;
import com.fich.sarh.positiontype.infrastructure.adapter.inbound.rest.model.response.PositionTypeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PositionTypeRestMapper {

    PositionTypeRestMapper INSTANCE = Mappers.getMapper(PositionTypeRestMapper.class);

    PositionType toPositionType(PositionTypeRequest request);

    PositionTypeResponse toPositionTypeResponse(PositionType position);


}
