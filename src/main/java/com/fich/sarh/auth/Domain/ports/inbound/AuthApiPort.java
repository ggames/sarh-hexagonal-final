package com.fich.sarh.auth.Domain.ports.inbound;

import com.fich.sarh.auth.Infrastructure.adapter.inbound.rest.model.request.LoginRequest;
import com.fich.sarh.auth.Infrastructure.adapter.inbound.rest.model.response.AuthResponse;

public interface AuthApiPort {
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(String refreshToken);
}
