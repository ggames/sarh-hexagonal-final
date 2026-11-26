package com.fich.sarh.transformation.application.usecases;

import com.fich.sarh.common.UseCase;
import com.fich.sarh.common.exceptions.BusinessRuleViolationException;
import com.fich.sarh.common.exceptions.ResourceNotFoundException;
import com.fich.sarh.position.domain.ports.inbound.PositionApiPort;
import com.fich.sarh.transformation.domain.model.Transformation;
import com.fich.sarh.transformation.domain.ports.inbound.TransformationApiPort;
import com.fich.sarh.transformation.domain.ports.outbound.TransformationSpiPort;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Lazy;

import java.util.List;

@UseCase
public class TransformationUseCases implements TransformationApiPort {

    private final TransformationSpiPort transformationSpiPort;
    private final PositionApiPort positionApiPort;

    public TransformationUseCases(
            TransformationSpiPort transformationSpiPort,
            @Lazy PositionApiPort positionApiPort) {
        this.transformationSpiPort = transformationSpiPort;
        this.positionApiPort = positionApiPort;
    }

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
    @Transactional
    public Transformation saveTransformation(Transformation transformation) {
        if (transformation != null && transformation.getResolutionNumber() != null) {
            if (transformationSpiPort.existByResolutionNumber(transformation.getResolutionNumber())) {
                throw new BusinessRuleViolationException("La resolución de transformación ingresada ya existe");
            }
        }
        return transformationSpiPort.saveTransformation(transformation);
    }

    @Override
    @Transactional
    public Transformation updateTransformation(Long id, Transformation request) {

        // var transformation = restMapper.toTransformation(request);

        return transformationSpiPort.updateTransformation(id, request);
    }

    @Override
    public boolean existByResolutionNumber(String resolutionNumber) {

        return transformationSpiPort.existByResolutionNumber(resolutionNumber);
    }

    @Override
    @Transactional
    public void deleteTransformation(Long id) {
        transformationSpiPort.findTransformationById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resolución de transformación no encontrada con ID: " + id));

        if (positionApiPort.existsByTransformationId(id)) {
            throw new BusinessRuleViolationException("No se puede eliminar la resolución de transformación porque está asociada a uno o más cargos.");
        }

        transformationSpiPort.deleteTransformation(id);
    }
}
