package com.fich.sarh.transformation.domain.ports.inbound;

import com.fich.sarh.transformation.domain.model.Transformation;
import com.fich.sarh.transformation.infrastructure.adapters.inbound.rest.model.request.TransformationRequest;

import java.util.List;

public interface TransformationApiPort {

    List<Transformation> findAllTransformations();

    Transformation findTransformationById(Long id);

    Transformation findByResolutionNumber(String resolutionNumber);

    Transformation findFirstByOrderDesc();

    Transformation saveTransformation(Transformation transformation);

    Transformation updateTransformation(Long id, Transformation transformation);

    boolean existByResolutionNumber(String resolutionNumber);

}
