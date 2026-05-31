package com.fich.sarh.position.domain.ports.outbound;

import com.fich.sarh.common.StatusOfPositions;
import com.fich.sarh.position.domain.model.Position;
import com.fich.sarh.position.domain.model.PositionDto;

import java.util.List;
import java.util.Optional;

public interface PositionSpiPort {
    // boolean existsOriginPositionId(Long id_generatePosition);
    List<PositionDto> findOriginPositions(Long id_generatePosition);
    List<PositionDto> findAllPositions();
    List<Position> findAllPosition();
    List<PositionDto> findVacantPositions();
    List<PositionDto>findFreePositions();
    List<Position> findAllByIdIn(List<Long> ids);
    List<Position> findAvailablePosition(StatusOfPositions status);
    Optional<Position> findPositionById(Long id);
   // Position updatePosition(Position command);
    Position savePosition(Position position);
    Position updatePosition(Position position);
    void deletePosition(Long id);
}
