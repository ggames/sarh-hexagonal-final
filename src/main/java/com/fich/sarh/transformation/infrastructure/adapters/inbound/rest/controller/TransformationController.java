package com.fich.sarh.transformation.infrastructure.adapters.inbound.rest.controller;

import com.fich.sarh.common.WebAdapter;
import com.fich.sarh.transformation.domain.model.Transformation;
import com.fich.sarh.transformation.domain.ports.inbound.TransformationApiPort;
import com.fich.sarh.transformation.infrastructure.adapters.inbound.rest.mapper.TransformationRestMapper;
import com.fich.sarh.transformation.infrastructure.adapters.inbound.rest.model.response.TransformationResponse;
import com.fich.sarh.transformation.infrastructure.adapters.outbound.persistence.mapper.TransformationMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@WebAdapter
@RestController
@RequiredArgsConstructor
@RequestMapping("/transformation")
public class TransformationController {

    public static final String AES = "AES";
    Logger logger = LoggerFactory.getLogger(TransformationController.
            class);

    private final TransformationApiPort transformationApiPort;
    private final TransformationMapper mapper;
    private final TransformationRestMapper restMapper;

    @PostMapping("create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> save(@Valid @RequestBody Transformation request) {

        var exist = transformationApiPort.existByResolutionNumber(request.getResolutionNumber());

        logger.error("VALOR DE LA TRANSFORMACION  " + exist + "   " + request.getResolutionNumber());

        return ResponseEntity.status(HttpStatus.CREATED).body(
                restMapper.toTransformationResponse(
                        transformationApiPort.saveTransformation(
                                request
                        )
                )
        );
    }

    @GetMapping("all")
    @PreAuthorize("hasAnyRole('USER', 'ONLY_CONSULT')")
    public List<TransformationResponse> getAll() {

        return transformationApiPort
                .findAllTransformations().stream().map(
                        restMapper::toTransformationResponse
                ).toList();
    }

    @GetMapping("last")
    @PreAuthorize("hasRole('USER')")
    public TransformationResponse getTransformationLast() {
        //Cipher cipher = Cipher.getInstance(AES);
        return restMapper.toTransformationResponse(transformationApiPort.findFirstByOrderDesc());
    }
}
