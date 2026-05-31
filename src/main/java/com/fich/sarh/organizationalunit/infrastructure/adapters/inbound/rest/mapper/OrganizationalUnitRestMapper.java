package com.fich.sarh.organizationalunit.infrastructure.adapters.inbound.rest.mapper;

import com.fich.sarh.organizationalunit.domain.model.OrganizationalUnit;
import com.fich.sarh.organizationalunit.infrastructure.adapters.inbound.rest.model.request.OrganizationalUnitRequest;
import com.fich.sarh.organizationalunit.infrastructure.adapters.inbound.rest.model.response.OrganizationalUnitResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface OrganizationalUnitRestMapper {

      OrganizationalUnitRestMapper INSTANCE = Mappers.getMapper(OrganizationalUnitRestMapper.class);

      OrganizationalUnit toOrganizationalUnit(OrganizationalUnitRequest request);

      OrganizationalUnitResponse toOrganizationalUnitResponse(OrganizationalUnit organizational);

      List<OrganizationalUnitResponse> toOrganizationalUnitResponseList(List<OrganizationalUnit> organizationalUnitList);
}
