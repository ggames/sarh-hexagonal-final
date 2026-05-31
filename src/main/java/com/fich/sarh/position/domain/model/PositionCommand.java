package com.fich.sarh.position.domain.model;

import com.fich.sarh.common.StatusOfPositions;
import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PositionCommand {

    Long point;
    Long organizational;
    StatusOfPositions positionStatus;
    List<Long> originPositionIds;
    Long resolutionTransformation;

    @Override
    public String toString() {
        return "PositionCommand{" +
                "pointId=" + point +
                ", organizationalId=" + organizational +
                ", originPositionId=" + originPositionIds +
                ", resolutionTransformationId=" + resolutionTransformation +
                '}';
    }
}
