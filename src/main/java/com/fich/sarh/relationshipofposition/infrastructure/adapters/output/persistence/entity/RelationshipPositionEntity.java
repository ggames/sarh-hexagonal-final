package com.fich.sarh.relationshipofposition.infrastructure.adapters.output.persistence.entity;


import com.fich.sarh.position.infrastructure.adapters.output.persistence.entity.PositionEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Setter @Getter
@AllArgsConstructor @NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "relationship_position")
public class RelationshipPositionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_position_id")
    PositionEntity parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_position_id")
    PositionEntity child;
}
