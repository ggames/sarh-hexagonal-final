package com.fich.sarh.movement.infrastructure.adapters.outbound.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Embeddable
@ToString
public class MovementId implements Serializable {

    @Column(name = "planta_id")
    private Long plantId;
    @Column(name = "cargo_id")
    private Long positionId;
}
