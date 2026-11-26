package com.fich.sarh.point.application.usecases;

import com.fich.sarh.common.UseCase;
import com.fich.sarh.common.exceptions.BusinessRuleViolationException;
import com.fich.sarh.common.exceptions.ResourceNotFoundException;
import com.fich.sarh.point.domain.model.Point;
import com.fich.sarh.point.domain.ports.inbound.PointApiPort;
import com.fich.sarh.point.domain.ports.outbound.PointSpiPort;
import com.fich.sarh.position.domain.ports.inbound.PositionApiPort;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Lazy;

import java.util.List;
import java.util.Optional;

@UseCase
public class PointUseCases implements PointApiPort {

    private final PointSpiPort pointSpiPort;
    private final PositionApiPort positionApiPort;

    public PointUseCases(
            PointSpiPort pointSpiPort,
            @Lazy PositionApiPort positionApiPort) {
        this.pointSpiPort = pointSpiPort;
        this.positionApiPort = positionApiPort;
    }

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
    @Transactional
    public void applyGlobalParity(double percentage) {
        double percent = percentage / 100.0;
        List<Point> points = pointSpiPort.findAllPoints();
        for (Point point : points) {
            double prevPoint = point.getAmountPoint() != null ? point.getAmountPoint().doubleValue() : 0.0;
            double newPoint = prevPoint * (1.0 + percent);
            point.setAmountPoint(Math.round(newPoint));
            pointSpiPort.updateSave(point);
        }
    }

    @Override
    @Transactional
    public void applyParityByPositionType(Long id, Long amount_point) {
        var point = pointSpiPort.findPointById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el tipo de cargo seleccionado"));
        point.setAmountPoint(amount_point);
        pointSpiPort.updateSave(point);
    }

    @Override
    @Transactional
    public void deletePoint(Long id) {
        pointSpiPort.findPointById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Punto/Categoría no encontrado con ID: " + id));

        if (positionApiPort.existsByPointId(id)) {
            throw new BusinessRuleViolationException("No se puede eliminar el punto/categoría porque está asignado a uno o más cargos.");
        }

        pointSpiPort.deletePoint(id);
    }
}
