package com.fich.sarh.positiontype.infrastructure.adapter.outbound.persistence.repository;

import com.fich.sarh.positiontype.infrastructure.adapter.outbound.persistence.entity.PositionTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionTypeRepository extends JpaRepository<PositionTypeEntity, Long> {
}
