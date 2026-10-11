package com.fich.sarh.auth.application.ports.entrypoint.api;

import com.fich.sarh.auth.infrastructure.adapter.input.rest.model.request.UserRequest;
import com.fich.sarh.auth.infrastructure.adapter.input.rest.model.response.AuthResponse;

public interface UserManagerApiPort {
    AuthResponse createUser(UserRequest request);
}
