package com.fich.sarh.auth.application.ports.entrypoint.api;

import com.fich.sarh.auth.application.model.LoginRequest;
import com.fich.sarh.auth.application.model.AuthResponse;

public interface AuthApiPort {
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(String refreshToken);
    void logout(String refreshToken);

}
