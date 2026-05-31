package com.fich.sarh.transformation.application.usecases;

import com.fich.sarh.common.UseCase;
import com.fich.sarh.common.exceptions.ResourceNotFoundException;
import com.fich.sarh.transformation.domain.model.Transformation;
import com.fich.sarh.transformation.domain.ports.inbound.TransformationApiPort;
import com.fich.sarh.transformation.domain.ports.outbound.TransformationSpiPort;
import com.fich.sarh.transformation.infrastructure.adapters.inbound.rest.mapper.TransformationRestMapper;
import com.fich.sarh.transformation.infrastructure.adapters.inbound.rest.model.request.TransformationRequest;
import lombok.RequiredArgsConstructor;

import java.util.List;

@UseCase
@RequiredArgsConstructor
public class TransformationUseCases implements TransformationApiPort {

    private final TransformationSpiPort transformationSpiPort;
    private final TransformationRestMapper restMapper;

    @Override
    public List<Transformation> findAllTransformations() {
        return transformationSpiPort.findAllTransformations();
    }

    @Override
    public Transformation findTransformationById(Long id) {
        return transformationSpiPort.findTransformationById(id)
                .orElseThrow( () -> new ResourceNotFoundException("Transformación"));
    }

    @Override
    public Transformation findByResolutionNumber(String resolutionNumber) {
        return transformationSpiPort.findByResolutionNumber(resolutionNumber)
                .orElseThrow(()-> new ResourceNotFoundException("Transformación"));
    }

    @Override
    public Transformation findFirstByOrderDesc() {
        return transformationSpiPort.findFirstByOrderDesc()
                .orElseThrow(()-> new ResourceNotFoundException("Transformación"));
    }

    @Override
    public Transformation saveTransformation(Transformation transformation) {

        return transformationSpiPort.saveTransformation(transformation);
    }

    @Override
    public Transformation updateTransformation(Long id, Transformation request) {

        // var transformation = restMapper.toTransformation(request);

        return transformationSpiPort.updateTransformation(id, request);
    }

    @Override
    public boolean existByResolutionNumber(String resolutionNumber) {

        return transformationSpiPort.existByResolutionNumber(resolutionNumber);
    }
}
