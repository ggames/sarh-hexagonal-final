package com.fich.sarh.auth.application.ports.output.persistence;

import com.fich.sarh.auth.domain.model.UserDTO;

public interface UserSaveSpiPort {

    UserDTO saveUsername(UserDTO user);

    void deleteUser(Long id);
}
