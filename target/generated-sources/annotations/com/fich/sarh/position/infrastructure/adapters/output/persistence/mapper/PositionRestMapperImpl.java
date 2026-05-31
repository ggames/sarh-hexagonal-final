package com.fich.sarh.position.infrastructure.adapters.output.persistence.mapper;

import com.fich.sarh.position.domain.model.Position;
import com.fich.sarh.position.infrastructure.adapters.inbound.rest.model.request.PositionRequest;
import com.fich.sarh.position.infrastructure.adapters.inbound.rest.model.response.PositionResponse;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-29T18:48:51-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.9 (Microsoft)"
)
@Component
public class PositionRestMapperImpl implements PositionRestMapper {

    @Override
    public PositionResponse toPositionResponse(Position position) {
        if ( position == null ) {
            return null;
        }

        PositionResponse.PositionResponseBuilder positionResponse = PositionResponse.builder();

        positionResponse.positionStatus( position.getPositionStatus() );
        positionResponse.pointsAvailable( position.getPointsAvailable() );

        return positionResponse.build();
    }

    @Override
    public Position toPosition(PositionRequest request) {
        if ( request == null ) {
            return null;
        }

        Position.PositionBuilder position = Position.builder();

        position.positionStatus( request.getPositionStatus() );
        position.pointsAvailable( request.getPointsAvailable() );

        return position.build();
    }

    @Override
    public List<PositionResponse> toPositionResponseList(List<Position> positionList) {
        if ( positionList == null ) {
            return null;
        }

        List<PositionResponse> list = new ArrayList<PositionResponse>( positionList.size() );
        for ( Position position : positionList ) {
            list.add( toPositionResponse( position ) );
        }

        return list;
    }
}
