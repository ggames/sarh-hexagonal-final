package com.fich.sarh.auth.infrastructure.adapter.output.persistence.adapter;

import com.fich.sarh.auth.application.ports.output.persistence.RefreshTokenSpiPort;
import com.fich.sarh.auth.domain.model.RefreshToken;
import com.fich.sarh.auth.infrastructure.adapter.output.persistence.entity.RefreshTokenEntity;
import com.fich.sarh.auth.infrastructure.adapter.output.persistence.repository.RefreshTokenRepository;
import com.fich.sarh.common.PersistenceAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@PersistenceAdapter
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefreshTokenPersistenceAdapter implements RefreshTokenSpiPort {

    private final RefreshTokenRepository repository;

    @Override
    @Transactional
    public Optional<RefreshToken> findByJtiForUpdate(String jti) {
        return repository.findByJtiForUpdate(jti).map(this::toDomain);
    }

    @Override
    @Transactional
    public RefreshToken save(RefreshToken refreshToken) {
        return toDomain(repository.save(toEntity(refreshToken)));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void revokeFamily(String familyId, Instant revokedAt) {
        repository.revokeFamily(familyId, revokedAt);
    }

    private RefreshToken toDomain(RefreshTokenEntity entity) {
        return RefreshToken.builder()
                .jti(entity.getJti())
                .familyId(entity.getFamilyId())
                .userId(entity.getUserId())
                .expiresAt(entity.getExpiresAt())
                .createdAt(entity.getCreatedAt())
                .usedAt(entity.getUsedAt())
                .revokedAt(entity.getRevokedAt())
                .build();
    }

    private RefreshTokenEntity toEntity(RefreshToken token) {
        return RefreshTokenEntity.builder()
                .jti(token.getJti())
                .familyId(token.getFamilyId())
                .userId(token.getUserId())
                .expiresAt(token.getExpiresAt())
                .createdAt(token.getCreatedAt())
                .usedAt(token.getUsedAt())
                .revokedAt(token.getRevokedAt())
                .build();
    }
}
