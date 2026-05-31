package com.fich.sarh.position.infrastructure.adapters.output.persistence.entity;

import com.fich.sarh.common.StatusOfPositions;
import com.fich.sarh.movement.infrastructure.adapters.outbound.persistence.entity.MovementEntity;
import com.fich.sarh.organizationalunit.infrastructure.adapters.outbound.persistence.entity.OrganizationalUnitEntity;
import com.fich.sarh.point.infrastructure.adapter.output.persistence.entity.PointEntity;
import com.fich.sarh.relationshipofposition.infrastructure.adapters.output.persistence.entity.RelationshipPositionEntity;
import com.fich.sarh.transformation.infrastructure.adapters.outbound.persistence.entity.TransformationEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
@Entity
@Table(name = "cargos")
public class PositionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    boolean active;

    @JoinColumn(name = "punto_id")
    @ManyToOne(fetch = FetchType.LAZY)
    PointEntity point;

    @JoinColumn(name = "unidad_organizativa_id")
    @ManyToOne(fetch = FetchType.LAZY)
    OrganizationalUnitEntity organizationalUnit;

    @Column(name = "estado_cargo")
    @Enumerated(EnumType.ORDINAL)
    StatusOfPositions positionStatus;

    @Column(name = "puntos_disp")
    Long pointsAvailable;

    @JoinColumn(name = "resolucion_creacion_id")
    @ManyToOne(targetEntity = TransformationEntity.class)
    TransformationEntity creationResolution;

    @JoinColumn(name = "resolucion_supresion_id")
    @ManyToOne(targetEntity = TransformationEntity.class)
    TransformationEntity resolutionSuppression;

    @Builder.Default
    @OneToMany(mappedBy = "position", cascade = CascadeType.ALL)
    List<MovementEntity> movements = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "child",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    List<RelationshipPositionEntity> parentRelations = new ArrayList<>();


    // =====================================================
    // HELPERS
    // =====================================================

    public void addParent(PositionEntity parent){

        if(parent == null){
            return;
        }

        if(this.parentRelations == null){
            this.parentRelations = new ArrayList<>();
        }

        boolean exists = this.parentRelations.stream()
                .anyMatch(rel ->
                        rel.getParent() != null
                                && rel.getParent().getId() != null
                                && rel.getParent().getId()
                                .equals(parent.getId())
                );

        if(exists){
            return;
        }

        RelationshipPositionEntity rel =
                new RelationshipPositionEntity();

        rel.setParent(parent);
        rel.setChild(this);

        this.parentRelations.add(rel);
    }

    public void removeParent(
            RelationshipPositionEntity relation
    ){

        if(this.parentRelations == null){
            return;
        }

        this.parentRelations.remove(relation);
    }

    public void clearParents(){

        if(this.parentRelations != null){
            this.parentRelations.clear();
        }
    }

    public void replaceParents(
            List<PositionEntity> parents
    ){

        clearParents();

        if(parents == null){
            return;
        }

        for(PositionEntity parent : parents){
            addParent(parent);
        }
    }
}
