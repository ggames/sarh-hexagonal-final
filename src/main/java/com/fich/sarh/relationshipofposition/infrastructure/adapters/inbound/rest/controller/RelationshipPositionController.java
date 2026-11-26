package com.fich.sarh.relationshipofposition.infrastructure.adapters.inbound.rest.controller;

import com.fich.sarh.common.WebAdapter;
import com.fich.sarh.relationshipofposition.domain.ports.inbound.RelationshipPositionApiPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@WebAdapter
@RestController
@RequiredArgsConstructor
@RequestMapping("/relationship-position")
public class RelationshipPositionController {

    private final RelationshipPositionApiPort relationshipPositionApiPort;

    @DeleteMapping(value = {"{id}", "delete/{id}"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        relationshipPositionApiPort.deleteRelationShipPosition(id);
        return ResponseEntity.ok().build();
    }
}
