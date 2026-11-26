package com.fich.sarh.planthistory.infrastructure.adapters.inbound.rest.controller;

import com.fich.sarh.common.WebAdapter;
import com.fich.sarh.planthistory.domain.model.PlantHistory;
import com.fich.sarh.planthistory.domain.ports.inbound.PlantHistoryApiPort;
import com.fich.sarh.planthistory.infrastructure.adapters.inbound.rest.mapper.PlantHistoryRestMapper;
import com.fich.sarh.planthistory.infrastructure.adapters.inbound.rest.model.response.PlantHistoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@WebAdapter
@RestController @RequiredArgsConstructor
@RequestMapping("/planthistory")
public class PlantHistoryController {

    private final PlantHistoryApiPort plantHistoryApiPort;
    private final PlantHistoryRestMapper mapperRest;



    @GetMapping("{id}")
    @PreAuthorize("hasAnyRole('USER','ONLY_CONSULT')")
    public List<PlantHistoryResponse> findPlantHistoryByPlantId(@PathVariable Long id){
        return PlantHistoryRestMapper.INSTANCE.toPlantHistoryResponseList(plantHistoryApiPort.findPlantHistoryByPlantId(id));
    }

    @GetMapping("history/{id}")
    @PreAuthorize("hasAnyRole('USER','ONLY_CONSULT')")
    public PlantHistory findPlantByIdActive(@PathVariable  Long id){
        return plantHistoryApiPort.findPlantByIdActive(id).orElse(null);
    }

    @GetMapping("top/{id}")
    @PreAuthorize("hasAnyRole('USER','ONLY_CONSULT')")
    public PlantHistory findTopByPlant(@PathVariable Long id){
        return plantHistoryApiPort.findTopByPlantIdOrderHistoryIdDesc(id);
    }

    @DeleteMapping(value = {"{id}", "delete/{id}"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        plantHistoryApiPort.deletePlantHistory(id);
        return ResponseEntity.ok().build();
    }

}
