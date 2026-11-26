package com.fich.sarh.auth.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder(toBuilder = true)
public class RefreshToken {

    private final String jti;
    private final String familyId;
    private final Long userId;
    private final Instant expiresAt;
    private final Instant createdAt;
    private final Instant usedAt;
    private final Instant revokedAt;

    public boolean isUsable(Instant now) {
        return usedAt == null && revokedAt == null && expiresAt.isAfter(now);
    }

    public boolean wasUsed() {
        return usedAt != null;
    }
}
