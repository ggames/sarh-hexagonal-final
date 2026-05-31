package com.fich.sarh.point.infrastructure.adapter.output.persistence.adapter;

import com.fich.sarh.common.PersistenceAdapter;
import com.fich.sarh.point.domain.model.Point;
import com.fich.sarh.point.domain.ports.outbound.PointSpiPort;
import com.fich.sarh.point.infrastructure.adapter.input.rest.model.response.PointResponse;
import com.fich.sarh.point.infrastructure.adapter.output.persistence.mapper.PointMapper;
import com.fich.sarh.point.infrastructure.adapter.output.persistence.repository.PointRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;


@PersistenceAdapter
@RequiredArgsConstructor
public class PointPersistenceAdapter implements PointSpiPort {

    private final PointRepository pointRepository;

    private final PointMapper mapper;


    @Override
    public List<Point> findAllPoints() {
        return pointRepository.findAll().stream().map(
               mapper::toDto
        ).toList();
    }

    @Override
    public Optional<Point> findPointById(Long id) {
        return pointRepository.findById(id).map(
                mapper::toDto
        );
    }


    @Override
    public Point savePoint(Point point) {

        return mapper.toDto(
                pointRepository.save(PointMapper.INSTANCE.toEntity(point))
        );

    }

    @Override
    public Point updateSave(Point request) {
        var entity = mapper.toEntity(request);
        var saved = pointRepository.save(entity);
        return mapper.toDto(saved);
    }
}
