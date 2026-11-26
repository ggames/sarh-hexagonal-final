package com.fich.sarh.auth.application.services;

import com.fich.sarh.auth.application.ports.entrypoint.api.UserSaveApiPort;
import com.fich.sarh.auth.application.ports.output.persistence.UserSaveSpiPort;
import com.fich.sarh.auth.domain.model.UserDTO;
import com.fich.sarh.common.UseCase;

@UseCase
public class UserSaveUseCase implements UserSaveApiPort {

    private final UserSaveSpiPort userSaveSpiPort;

    public UserSaveUseCase(UserSaveSpiPort userSaveSpiPort) {
        this.userSaveSpiPort = userSaveSpiPort;
    }

    @Override
    public UserDTO saveUsername(UserDTO user) {
        return userSaveSpiPort.saveUsername(user);
    }

    @Override
    public void deleteUser(Long id) {
        userSaveSpiPort.deleteUser(id);
    }
}
