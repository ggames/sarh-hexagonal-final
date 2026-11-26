package com.fich.sarh.auth.application.ports.entrypoint.api;

import com.fich.sarh.auth.application.model.UserRequest;
import com.fich.sarh.auth.application.model.AuthResponse;

public interface UserManagerApiPort {
    AuthResponse createUser(UserRequest request);
}
