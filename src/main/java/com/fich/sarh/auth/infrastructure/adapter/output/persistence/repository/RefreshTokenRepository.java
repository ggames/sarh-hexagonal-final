package com.fich.sarh.auth.infrastructure.adapter.output.persistence.repository;

import com.fich.sarh.auth.infrastructure.adapter.output.persistence.entity.RefreshTokenEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from RefreshTokenEntity t where t.jti = :jti")
    Optional<RefreshTokenEntity> findByJtiForUpdate(@Param("jti") String jti);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update RefreshTokenEntity t set t.revokedAt = :revokedAt " +
            "where t.familyId = :familyId and t.revokedAt is null")
    void revokeFamily(@Param("familyId") String familyId, @Param("revokedAt") Instant revokedAt);
}
