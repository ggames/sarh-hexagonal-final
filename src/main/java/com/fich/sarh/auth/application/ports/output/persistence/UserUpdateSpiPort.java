package com.fich.sarh.auth.application.ports.output.persistence;

import com.fich.sarh.auth.domain.model.UserDTO;

public interface UserUpdateSpiPort {

    UserDTO updateUser(Long userId, UserDTO dto);
}
