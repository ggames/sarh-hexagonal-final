package com.fich.sarh.positiontype.infrastructure.adapter.outbound.persistence.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "Tipos-cargos")
public class PositionTypeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "cargo")
    String namePosition;
    @Column(name = "salario_basico")
    Double basicSalary;
    @Column(name = "cantidad_puntos_por_cargo")
    int amountOfPointPerPosition;

}