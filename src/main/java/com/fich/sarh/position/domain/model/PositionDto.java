package com.fich.sarh.position.domain.model;

import com.fich.sarh.common.StatusOfPositions;


public interface PositionDto {

    Long getId();
    String getNamePosition();
    String getNameUnit();
    Long getPointsAvailable();
    Long getAmountPoint();
    StatusOfPositions getPositionStatus();
    String getResolutionNumber();


}
