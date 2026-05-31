package com.fich.sarh.transformation.infrastructure.adapters.outbound.persistence.adapter;

import com.fich.sarh.common.WebAdapter;
import com.fich.sarh.common.exceptions.ResourceNotFoundException;
import com.fich.sarh.transformation.domain.model.Transformation;
import com.fich.sarh.transformation.domain.ports.outbound.TransformationSpiPort;
import com.fich.sarh.transformation.infrastructure.adapters.outbound.persistence.mapper.TransformationMapper;
import com.fich.sarh.transformation.infrastructure.adapters.outbound.persistence.repository.TransformationRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@WebAdapter
@RequiredArgsConstructor
public class TransformationPersistenceAdapter implements TransformationSpiPort {

    private final TransformationRepository transformationRepository;
    private final TransformationMapper mapper;

    @Override
    public List<Transformation> findAllTransformations() {
        return transformationRepository.findAll()
                .stream().map(mapper::toDto).toList();
    }

    @Override
    public Optional<Transformation> findTransformationById(Long id) {

        return transformationRepository.findById(id).map(mapper::toDto);
    }

    @Override
    public Optional<Transformation> findByResolutionNumber(String resolutionNumber) {
        return transformationRepository
                .findByResolutionNumber(resolutionNumber).map(mapper::toDto);
    }

    @Override
    public Optional<Transformation> findFirstByOrderDesc() {
        return transformationRepository.findFirstByOrderByIdDesc()
                .map(mapper::toDto);
    }

    @Override
    public Transformation saveTransformation(Transformation transformation) {
        var entity = mapper.toEntity(transformation);
        return mapper.toDto(transformationRepository.save(entity));
    }

    @Override
    public Transformation updateTransformation(Long id, Transformation transformation) {
        var entity = transformationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transformación"));

        entity.setDate(transformation.getDate());
        entity.setReason(transformation.getReason());
        entity.setResolutionNumber(transformation.getResolutionNumber());

        return mapper.toDto(transformationRepository.save(entity));
    }

    @Override
    public boolean existByResolutionNumber(String resolutionNumber) {

        return transformationRepository.existsByResolutionNumber(resolutionNumber);
    }
}
