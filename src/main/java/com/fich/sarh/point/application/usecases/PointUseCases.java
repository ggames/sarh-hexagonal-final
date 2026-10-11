package com.fich.sarh.point.application.usecases;

import com.fich.sarh.common.UseCase;
import com.fich.sarh.common.exceptions.ResourceNotFoundException;
import com.fich.sarh.point.domain.model.Point;
import com.fich.sarh.point.domain.ports.inbound.PointApiPort;
import com.fich.sarh.point.domain.ports.outbound.PointSpiPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@UseCase
@RequiredArgsConstructor
public class PointUseCases implements PointApiPort {

    private final PointSpiPort pointSpiPort;

    @Override
    public List<Point> findAllPoints() {
        return pointSpiPort.findAllPoints();
    }

    @Override
    public Optional<Point> findPointById(Long id) {
        return pointSpiPort.findPointById(id);
    }

    @Override
    @Transactional
    public Point savePoint(Point request) {
        return pointSpiPort.savePoint(request);
    }

    @Override
    @Transactional
    public Point updatePoint(Long id, Point request) {
        var point = pointSpiPort.findPointById(id)
                .orElseThrow( () -> new ResourceNotFoundException("No existe el tipo de cargo seleccionado"));
            point.setAmountPoint(request.getAmountPoint());
            point.setDate(request.getDate());
            point.setDedication(request.getDedication());
            point.setPositionCode(request.getPositionCode());
            point.setNamePosition(request.getNamePosition());
        return pointSpiPort.updateSave(point);
    }

    @Override
    public void applyGlobalParity(double percentage) {

    }

    @Override
    public void applyParityByPositionType(Long id, Long amount_point) {

    }
}
