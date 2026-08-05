package com.fich.sarh.position.application.factory;

import com.fich.sarh.position.application.UpdateType;
import com.fich.sarh.position.application.strategy.UpdatePositionStrategy;

public interface IUpdatePositionFactory {
    public UpdatePositionStrategy<?> getStrategy(UpdateType type);
}
