package com.fich.sarh.relationshipofposition.infrastructure.adapters.output.persistence.adapter;

import com.fich.sarh.common.PersistenceAdapter;
import com.fich.sarh.relationshipofposition.domain.model.RelationshipPosition;
import com.fich.sarh.relationshipofposition.domain.ports.outbound.RelationshipPositionSpiPort;
import com.fich.sarh.relationshipofposition.infrastructure.adapters.output.persistence.mapper.RelationshipPositionMapper;
import com.fich.sarh.relationshipofposition.infrastructure.adapters.output.persistence.repository.RelationshipPositionRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@PersistenceAdapter
@RequiredArgsConstructor
public class RelationshipPositionPersistenceAdapter implements RelationshipPositionSpiPort {

    private final RelationshipPositionRepository relationShipPositionRepository;
    private final RelationshipPositionMapper mapper;

    @Override
    public List<RelationshipPosition> findAllRelationshipPositions() {
        return  mapper.toDtoList(relationShipPositionRepository.findAll());
    }

    @Override
    public Optional<RelationshipPosition> findRelationshipPositionById(Long id) {
        return relationShipPositionRepository.findById(id).map(mapper::toDto);
    }



    @Override
    public void deleteRelationshipPosition(Long id) {
        relationShipPositionRepository.deleteById(id);
    }

    @Override
    public boolean existsByPositionId(Long positionId) {
        return relationShipPositionRepository.existsByParent_IdOrChild_Id(positionId, positionId);
    }
}
