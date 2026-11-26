package com.fich.sarh.relationshipofposition.domain.ports.outbound;

public interface RelationshipQueryPort {
    boolean existsPath(Long startId, Long targetId);
}
