package com.fich.sarh.organizationalsubunit.infrastructure.adapter.inbound.rest.controller;

import com.fich.sarh.common.WebAdapter;
import com.fich.sarh.organizationalsubunit.domain.model.OrganizationalSubUnit;
import com.fich.sarh.organizationalsubunit.domain.model.OrganizationalSubUnitDTO;
import com.fich.sarh.organizationalsubunit.domain.ports.inbound.OrganizationalSubunitApiPort;
import com.fich.sarh.organizationalsubunit.infrastructure.adapter.inbound.rest.mapper.OrganizationalSubUnitRestMapper;
import com.fich.sarh.organizationalsubunit.infrastructure.adapter.inbound.rest.model.request.OrganizationalSubunitRequest;
import com.fich.sarh.organizationalsubunit.infrastructure.adapter.inbound.rest.model.response.OrganizationalSubUnitResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@WebAdapter
@RestController
@RequestMapping("/suborganizational")
@RequiredArgsConstructor
public class OrganizationalSubUnitController {

    private final OrganizationalSubunitApiPort subunitApiPort;

    private final OrganizationalSubUnitRestMapper restMapper;

    //private final OrganizationalSubUnitMapper suborganizationalMapper;

    Logger logger = LoggerFactory.getLogger(OrganizationalSubUnitController.class);


@GetMapping("{id}")
@PreAuthorize("hasRole('USER')")
 public OrganizationalSubUnit findOrganizationalSubUnitById(@PathVariable Long id){
        Optional<OrganizationalSubUnit> organizationalSubunit = subunitApiPort.findOrganizationalSubunitById(id);

        if(!organizationalSubunit.isPresent()) return null;

        return organizationalSubunit.get();
 }

    @PreAuthorize("hasAnyRole('USER', 'INVITED')")
    @GetMapping("all")
    public List<OrganizationalSubUnit> findAll(){


        return  subunitApiPort.findAllOrganizationalSubUnits();

    }

    @PostMapping("create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> save(@RequestBody @Valid OrganizationalSubunitRequest request){
        OrganizationalSubUnit subUnit = subunitApiPort.saveOrganizationSubunit(request);

        return  ResponseEntity.status(HttpStatus.CREATED).body(
                        subUnit);
    }

    @PutMapping("update/{id}")
    @PreAuthorize("hasRole('USER')")
    public OrganizationalSubUnitResponse update(@PathVariable Long id, @RequestBody OrganizationalSubUnit request){

        return restMapper.toOrganizationalSubUnit(subunitApiPort.updateOrganizationSubunit(id,request));
    }

    @PreAuthorize("hasAnyRole('USER', 'INVITED')")
    @GetMapping("dto/all")
    public List<OrganizationalSubUnitDTO> findAllDto(){

        return subunitApiPort.findAllOrganizationalSubUnitDTOs();
    }
}
