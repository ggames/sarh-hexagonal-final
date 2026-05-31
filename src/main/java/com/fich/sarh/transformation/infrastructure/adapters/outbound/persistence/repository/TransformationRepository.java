package com.fich.sarh.transformation.infrastructure.adapters.outbound.persistence.repository;

import com.fich.sarh.transformation.infrastructure.adapters.outbound.persistence.entity.TransformationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransformationRepository extends JpaRepository<TransformationEntity, Long> {

    Optional<TransformationEntity>  findByResolutionNumber(String resolution);

    Optional<TransformationEntity> findFirstByOrderByIdDesc();

    boolean existsByResolutionNumber(String resolutionNumber);
}
