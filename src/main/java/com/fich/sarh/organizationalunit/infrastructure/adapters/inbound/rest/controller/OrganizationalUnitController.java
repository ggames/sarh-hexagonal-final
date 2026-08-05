package com.fich.sarh.organizationalunit.infrastructure.adapters.inbound.rest.controller;

import com.fich.sarh.common.WebAdapter;
import com.fich.sarh.common.exceptions.ResourceNotFoundException;
import com.fich.sarh.organizationalunit.domain.model.OrganizationalDTO;
import com.fich.sarh.organizationalunit.domain.model.OrganizationalUnit;
import com.fich.sarh.organizationalunit.domain.ports.inbound.OrganizationalunitApiPort;
import com.fich.sarh.organizationalunit.infrastructure.adapters.inbound.rest.mapper.OrganizationalUnitRestMapper;
import com.fich.sarh.organizationalunit.infrastructure.adapters.inbound.rest.model.request.OrganizationalUnitRequest;
import com.fich.sarh.organizationalunit.infrastructure.adapters.inbound.rest.model.response.OrganizationalUnitResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@WebAdapter
@RestController
@RequiredArgsConstructor
@RequestMapping("/organizational")
public class OrganizationalUnitController {

    private final OrganizationalunitApiPort organizationalApiPort;

    private final OrganizationalUnitRestMapper restMapper;


    @GetMapping("all")
    @PreAuthorize("hasAnyRole('USER','ONLY_CONSULT')")
    public List<OrganizationalUnitResponse> findAll() {
        List<OrganizationalUnitResponse> responses = organizationalApiPort.findAllOrganizationalUnits()
                .stream().map(restMapper::toOrganizationalUnitResponse).toList();
        return responses;
    }

    @GetMapping("dto/all")
    @PreAuthorize("hasAnyRole('USER', 'ONLY_CONSULT')")
    public List<OrganizationalDTO> findOrganizationalDTO() {
        return organizationalApiPort.findAllOrganizationDto();
    }

    @PostMapping("create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> save(@Valid @RequestBody OrganizationalUnitRequest request) {
        OrganizationalUnit organizationalUnit = organizationalApiPort
                .saveOrganizationUnit(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(organizationalUnit);
    }

    @GetMapping("{id}")
    @PreAuthorize("hasRole('USER')")
    public OrganizationalUnit findOrganizationalUnitById(@PathVariable Long id) {
        OrganizationalUnit organizational = organizationalApiPort
                .findOrganizationalunitById(id).orElseThrow(() -> new ResourceNotFoundException("Departamento no existente"));


        return organizational;
    }

    @PutMapping("update/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody OrganizationalUnitRequest request) {
        OrganizationalUnit organizationalUnit = organizationalApiPort
                .updateOrganizationUnit(id,
                        request);
        return ResponseEntity.status(HttpStatus.OK).body(organizationalUnit);
    }
}
