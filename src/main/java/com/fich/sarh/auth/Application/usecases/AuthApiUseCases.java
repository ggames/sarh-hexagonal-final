package com.fich.sarh.auth.Application.usecases;

import com.fich.sarh.auth.Domain.ports.inbound.AuthApiPort;
import com.fich.sarh.auth.Domain.ports.outbound.AuthSpiPort;
import com.fich.sarh.auth.Infrastructure.adapter.inbound.rest.model.request.LoginRequest;
import com.fich.sarh.auth.Infrastructure.adapter.inbound.rest.model.response.AuthResponse;
import com.fich.sarh.common.UseCase;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class AuthApiUseCases implements AuthApiPort {


    private final AuthSpiPort authSpiPort;

    @Override
    public AuthResponse login(LoginRequest request) {
          return authSpiPort.login(request);
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        return  authSpiPort.refreshToken(refreshToken);
    }
}
