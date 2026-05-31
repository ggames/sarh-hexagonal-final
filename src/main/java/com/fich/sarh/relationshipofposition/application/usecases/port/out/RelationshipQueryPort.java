package com.fich.sarh.relationshipofposition.application.usecases.port.out;

public interface RelationshipQueryPort {
    boolean existsPath(Long startId, Long targetId);
}
