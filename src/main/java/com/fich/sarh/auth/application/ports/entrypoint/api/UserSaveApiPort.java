package com.fich.sarh.auth.application.ports.entrypoint.api;

import com.fich.sarh.auth.infrastructure.adapter.output.persistence.entities.UserEntity;

public interface UserSaveApiPort {

    UserEntity saveUsername(UserEntity user);


}
