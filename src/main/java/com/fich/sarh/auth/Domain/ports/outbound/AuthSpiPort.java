package com.fich.sarh.auth.Domain.ports.outbound;

import com.fich.sarh.auth.Infrastructure.adapter.inbound.rest.model.request.LoginRequest;
import com.fich.sarh.auth.Infrastructure.adapter.inbound.rest.model.response.AuthResponse;

public interface AuthSpiPort {
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(String refreshToken);
}
