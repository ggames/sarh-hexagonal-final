package com.fich.sarh.positiontype.domain.ports.inbound;

import com.fich.sarh.positiontype.domain.model.PositionType;
import com.fich.sarh.positiontype.infrastructure.adapter.inbound.rest.model.request.PositionTypeRequest;

import java.util.List;
import java.util.Optional;

public interface PositionTypeApiPort {
    List<PositionType> findAllPositionType();
    Optional<PositionType> findPositionTypeById(Long id);
    PositionType savePositionType(PositionTypeRequest request);
    PositionType updatePositionType(Long id, PositionTypeRequest request);
}
