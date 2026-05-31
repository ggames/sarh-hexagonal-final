package com.fich.sarh.point.infrastructure.adapter.input.rest.controller;

import com.fich.sarh.common.WebAdapter;
import com.fich.sarh.point.domain.model.ParityByPosition;
import com.fich.sarh.point.domain.model.ParityPercentage;
import com.fich.sarh.point.domain.ports.inbound.PointApiPort;
import com.fich.sarh.point.infrastructure.adapter.input.rest.model.request.PointRequest;
import com.fich.sarh.point.infrastructure.adapter.input.rest.model.response.PointResponse;
import com.fich.sarh.point.infrastructure.adapter.output.persistence.mapper.PointRestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@WebAdapter
@RestController @RequiredArgsConstructor
@RequestMapping("/point")
public class PointController {

    private final PointApiPort pointApiPort;

    private final PointRestMapper restMapper;


    @GetMapping("all")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public List<PointResponse> findAll(){
        return PointRestMapper.INSTANCE.toPointResponseList(pointApiPort.findAllPoints());
    }

    @PostMapping("create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PointResponse> save(@RequestBody PointRequest request){

        return ResponseEntity.status(HttpStatus.CREATED).body(
                pointApiPort.savePoint(request)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("parity")
    public void updatePoint(@RequestBody ParityPercentage parity) {
        pointApiPort.applyGlobalParity(parity.getPercentage());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("parity/{id}")
    public void updatePointByType(@PathVariable Long id, @RequestBody ParityByPosition parity){
        pointApiPort.applyParityByPositionType(id, parity.getAmountPositionNew());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("{id}")
    public PointResponse fetchPointById(@PathVariable Long id){

        return restMapper.PointToPointResponse(pointApiPort.findPointById(id).get() );
    }


}
