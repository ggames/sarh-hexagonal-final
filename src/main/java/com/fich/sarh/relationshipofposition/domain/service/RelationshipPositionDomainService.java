package com.fich.sarh.relationshipofposition.domain.service;

import com.fich.sarh.common.exceptions.BusinessRuleViolationException;
import com.fich.sarh.position.domain.model.Position;
import com.fich.sarh.relationshipofposition.application.usecases.port.out.RelationshipQueryPort;
import lombok.RequiredArgsConstructor;

import java.util.List;


@RequiredArgsConstructor
public class RelationshipPositionDomainService {

    private final RelationshipQueryPort relationshipQueryPort;

    public void processRelationship(Position child, List<Position> parents) {
          if(parents == null || parents.isEmpty()){
              return;
          }

          validateBusinessRules(child,parents);
          validateNoCycles(child,parents);

          parents.forEach(child::addParent);

    }

    private void validateBusinessRules(Position child, List<Position> parents){
          // 1. No duplicados
        Long uniqueCount = parents.stream().map(Position::getId)
                           .distinct().count();

        if(uniqueCount != parents.size()){
            throw new BusinessRuleViolationException("Existen cargos origen duplicados");

        }

        // 2. No Null
        boolean hasNulls = parents.stream().anyMatch(p -> p == null || p.getId() == null);

        if(hasNulls){
            throw new BusinessRuleViolationException("Existen cargos de origen invalidos");
        }

        // 3. No auto-referencia
        if(child.getId() != null){
            boolean selfReference = parents.stream()
                    .anyMatch(p -> p.getId().equals(child.getId()));
            if(selfReference){
                throw new BusinessRuleViolationException("Un cargo no puede ser origen de si mismo");
            }
        }


    }

    private void validateNoCycles(Position child, List<Position> parents){

       // Si child aún no esta persistido, no tiene ID --> no puede haber ciclos todavia
        if(child.getId() == null) {
            return;
        }

        for(Position parent: parents){
            // Caso directo
            if(parent.getId().equals(child.getId())){
                throw new BusinessRuleViolationException("Relación cíclica directa detectada");
            }

            // Caso indirecto
            boolean cycleExists = relationshipQueryPort.existsPath(parent.getId(), child.getId());
            if(cycleExists){
                throw new BusinessRuleViolationException("Se dectecto un ciclo en la jerarquia de cargos (parentId= "+
                        parent.getId() + " , childId= " + child.getId() + ")" );
            }
        }

    }
}
