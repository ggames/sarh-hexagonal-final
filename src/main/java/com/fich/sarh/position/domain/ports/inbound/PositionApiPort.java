package com.fich.sarh.position.domain.ports.inbound;

import com.fich.sarh.common.StatusOfPositions;
import com.fich.sarh.position.domain.model.Position;
import com.fich.sarh.position.domain.model.PositionCommand;
import com.fich.sarh.position.domain.model.PositionDto;

import java.util.List;
import java.util.Optional;

public interface PositionApiPort {

    List<PositionDto> findOriginPositions(Long id_generatePosition);
    List<PositionDto> findAllPositions();
    List<Position> findAllPosition();
    List<PositionDto> findVacantPositions();
    List<PositionDto>findFreePositions();
    List<Position> findAllByIdIn(List<Long> ids);
    List<Position> findAvailablePosition(StatusOfPositions status);
    Optional<Position> findPositionById(Long id);

    Position updateFullPosition(Long id, PositionCommand command);
    Position updateAvailablePoint(Long id, Position command);
    Position updateOriginatorRelation(Long id,Position command);

    Position addPosition(PositionCommand command);
    void deletePosition(Long id);
}
