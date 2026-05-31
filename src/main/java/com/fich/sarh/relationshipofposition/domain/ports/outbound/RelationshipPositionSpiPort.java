package com.fich.sarh.relationshipofposition.domain.ports.outbound;

import com.fich.sarh.relationshipofposition.domain.model.RelationshipPosition;

import java.util.List;
import java.util.Optional;

public interface RelationshipPositionSpiPort {

   List<RelationshipPosition> findAllRelationshipPositions();
   Optional<RelationshipPosition> findRelationshipPositionById(Long id);
  // void saveRelationshipPosition(RelationshipPosition relationShipPosition);
  // RelationshipPosition updateRelationshipPosition(RelationshipPosition relationShipPosition);
   void deleteRelationshipPosition(Long id);

}
