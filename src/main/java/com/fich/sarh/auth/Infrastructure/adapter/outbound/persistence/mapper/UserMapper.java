package com.fich.sarh.auth.Infrastructure.adapter.outbound.persistence.mapper;

import com.fich.sarh.auth.Domain.model.UserDTO;
import com.fich.sarh.auth.Infrastructure.adapter.outbound.persistence.entities.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mappings({
            @Mapping(target = "roles", source = "roles")
    })
    UserEntity toUserEntity(UserDTO user);

    UserDTO toUserDTO(UserEntity entity);

    List<UserDTO> toUserEntityList(List<UserEntity> userList);

}
