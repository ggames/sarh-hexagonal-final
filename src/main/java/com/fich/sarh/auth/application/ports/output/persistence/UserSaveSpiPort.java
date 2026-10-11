package com.fich.sarh.auth.application.ports.output.persistence;

import com.fich.sarh.auth.infrastructure.adapter.output.persistence.entities.UserEntity;

public interface UserSaveSpiPort {

    UserEntity saveUsername(UserEntity user);
}
