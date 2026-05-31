package com.fich.sarh.relationshipofposition.infrastructure.adapters.output.persistence.adapter;

import com.fich.sarh.relationshipofposition.application.usecases.port.out.RelationshipQueryPort;
import com.fich.sarh.relationshipofposition.infrastructure.adapters.output.persistence.entity.RelationshipPositionEntity;
import com.fich.sarh.relationshipofposition.infrastructure.adapters.output.persistence.repository.RelationshipPositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class RelationshipQueryAdapter implements RelationshipQueryPort {

    private final RelationshipPositionRepository repository;
    @Override
    public boolean existsPath(Long startId, Long targetId) {
        Set<Long> visited = new HashSet<>();
        Queue<Long> queue = new LinkedList<>();

        queue.add(startId);

        while (!queue.isEmpty()){
            Long current = queue.poll();
            if(current.equals(targetId)){
                return true;
            }
            if(!visited.add(current)) continue;

            List<RelationshipPositionEntity> relations = repository.findByParentId(current);

            relations.forEach(rel -> queue.add(rel.getChild().getId()));
        }
        return false;
    }
}
