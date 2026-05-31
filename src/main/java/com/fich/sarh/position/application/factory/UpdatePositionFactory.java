package com.fich.sarh.position.application.factory;

import com.fich.sarh.position.application.UpdateType;
import com.fich.sarh.position.application.strategy.AvailablePointUpdatePositionStrategy;
import com.fich.sarh.position.application.strategy.FullUpdatePositionStrategy;
import com.fich.sarh.position.application.strategy.OriginatorUpdateStrategy;
import com.fich.sarh.position.application.strategy.UpdatePositionStrategy;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UpdatePositionFactory {
    private final FullUpdatePositionStrategy fullStrategy;
    private final AvailablePointUpdatePositionStrategy availableStrategy;
    private final OriginatorUpdateStrategy originatorStrategy;

    public UpdatePositionStrategy<?> getStrategy(UpdateType type){
        return switch (type) {
            case FULL -> fullStrategy;
            case AVAILABLE_POINT -> originatorStrategy;
            case ORIGINATOR -> originatorStrategy;
        };
    }
}
