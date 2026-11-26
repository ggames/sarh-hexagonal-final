package com.fich.sarh.auth.infrastructure.adapter.output.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "auth_refresh_token", indexes = {
        @Index(name = "idx_auth_refresh_family", columnList = "family_id"),
        @Index(name = "idx_auth_refresh_user", columnList = "user_id")
})
public class RefreshTokenEntity {

    @Id
    @Column(name = "jti", nullable = false, length = 64, updatable = false)
    private String jti;

    @Column(name = "family_id", nullable = false, length = 64)
    private String familyId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "used_at")
    private Instant usedAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;
}
