package com.fich.sarh.organizationalsubunit.infrastructure.adapter.outbound.persistence.mapper;

import com.fich.sarh.organizationalsubunit.domain.model.OrganizationalSubUnit;
import com.fich.sarh.organizationalsubunit.infrastructure.adapter.outbound.persistence.entity.OrganizationalSubUnitEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OrganizationalSubUnitMapper {

    OrganizationalSubUnitMapper INSTANCE = Mappers.getMapper(OrganizationalSubUnitMapper.class);
    OrganizationalSubUnit toOrganizationalSubUnit(OrganizationalSubUnitEntity entity);
    OrganizationalSubUnitEntity toOrganizationalSubUnitEntity(OrganizationalSubUnit suborganizational);

}
