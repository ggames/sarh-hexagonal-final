package com.fich.sarh.positiontype.domain.ports.outbound;

import com.fich.sarh.positiontype.domain.model.PositionType;

import java.util.List;
import java.util.Optional;

public interface PositionTypeSpiPort {
    List<PositionType> findAllPositionType();
    Optional<PositionType> findPositionTypeById(Long id);
    PositionType savePositionType(PositionType command);
}
