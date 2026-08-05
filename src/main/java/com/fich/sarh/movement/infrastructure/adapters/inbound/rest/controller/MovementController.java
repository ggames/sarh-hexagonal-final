package com.fich.sarh.movement.infrastructure.adapters.inbound.rest.controller;

import com.fich.sarh.common.WebAdapter;
import com.fich.sarh.movement.domain.ports.inbound.MovementApiPort;
import com.fich.sarh.movement.infrastructure.adapters.inbound.rest.mapper.MovementRestMapper;
import com.fich.sarh.movement.infrastructure.adapters.inbound.rest.model.response.MovementResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@WebAdapter
@RestController
@RequiredArgsConstructor
@RequestMapping("/movement")
public class MovementController {

    private final MovementApiPort movementApiPort;


    @GetMapping("all")
    @PreAuthorize("hasAnyRole('USER','ONLY_CONSULT')")
    public List<MovementResponse> findAll() {
        return MovementRestMapper.INSTANCE.toMovementList(movementApiPort.findAllMovements());
    }
}
