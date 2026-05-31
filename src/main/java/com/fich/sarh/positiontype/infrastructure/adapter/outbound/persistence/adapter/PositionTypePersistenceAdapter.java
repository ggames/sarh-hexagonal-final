package com.fich.sarh.positiontype.infrastructure.adapter.outbound.persistence.adapter;

import com.fich.sarh.common.PersistenceAdapter;
import com.fich.sarh.positiontype.domain.model.PositionType;
import com.fich.sarh.positiontype.domain.ports.outbound.PositionTypeSpiPort;
import com.fich.sarh.positiontype.infrastructure.adapter.outbound.persistence.entity.PositionTypeEntity;
import com.fich.sarh.positiontype.infrastructure.adapter.outbound.persistence.mapper.PositionTypeMapper;
import com.fich.sarh.positiontype.infrastructure.adapter.outbound.persistence.repository.PositionTypeRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@PersistenceAdapter
@RequiredArgsConstructor
public class PositionTypePersistenceAdapter implements
        PositionTypeSpiPort {

    private final PositionTypeRepository positionTypeRepository;
    private final PositionTypeMapper mapper;


    @Override
    public List<PositionType> findAllPositionType() {
        return positionTypeRepository.findAll()
                .stream().map(mapper::toPositionType ).toList();
    }

    @Override
    public Optional<PositionType> findPositionTypeById(Long id) {
        Optional<PositionType> positionType = positionTypeRepository
                                       .findById(id).map(mapper::toPositionType);
        return positionType;
    }

    @Override
    public PositionType savePositionType(PositionType position) {
        PositionTypeEntity entity = mapper.toPositionTypeEntity(position);
        return mapper.toPositionType(positionTypeRepository.save(entity));

    }



}
