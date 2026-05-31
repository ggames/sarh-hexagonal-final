package com.fich.sarh.relationshipofposition.infrastructure.adapters.output.persistence.adapter;

import com.fich.sarh.common.WebAdapter;
import com.fich.sarh.common.exceptions.BusinessRuleViolationException;
import com.fich.sarh.relationshipofposition.domain.model.RelationshipPosition;
import com.fich.sarh.relationshipofposition.domain.ports.outbound.RelationshipPositionSpiPort;
import com.fich.sarh.relationshipofposition.infrastructure.adapters.output.persistence.entity.RelationshipPositionEntity;
import com.fich.sarh.relationshipofposition.infrastructure.adapters.output.persistence.mapper.RelationshipPositionMapper;
import com.fich.sarh.relationshipofposition.infrastructure.adapters.output.persistence.repository.RelationshipPositionRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@WebAdapter
@RequiredArgsConstructor
public class RelationshipPositionPersistenceAdapter implements RelationshipPositionSpiPort {

    private final RelationshipPositionRepository relationShipPositionRepository;
    private RelationshipPositionMapper mapper;

    @Override
    public List<RelationshipPosition> findAllRelationshipPositions() {
        return  mapper.toDtoList(relationShipPositionRepository.findAll());   // List.of();
    }

    @Override
    public Optional<RelationshipPosition> findRelationshipPositionById(Long id) {
        Optional<RelationshipPositionEntity> relationShipPositionE = relationShipPositionRepository.findById(id);
        if(relationShipPositionE.isEmpty()){
            throw new BusinessRuleViolationException("No existe la relación");
        }
        return Optional.of(mapper.toDto(relationShipPositionE.get()));
    }



    @Override
    public void deleteRelationshipPosition(Long id) {

    }
}
