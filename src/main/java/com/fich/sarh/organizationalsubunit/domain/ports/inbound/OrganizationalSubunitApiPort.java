package com.fich.sarh.organizationalsubunit.domain.ports.inbound;

import com.fich.sarh.organizationalsubunit.domain.model.OrganizationalSubUnit;
import com.fich.sarh.organizationalsubunit.domain.model.OrganizationalSubUnitDTO;

import java.util.List;
import java.util.Optional;

public interface OrganizationalSubunitApiPort {
    List<OrganizationalSubUnitDTO> findAllOrganizationalSubUnitDTOs();
    List<OrganizationalSubUnit> findAllOrganizationalSubUnits();
    Optional<OrganizationalSubUnit> findOrganizationalSubunitById(Long id);
    List<OrganizationalSubUnit> findByName(String unit);
    OrganizationalSubUnit saveOrganizationSubunit(OrganizationalSubUnit request);
    OrganizationalSubUnit updateOrganizationSubunit(Long id, OrganizationalSubUnit request);
    boolean existsByOrganizationalUnitId(Long organizationalUnitId);
    void deleteOrganizationalSubunit(Long id);

}
