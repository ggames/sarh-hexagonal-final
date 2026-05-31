package com.fich.sarh.organizationalunit.domain.ports.inbound;

import com.fich.sarh.organizationalunit.domain.model.OrganizationalDTO;
import com.fich.sarh.organizationalunit.domain.model.OrganizationalUnit;
import com.fich.sarh.organizationalunit.infrastructure.adapters.inbound.rest.model.request.OrganizationalUnitRequest;

import java.util.List;
import java.util.Optional;

public interface OrganizationalunitApiPort {
    List<OrganizationalUnit> findAllOrganizationalUnits();
    Optional<OrganizationalUnit> findOrganizationalunitById(Long id);
    List<OrganizationalDTO> findAllOrganizationDto();
    List<OrganizationalUnit> findByNameUnit(String unit);
    OrganizationalUnit saveOrganizationUnit(OrganizationalUnitRequest organizational);
    OrganizationalUnit updateOrganizationUnit(Long id, OrganizationalUnitRequest organizational);
}
