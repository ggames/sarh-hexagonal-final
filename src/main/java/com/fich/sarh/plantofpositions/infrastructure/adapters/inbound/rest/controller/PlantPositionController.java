package com.fich.sarh.plantofpositions.infrastructure.adapters.inbound.rest.controller;

import com.fich.sarh.common.WebAdapter;
import com.fich.sarh.common.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
// import com.fich.sarh.plantofpositions.application.ports.entrypoint.api.*;
import com.fich.sarh.plantofpositions.domain.model.*;
import com.fich.sarh.plantofpositions.domain.ports.inbound.PlantPositionApiPort;
import com.fich.sarh.plantofpositions.infrastructure.adapters.inbound.rest.mapper.PlantOfPositionRestMapper;
import com.fich.sarh.plantofpositions.infrastructure.adapters.inbound.rest.model.request.PlantOfPositionRequest;
import com.fich.sarh.plantofpositions.infrastructure.adapters.inbound.rest.model.response.PlantOfPositionResponse;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

@WebAdapter
@RestController
@RequestMapping("/plant")
@RequiredArgsConstructor
public class PlantPositionController {

    private final PlantPositionApiPort plantApiPort;
    private final PlantOfPositionRestMapper restMapper;

    Logger logger = LoggerFactory.getLogger(this.getClass());
    // private final PlantOfPositionRetrieveApiPort retrievePlantService;
    // private final PlantOfPositionSaveApiPort savePlantService;
    // private final MovementRetrieveServicePort movementRetrieve;
    // private final PlantOfPositionUpdateApiPort updatePlantService;
    // private final PlantOfPositionSearchApiPort plantSearch;

    // private final ExcelPlantReportApiPort excelReport;




    @GetMapping("all")
    @PreAuthorize("hasAnyRole('USER','ONLY_CONSULT')")
    public List<PlantOfPositionDto> findAll() {

        return plantApiPort.findAllPlantPositions();

    }

    @GetMapping("{id}")
    @PreAuthorize("hasAnyRole('USER','ONLY_CONSULT')")
    public PlantOfPositionResponse findPlantPositionById(@PathVariable Long id) {
        var plantOfPositionFound = plantApiPort.findPlantPositionById(id)
                .orElseThrow(()-> new ResourceNotFoundException(""));

        return restMapper.toPlantOfPositionResponse(plantOfPositionFound) ;
    }

    @PostMapping("create")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<PlantOfPositionResponse> save(@Valid @RequestBody PlantOfPositionCommand request) {
        PlantOfPosition created = plantApiPort.addPlantOfPosition(request);
        return new ResponseEntity<>(restMapper.toPlantOfPositionResponse(created), HttpStatus.CREATED);
    }

    @GetMapping("search/")
    @PreAuthorize("hasAnyRole('USER','ONLY_CONSULT')")
    public ResponseEntity<?> search(PlantFilter filter) {

        // Specification<PlantOfPositionEntity> specification = PlantSpecifications.createSpecification(filter);
        return new ResponseEntity<>(plantApiPort.search(filter), HttpStatus.OK);
    }
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping(
            value = "/report/excel",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    )
    public ResponseEntity<StreamingResponseBody> generateExcelReport(@RequestBody PlantFilter filter) {
        logger.info("Generando reporte Excel con filtro: {}", filter);

        List<PlantProjectionDTO> plants = plantApiPort.search(filter);

        if (plants == null || plants.isEmpty()) {
            logger.warn("No se encontraron resultados para el filtro");
            return ResponseEntity.noContent().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"plantas.xlsx\"");
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        StreamingResponseBody body = out -> {
            try {
                plantApiPort.writeExcel(plants, out);
            } catch (IOException e) {
                logger.error("Error al escribir el reporte Excel", e);
                throw new UncheckedIOException(e);
            }
        };

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                ))
                .body(body);
    }
    @PutMapping("update/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<PlantOfPositionResponse> update(@PathVariable Long id, @Valid @RequestBody PlantOfPositionRequest request) {
        PlantOfPosition domainModel = restMapper.toPlantOfPosition(request);
        PlantOfPosition updated = plantApiPort.updatePlantOfPosition(id, domainModel);
        return new ResponseEntity<>(restMapper.toPlantOfPositionResponse(updated), HttpStatus.OK);
    }

    @DeleteMapping(value = {"{id}", "delete/{id}"})
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<?> deletePlantPosition(@PathVariable Long id) {
        plantApiPort.deletePlantPosition(id);
        return ResponseEntity.ok().body("Registro de planta eliminado con éxito");
    }
}

