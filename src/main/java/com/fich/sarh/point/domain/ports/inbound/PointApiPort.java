package com.fich.sarh.point.domain.ports.inbound;

import com.fich.sarh.point.domain.model.Point;

import java.util.List;
import java.util.Optional;

public interface PointApiPort {
    List<Point> findAllPoints();
    Optional<Point> findPointById(Long id);
    Point savePoint(Point request);
    Point updatePoint(Long id, Point request);
    void applyGlobalParity(double percentage);
    void applyParityByPositionType(Long id, Long amount_point);
}
