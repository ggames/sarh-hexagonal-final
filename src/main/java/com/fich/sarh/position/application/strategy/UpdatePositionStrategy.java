package com.fich.sarh.position.application.strategy;

import com.fich.sarh.position.domain.model.Position;

public interface UpdatePositionStrategy<C> {

    Position update(Long id, C command);

}
