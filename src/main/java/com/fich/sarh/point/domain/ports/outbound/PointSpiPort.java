package com.fich.sarh.point.domain.ports.outbound;

import com.fich.sarh.point.domain.model.Point;

import java.util.List;
import java.util.Optional;

public interface PointSpiPort {
    List<Point> findAllPoints();
    Optional<Point> findPointById(Long id);
    Point savePoint(Point request);
    Point updateSave(Point request);
    void deletePoint(Long id);
}
