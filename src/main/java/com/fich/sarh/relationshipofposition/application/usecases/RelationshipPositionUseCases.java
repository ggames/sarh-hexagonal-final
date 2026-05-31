package com.fich.sarh.relationshipofposition.application.usecases;

import com.fich.sarh.common.UseCase;
import com.fich.sarh.position.domain.ports.outbound.PositionSpiPort;
import com.fich.sarh.relationshipofposition.domain.ports.inbound.RelationshipPositionApiPort;
import com.fich.sarh.relationshipofposition.domain.ports.outbound.RelationshipPositionSpiPort;
import com.fich.sarh.relationshipofposition.domain.service.RelationshipPositionDomainService;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class RelationshipPositionUseCases implements RelationshipPositionApiPort {

    private final PositionSpiPort positionPort;
    private final RelationshipPositionSpiPort relationshipPort;
    private final RelationshipPositionDomainService domainService;





    @Override
    public void deleteRelationShipPosition(Long id) {

    }
}
