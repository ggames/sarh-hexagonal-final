package com.fich.sarh.point.domain.ports.inbound;

import com.fich.sarh.point.domain.model.Point;
import com.fich.sarh.point.infrastructure.adapter.input.rest.model.request.PointRequest;
import com.fich.sarh.point.infrastructure.adapter.input.rest.model.response.PointResponse;

import java.util.List;
import java.util.Optional;

public interface PointApiPort {
    List<Point> findAllPoints();
    Optional<Point> findPointById(Long id);
    PointResponse savePoint(PointRequest request);
    PointResponse updatePoint(Long id, PointRequest request);
    void applyGlobalParity(double percentage);
    void applyParityByPositionType(Long id, Long amount_point);
}
