package com.fich.sarh.transformation.domain.ports.outbound;

import com.fich.sarh.transformation.domain.model.Transformation;

import java.util.List;
import java.util.Optional;

public interface TransformationSpiPort {

    List<Transformation> findAllTransformations();

    Optional<Transformation> findTransformationById(Long id);

    Optional<Transformation> findByResolutionNumber(String resolutionNumber);

    Optional<Transformation> findFirstByOrderDesc();

    Transformation saveTransformation(Transformation transformation);

    Transformation updateTransformation(Long id, Transformation transformation);

    boolean existByResolutionNumber(String resolutionNumber);

}
