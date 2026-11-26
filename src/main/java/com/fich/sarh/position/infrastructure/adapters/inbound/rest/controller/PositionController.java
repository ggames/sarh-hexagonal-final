package com.fich.sarh.position.infrastructure.adapters.inbound.rest.controller;

import com.fich.sarh.common.WebAdapter;
import com.fich.sarh.position.domain.model.Position;
import com.fich.sarh.position.domain.model.PositionCommand;
import com.fich.sarh.position.domain.model.PositionDto;
import com.fich.sarh.position.domain.ports.inbound.PositionApiPort;
import com.fich.sarh.position.infrastructure.adapters.inbound.rest.model.response.PositionResponse;
import com.fich.sarh.position.infrastructure.adapters.output.persistence.mapper.PositionRestMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@WebAdapter
@RestController
@RequestMapping("/position")
@RequiredArgsConstructor
@Log4j2
public class PositionController {

    private final PositionApiPort positionApiPort;
   // private final PositionRetrieveServicePort retrieveService;
   // private final PositionSaveServicePort saveService;
   // private final PositionUpdateServicePort updateService;



    @GetMapping("origin/{id_generatePosition}")
    @PreAuthorize("hasAnyRole('USER','ONLY_CONSULT')")
    public List<PositionDto> findOriginPosition(@PathVariable Long id_generatePosition) {
       return  positionApiPort.findOriginPositions(id_generatePosition);
      //   return retrieveService.getOriginPositions(id_generatePosition);
    }

    @GetMapping("available")
    @PreAuthorize("hasAnyRole('USER','ONLY_CONSULT')")
    public List<PositionDto> findAvailablePositions(){
        return  positionApiPort.findFreePositions(); // getFreePositions();
    }

    @GetMapping("allposition")
    @PreAuthorize("hasAnyRole('USER','ONLY_CONSULT')")
    public List<PositionResponse> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "500") int size){
        var positions = positionApiPort.findAllPosition(page, size);
        log.debug("CANTIDAD DE CARGOS {}", positions.size());
        return PositionRestMapper.INSTANCE.toPositionResponseList(positions);
    }

    @GetMapping("vacant")
    @PreAuthorize("hasAnyRole('USER','ONLY_CONSULT')")
    public List<PositionDto> findVacantPositions() {
        return positionApiPort.findVacantPositions();
    }
    @GetMapping("all")
    @PreAuthorize("hasAnyRole('USER','ONLY_CONSULT')")
    public List<PositionDto> findAllPosition() {
        return positionApiPort.findAllPositions();
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAnyRole('USER','ONLY_CONSULT')")
    public ResponseEntity<Position> getPositionById(@PathVariable Long id) {
        Optional<Position> positionFound = positionApiPort.findPositionById(id);
        if (!positionFound.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        log.info("CARGO ENCONTRADO: " + positionFound.get());
        return ResponseEntity.ok(positionFound.get());
    }

    @PostMapping("create")
    @PreAuthorize("hasRole('USER')")
    public Position save(@Valid @RequestBody PositionCommand command){
        //infoLogger.info("SOLICITUD de CARGO " + command.getOriginPositionIds());
        return  positionApiPort.addPosition(command);  //saveService.savePosition(command);
    }

    @PutMapping("update/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody PositionCommand request){
        log.info("VALOR ID CARGO " + request);
        return  new ResponseEntity<>(positionApiPort.updateFullPosition(id, request), HttpStatus.OK);
    }

    @DeleteMapping(value = {"{id}", "delete/{id}"})
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deletePosition(@PathVariable Long id) {
        positionApiPort.deletePosition(id);
        return ResponseEntity.ok().body("Cargo eliminado con éxito");
    }

   /* @PostMapping("create")
    public ResponseEntity<PositionResponse> save(@RequestBody PositionRequest request){

        return ResponseEntity.status(HttpStatus.CREATED).body(
                PositionRestMapper.INSTANCE.toPositionResponse(
                        saveService.savePosition(PositionRestMapper.INSTANCE.toPosition(request))
                )
        );
    }*/
}
