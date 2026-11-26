package com.fich.sarh.auth.application.ports.entrypoint.api;

import com.fich.sarh.auth.domain.model.UserDTO;

public interface UserSaveApiPort {

    UserDTO saveUsername(UserDTO user);

    void deleteUser(Long id);
}
