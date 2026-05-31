package com.fich.sarh.positiontype.infrastructure.adapter.inbound.rest.controller;

import com.fich.sarh.common.WebAdapter;
import com.fich.sarh.positiontype.domain.model.PositionType;
import com.fich.sarh.positiontype.domain.ports.inbound.PositionTypeApiPort;
import com.fich.sarh.positiontype.infrastructure.adapter.inbound.rest.mapper.PositionTypeRestMapper;
import com.fich.sarh.positiontype.infrastructure.adapter.inbound.rest.model.request.PositionTypeRequest;
import com.fich.sarh.positiontype.infrastructure.adapter.inbound.rest.model.response.PositionTypeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@WebAdapter
@RestController
@RequiredArgsConstructor
@RequestMapping("/positiontype")
public class PositionTypeController {


    private final PositionTypeApiPort positionTypeApiPort;

    private final PositionTypeRestMapper restMapper;


    @GetMapping("all")
    public List<PositionTypeResponse> findAll(){
        return    positionTypeApiPort.findAllPositionType().stream().map(
                restMapper::toPositionTypeResponse
        ).toList();
    }

    @PostMapping("create")
    public ResponseEntity<PositionTypeResponse> save(@RequestBody PositionTypeRequest request){

        return ResponseEntity.status(HttpStatus.CREATED).body(
                restMapper.toPositionTypeResponse(
                        positionTypeApiPort.savePositionType(request))
        );
    }

    @PutMapping("update/{id}")
    public PositionTypeResponse update(@PathVariable Long id, @RequestBody PositionTypeRequest request ){
        return restMapper.toPositionTypeResponse(
                positionTypeApiPort.updatePositionType(id,
                        request)
        );
    }

}
