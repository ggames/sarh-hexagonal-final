package com.fich.sarh.auth.application.ports.entrypoint.api;

import com.fich.sarh.auth.domain.model.UserDTO;

public interface UserUpdateApiPort {

    UserDTO updateUser(Long userId, UserDTO dto);
}
