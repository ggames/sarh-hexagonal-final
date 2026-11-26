package com.fich.sarh.auth.application.ports.output.persistence;

import com.fich.sarh.auth.application.model.RefreshTokenClaims;
import org.springframework.security.core.Authentication;

public interface TokenProviderPort {

    String createAccessToken(Authentication authentication, boolean mustChangePassword);

    String createRefreshToken(Authentication authentication);

    RefreshTokenClaims validateRefreshToken(String refreshToken);
}
