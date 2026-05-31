package com.fich.sarh.transformation.infrastructure.adapters.inbound.rest.mapper;

import com.fich.sarh.transformation.domain.model.Transformation;
import com.fich.sarh.transformation.infrastructure.adapters.inbound.rest.model.request.TransformationRequest;
import com.fich.sarh.transformation.infrastructure.adapters.inbound.rest.model.response.TransformationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring" , unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface TransformationRestMapper {

    TransformationRestMapper INSTANCE = Mappers.getMapper(TransformationRestMapper.class);
    Transformation toTransformation(TransformationRequest request);

    TransformationResponse toTransformationResponse(Transformation transformation);


}
