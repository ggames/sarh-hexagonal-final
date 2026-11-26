package com.fich.sarh.auth.infrastructure.adapter.configuration.security.jwt;

import com.fich.sarh.auth.application.model.RefreshTokenClaims;
import com.fich.sarh.auth.application.ports.output.persistence.TokenProviderPort;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenProviderAdapter implements TokenProviderPort {

    private final JwtUtils jwtUtils;

    @Override
    public String createAccessToken(Authentication authentication, boolean mustChangePassword) {
        return jwtUtils.createToken(authentication, mustChangePassword);
    }

    @Override
    public String createRefreshToken(Authentication authentication) {
        return jwtUtils.createRefreshToken(authentication);
    }

    @Override
    public RefreshTokenClaims validateRefreshToken(String refreshToken) {
        DecodedJWT decoded = jwtUtils.validateRefreshToken(refreshToken);
        return new RefreshTokenClaims(
                decoded.getId(),
                decoded.getSubject(),
                decoded.getExpiresAt() == null ? null : decoded.getExpiresAt().toInstant());
    }
}
