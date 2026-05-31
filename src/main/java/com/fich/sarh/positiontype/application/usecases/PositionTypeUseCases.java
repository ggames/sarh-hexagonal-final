package com.fich.sarh.positiontype.application.usecases;

import com.fich.sarh.common.UseCase;
import com.fich.sarh.positiontype.domain.model.PositionType;
import com.fich.sarh.positiontype.domain.ports.inbound.PositionTypeApiPort;
import com.fich.sarh.positiontype.infrastructure.adapter.inbound.rest.model.request.PositionTypeRequest;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@UseCase
@RequiredArgsConstructor
public class PositionTypeUseCases implements PositionTypeApiPort {
    @Override
    public List<PositionType> findAllPositionType() {
        return List.of();
    }

    @Override
    public Optional<PositionType> findPositionTypeById(Long id) {
        return Optional.empty();
    }

    @Override
    public PositionType savePositionType(PositionTypeRequest command) {
        return null;
    }

    @Override
    public PositionType updatePositionType(Long id, PositionTypeRequest command) {
        return null;
    }
}
