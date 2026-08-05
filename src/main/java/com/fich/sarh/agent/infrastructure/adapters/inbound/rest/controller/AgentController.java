package com.fich.sarh.agent.infrastructure.adapters.inbound.rest.controller;


import com.fich.sarh.agent.domain.model.Agent;
import com.fich.sarh.agent.domain.ports.inbound.AgentApiPort;
import com.fich.sarh.agent.infrastructure.adapters.inbound.rest.model.request.AgentRequest;
import com.fich.sarh.agent.infrastructure.adapters.inbound.rest.model.response.AgentResponse;
import com.fich.sarh.agent.infrastructure.adapters.outbound.persistence.mapper.AgentRestMapper;
import com.fich.sarh.common.WebAdapter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@WebAdapter
@RestController
@RequestMapping("/agent")
@RequiredArgsConstructor
public class AgentController {


    private final AgentApiPort agentApiPort;
    private final AgentRestMapper restMapper;

    Logger logger = LoggerFactory.getLogger(AgentApiPort.class);


    //  @CrossOrigin(origins = "http://localhost:5173")
    @PreAuthorize("hasAnyRole('USER','ONLY_CONSULT')")
    @GetMapping("all")
    public Page<AgentResponse> findAll(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "5") int size) {

        return AgentRestMapper.INSTANCE.toAgentResponsePage(agentApiPort.findAllAgent(page, size));

    }

    @GetMapping("document/{document}")
    @PreAuthorize("hasRole('USER')")
    public AgentResponse findAgentByDocument(@PathVariable String document) {
        return AgentRestMapper.INSTANCE.AgentToAgentResponse(agentApiPort.findByDocument(document));
    }

    @GetMapping("search/{query}")
    @PreAuthorize("hasRole('USER')")
    public List<AgentResponse> findAgentByLastnameOrFirstname(@PathVariable String query) {
        return AgentRestMapper.INSTANCE.AgentListToAgentResponseList(agentApiPort.findByLastname(query));
    }

    @GetMapping("{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> findByIdAgent(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(agentApiPort.findAgentById(id));
    }


    @PostMapping("create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> save(@RequestBody @Valid Agent request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        agentApiPort.addAgent(request)
                );

    }

    @PutMapping("update/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody AgentRequest request) {
        logger.info("AGENTE ACTUALIZADO " + request + " ID " + id);
        return ResponseEntity.status(HttpStatus.OK).body(agentApiPort.updateAgent(id,
                restMapper.AgentRequestToAgent(request)));
    }
}
