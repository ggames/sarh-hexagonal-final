package com.fich.sarh.planthistory.infrastructure.adapters.inbound.rest.controller;

import com.fich.sarh.common.WebAdapter;
import com.fich.sarh.planthistory.domain.model.PlantHistory;
import com.fich.sarh.planthistory.domain.ports.outbound.PlantHistorySpiPort;
import com.fich.sarh.planthistory.infrastructure.adapters.inbound.rest.mapper.PlantHistoryRestMapper;
import com.fich.sarh.planthistory.infrastructure.adapters.inbound.rest.model.response.PlantHistoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@WebAdapter
@RestController @RequiredArgsConstructor
@RequestMapping("/planthistory")
public class PlantHistoryController {

    private final PlantHistorySpiPort plantHistorySpiPort;
    private final PlantHistoryRestMapper mapperRest;



    @GetMapping("{id}")
    @PreAuthorize("hasRole('USER')")
    public List<PlantHistoryResponse> findPlantHistoryByPlantId(@PathVariable Long id){
        return PlantHistoryRestMapper.INSTANCE.toPlantHistoryResponseList(plantHistorySpiPort.findPlantHistoryByPlantId(id));
    }

    @GetMapping("history/{id}")
    @PreAuthorize("hasRole('USER')")
    public PlantHistory findPlantByIdActive(@PathVariable  Long id){
        return plantHistorySpiPort.findPlantByIdActive(id).get();
    }

    @GetMapping("top/{id}")
    @PreAuthorize("hasRole('USER')")
    public PlantHistory findTopByPlant(@PathVariable Long id){
        PlantHistory plantHistory = plantHistorySpiPort.findTopByPlantIdOrderHistoryIdDesc(id);
        if(plantHistory == null) {
            return null;
        }
        return plantHistory;
    }

}
