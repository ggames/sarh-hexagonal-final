package com.fich.sarh.point.infrastructure.adapter.input.rest.controller;

import com.fich.sarh.common.WebAdapter;
import com.fich.sarh.point.domain.model.Point;
import com.fich.sarh.point.domain.model.ParityByPosition;
import com.fich.sarh.point.domain.model.ParityPercentage;
import com.fich.sarh.point.domain.ports.inbound.PointApiPort;
import com.fich.sarh.point.infrastructure.adapter.input.rest.model.request.PointRequest;
import com.fich.sarh.point.infrastructure.adapter.input.rest.model.response.PointResponse;
import com.fich.sarh.point.infrastructure.adapter.output.persistence.mapper.PointRestMapper;
import jakarta.validation.Valid;
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
    public ResponseEntity<PointResponse> save(@Valid @RequestBody PointRequest request){
        Point domainModel = restMapper.PointRequestToPoint(request);
        Point saved = pointApiPort.savePoint(domainModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                restMapper.PointToPointResponse(saved)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping("parity")
    public void updatePoint(@Valid @RequestBody ParityPercentage parity) {
        pointApiPort.applyGlobalParity(parity.getPercentage());
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping("parity/{id}")
    public void updatePointByType(@PathVariable Long id, @Valid @RequestBody ParityByPosition parity){
        pointApiPort.applyParityByPositionType(id, parity.getAmountPositionNew());
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("{id}")
    public ResponseEntity<PointResponse> fetchPointById(@PathVariable Long id){
        return pointApiPort.findPointById(id)
                .map(restMapper::PointToPointResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping(value = {"{id}", "delete/{id}"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deletePoint(@PathVariable Long id) {
        pointApiPort.deletePoint(id);
        return ResponseEntity.ok().body("Punto/Categoría eliminado con éxito");
    }

}
