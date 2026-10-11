package com.fich.sarh.auth.application.ports.output.persistence;

import com.fich.sarh.auth.domain.model.RoleDTO;
import com.fich.sarh.auth.infrastructure.adapter.output.persistence.entities.RoleEnum;

import java.util.List;

public interface RoleRetrieveSpiPort {

    RoleDTO fetchByRoleEnum(RoleEnum name);

    List<RoleDTO> fetchAllRole();

}
