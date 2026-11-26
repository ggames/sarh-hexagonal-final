package com.fich.sarh.positiontype.application.usecases;

import com.fich.sarh.common.UseCase;
import com.fich.sarh.common.exceptions.ResourceNotFoundException;
import com.fich.sarh.positiontype.domain.model.PositionType;
import com.fich.sarh.positiontype.domain.ports.inbound.PositionTypeApiPort;
import com.fich.sarh.positiontype.domain.ports.outbound.PositionTypeSpiPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@UseCase
@RequiredArgsConstructor
public class PositionTypeUseCases implements PositionTypeApiPort {

    private final PositionTypeSpiPort positionTypeSpiPort;

    @Override
    public List<PositionType> findAllPositionType() {
        return positionTypeSpiPort.findAllPositionType();
    }

    @Override
    public Optional<PositionType> findPositionTypeById(Long id) {
        return positionTypeSpiPort.findPositionTypeById(id);
    }

    @Override
    @Transactional
    public PositionType savePositionType(PositionType command) {
        return positionTypeSpiPort.savePositionType(command);
    }

    @Override
    @Transactional
    public PositionType updatePositionType(Long id, PositionType command) {
        PositionType existing = positionTypeSpiPort.findPositionTypeById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de cargo no encontrado"));
        existing.setNamePosition(command.getNamePosition());
        return positionTypeSpiPort.savePositionType(existing);
    }

    @Override
    @Transactional
    public void deletePositionType(Long id) {
        positionTypeSpiPort.findPositionTypeById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de cargo no encontrado con ID: " + id));
        positionTypeSpiPort.deletePositionType(id);
    }
}
