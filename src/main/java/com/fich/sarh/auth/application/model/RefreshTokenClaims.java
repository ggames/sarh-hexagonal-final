package com.fich.sarh.auth.application.model;

import java.time.Instant;

public record RefreshTokenClaims(String jti, String username, Instant expiresAt) {
}
