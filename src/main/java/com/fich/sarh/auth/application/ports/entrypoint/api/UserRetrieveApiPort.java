package com.fich.sarh.auth.application.ports.entrypoint.api;

import com.fich.sarh.auth.domain.model.UserDTO;

import java.util.List;
import java.util.Optional;

public interface UserRetrieveApiPort {

    List<UserDTO> findAllUsers();
    List<UserDTO> findUserByUsernameAndEmail(String query);
    Optional<UserDTO> findByUsername(String username);
    Optional<UserDTO> findUserById(Long userId);
    byte[] getPhotoByUsername(String username);
}
