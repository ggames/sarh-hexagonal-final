package com.fich.sarh.relationshipofposition.infrastructure.adapters.output.persistence.repository;

import com.fich.sarh.relationshipofposition.infrastructure.adapters.output.persistence.entity.RelationshipPositionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface RelationshipPositionRepository extends JpaRepository<RelationshipPositionEntity, Long> {
   // @Query("""
   // SELECT r FROM RelationshipPositionEntity r
  //  WHERE r.parent.id = :parentId
  // """)
    List<RelationshipPositionEntity> findByParentId(Long parentId);
}
