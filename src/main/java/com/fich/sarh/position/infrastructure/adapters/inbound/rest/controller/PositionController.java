package com.fich.sarh.position.infrastructure.adapters.inbound.rest.controller;

import com.fich.sarh.common.WebAdapter;
import com.fich.sarh.position.domain.model.Position;
import com.fich.sarh.position.domain.model.PositionCommand;
import com.fich.sarh.position.domain.model.PositionDto;
import com.fich.sarh.position.domain.ports.inbound.PositionApiPort;
import com.fich.sarh.position.domain.ports.outbound.PositionSpiPort;
import com.fich.sarh.position.infrastructure.adapters.inbound.rest.model.response.PositionResponse;
import com.fich.sarh.position.infrastructure.adapters.output.persistence.mapper.PositionRestMapper;
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
    @PreAuthorize("hasRole('USER')")
    public List<PositionDto> findOriginPosition(@PathVariable Long id_generatePosition) {
       return  positionApiPort.findOriginPositions(id_generatePosition);
      //   return retrieveService.getOriginPositions(id_generatePosition);
    }

    @GetMapping("available")
    @PreAuthorize("hasRole('USER')")
    public List<PositionDto> findAvailablePositions(){
        return  positionApiPort.findFreePositions(); // getFreePositions();
    }

    @GetMapping("allposition")
    @PreAuthorize("hasRole('USER')")
    public List<PositionResponse> findAll(){
        log.info("CANTIDAD DE CARGOS " + positionApiPort.findAllPosition().size());
        return PositionRestMapper.INSTANCE.toPositionResponseList(positionApiPort.findAllPosition());
    }

    @GetMapping("vacant")
    @PreAuthorize("hasRole('USER')")
    public List<PositionDto> findVacantPositions() {
        return positionApiPort.findVacantPositions();
    }
    @GetMapping("all")
    @PreAuthorize("hasRole('USER')")
    public List<PositionDto> findAllPosition() {
        return positionApiPort.findAllPositions();
    }

    @GetMapping("{id}")
    @PreAuthorize("hasRole('USER')")
    public Position getPositionById(@PathVariable Long id){
        Optional<Position> positionFound = positionApiPort.findPositionById(id);
        log.info("CARGOS ENCONTRADO ????" + positionFound.get());
        if(!positionFound.isPresent()) {
            return null;
        }
        return positionFound.get();
    }

    @PostMapping("create")
    @PreAuthorize("hasRole('USER')")
    public Position save(@RequestBody PositionCommand command){
        //infoLogger.info("SOLICITUD de CARGO " + command.getOriginPositionIds());
        return  positionApiPort.addPosition(command);  //saveService.savePosition(command);
    }

    @PutMapping("update/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> update(@PathVariable Long id,@RequestBody PositionCommand request){
        log.info("VALOR ID CARGO " + request);
        return  new ResponseEntity<>(positionApiPort.updateFullPosition(id, request), HttpStatus.OK);
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
