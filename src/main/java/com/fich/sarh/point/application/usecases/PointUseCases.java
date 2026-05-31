package com.fich.sarh.point.application.usecases;

import com.fich.sarh.common.UseCase;
import com.fich.sarh.common.exceptions.ResourceNotFoundException;
import com.fich.sarh.point.domain.model.Point;
import com.fich.sarh.point.domain.ports.inbound.PointApiPort;
import com.fich.sarh.point.domain.ports.outbound.PointSpiPort;
import com.fich.sarh.point.infrastructure.adapter.input.rest.model.request.PointRequest;
import com.fich.sarh.point.infrastructure.adapter.input.rest.model.response.PointResponse;
import com.fich.sarh.point.infrastructure.adapter.output.persistence.mapper.PointRestMapper;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
@UseCase
@RequiredArgsConstructor
public class PointUseCases implements PointApiPort {

    private final PointSpiPort pointSpiPort;
    private final PointRestMapper restMapper;

    @Override
    public List<Point> findAllPoints() {
        return pointSpiPort.findAllPoints();
    }

    @Override
    public Optional<Point> findPointById(Long id) {
        return pointSpiPort.findPointById(id);
    }

    @Override
    public PointResponse savePoint(PointRequest request) {
        var point = restMapper.PointRequestToPoint(request);
        var saved = pointSpiPort.savePoint(point);
        return restMapper.PointToPointResponse(saved);
    }

    @Override
    public PointResponse updatePoint(Long id, PointRequest request) {
        var point = pointSpiPort.findPointById(id)
                .orElseThrow( () -> new ResourceNotFoundException("No existe el tipo de cargo seleccionado"));
            point.setAmountPoint(request.getAmountPoint());
            point.setDate(request.getDate());
            point.setDedication(request.getDedication());
            point.setPositionCode(request.getPositionCode());
            point.setNamePosition(request.getNamePosition());
        var updated = pointSpiPort.updateSave(point);
        return restMapper.PointToPointResponse(updated);
    }

    @Override
    public void applyGlobalParity(double percentage) {

    }

    @Override
    public void applyParityByPositionType(Long id, Long amount_point) {

    }
}
