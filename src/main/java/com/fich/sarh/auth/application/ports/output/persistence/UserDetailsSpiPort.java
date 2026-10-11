package com.fich.sarh.auth.application.ports.output.persistence;

import com.fich.sarh.auth.infrastructure.adapter.input.rest.model.request.LoginRequest;
import com.fich.sarh.auth.infrastructure.adapter.input.rest.model.response.AuthResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserDetailsSpiPort extends UserDetailsService {

   // AuthResponse createUser(UserRequest request);
    AuthResponse loginUser(LoginRequest authLoginRequest);
    AuthResponse refreshToken(String refreshToken);
}
