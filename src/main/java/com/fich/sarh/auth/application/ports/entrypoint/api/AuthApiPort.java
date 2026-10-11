package com.fich.sarh.auth.application.ports.entrypoint.api;

import com.fich.sarh.auth.infrastructure.adapter.input.rest.model.request.LoginRequest;
import com.fich.sarh.auth.infrastructure.adapter.input.rest.model.response.AuthResponse;

public interface AuthApiPort {
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(String refreshToken);

}
