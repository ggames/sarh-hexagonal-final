package com.fich.sarh.auth.application.ports.output.persistence;

import com.fich.sarh.auth.domain.model.UserDTO;

import java.util.List;
import java.util.Optional;

public interface UserRetrieveSpiPort {

    List<UserDTO> findAllUsers();

    List<UserDTO> findUserByUsernameAndEmail(String query);

    Optional<UserDTO> findByUsername(String username);

    Optional<UserDTO> findUserById(Long userId);

    byte[] getPhotoByUsername(String username);

    boolean existsUsername(String username);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByUsernameIgnoreCaseAndIdNot(String username, Long id);
}
