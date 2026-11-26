package com.fich.sarh.positiontype.domain.ports.inbound;

import com.fich.sarh.positiontype.domain.model.PositionType;

import java.util.List;
import java.util.Optional;

public interface PositionTypeApiPort {
    List<PositionType> findAllPositionType();
    Optional<PositionType> findPositionTypeById(Long id);
    PositionType savePositionType(PositionType positionType);
    PositionType updatePositionType(Long id, PositionType positionType);
    void deletePositionType(Long id);
}
