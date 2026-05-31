package com.fich.sarh.organizationalunit.domain.ports.outbound;

import com.fich.sarh.organizationalunit.domain.model.OrganizationalDTO;
import com.fich.sarh.organizationalunit.domain.model.OrganizationalUnit;

import java.util.List;
import java.util.Optional;

public interface OrganizationalunitSpiPort {
    List<OrganizationalUnit> findAllOrganizationalUnits();
    Optional<OrganizationalUnit> findOrganizationalunitById(Long id);
    List<OrganizationalDTO> findAllOrganizationDto();
    List<OrganizationalUnit>findByNameUnit(String unit);
    OrganizationalUnit saveOrganizationUnit(OrganizationalUnit organizational);
}
