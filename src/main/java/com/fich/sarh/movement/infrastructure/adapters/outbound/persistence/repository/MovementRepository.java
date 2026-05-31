package com.fich.sarh.movement.infrastructure.adapters.outbound.persistence.repository;

import com.fich.sarh.movement.infrastructure.adapters.outbound.persistence.entity.MovementEntity;
import com.fich.sarh.plantofpositions.infrastructure.adapters.outbound.persistence.entity.PlantOfPositionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MovementRepository extends JpaRepository<MovementEntity, Long> {
    @Query("SELECT m FROM MovementEntity m WHERE m.plant = ?1")
    MovementEntity fetchMovementByPlant(PlantOfPositionEntity plant);
  /*  @Query("SELECT m FROM MovementEntity m LEFT JOIN m.plant pl WHERE pl.currentStatusID IN ?1")
   List<MovementEntity> fetchMovementWithInactiveAgent(List<PlantStatus> status);*/

}
