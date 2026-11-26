package com.fich.sarh.auth.application.ports.output.persistence;

import com.fich.sarh.auth.domain.model.RefreshToken;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenSpiPort {

    Optional<RefreshToken> findByJtiForUpdate(String jti);

    RefreshToken save(RefreshToken refreshToken);

    void revokeFamily(String familyId, Instant revokedAt);
}
