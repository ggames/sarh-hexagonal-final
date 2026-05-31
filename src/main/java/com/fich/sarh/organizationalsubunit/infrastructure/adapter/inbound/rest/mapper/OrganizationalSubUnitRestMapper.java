package com.fich.sarh.organizationalsubunit.infrastructure.adapter.inbound.rest.mapper;

import com.fich.sarh.organizationalsubunit.domain.model.OrganizationalSubUnit;
import com.fich.sarh.organizationalsubunit.infrastructure.adapter.inbound.rest.model.request.OrganizationalSubunitRequest;
import com.fich.sarh.organizationalsubunit.infrastructure.adapter.inbound.rest.model.response.OrganizationalSubUnitResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrganizationalSubUnitRestMapper {

      OrganizationalSubUnitRestMapper INSTANCE = Mappers.getMapper(OrganizationalSubUnitRestMapper.class);

      OrganizationalSubUnit toOrganizationalSubUnit(OrganizationalSubunitRequest request);

      OrganizationalSubUnitResponse toOrganizationalSubUnit(OrganizationalSubUnit subunit);

      }
