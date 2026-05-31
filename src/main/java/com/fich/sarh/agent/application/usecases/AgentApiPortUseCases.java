package com.fich.sarh.agent.application.usecases;

import com.fich.sarh.agent.domain.model.Agent;
import com.fich.sarh.agent.domain.ports.inbound.AgentApiPort;
import com.fich.sarh.agent.domain.ports.outbound.AgentSpiPort;
import com.fich.sarh.agent.infrastructure.adapters.outbound.persistence.mapper.AgentRestMapper;
import com.fich.sarh.common.UseCase;
import com.fich.sarh.common.exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

@UseCase
@RequiredArgsConstructor
public class AgentApiPortUseCases implements AgentApiPort {

    private final AgentSpiPort agentSpiPort;
    private final AgentRestMapper restMapper;

    @Override
    public Page<Agent> findAllAgent(int page, int size) {

        return  agentSpiPort.findAllAgent(page, size);

    }

    @Override
    public Agent findAgentById(Long id) {

        return agentSpiPort.findAgentById(id).orElseThrow(()-> new ResourceNotFoundException("Agente"));
    }

    @Override
    public Agent findByDocument(String document) {

        return agentSpiPort.findByDocument(document);
    }

    @Override
    public List<Agent> findByLastname(String lastname) {

        return agentSpiPort.findByLastname(lastname);
    }

    @Override
    @Transactional
    public Agent addAgent(Agent command) {

        return agentSpiPort.saveAgent(command);
    }

    @Override
    @Transactional
    public Agent updateAgent(Long id, Agent command) {


         Agent savedAgent = agentSpiPort.findAgentById(id)
                   .orElseThrow(() -> new ResourceNotFoundException("El agente indicado no existe"));

                    savedAgent.setFirstname(command.getFirstname());
                    savedAgent.setLastname(command.getLastname());
                    savedAgent.setDocumenttype(command.getDocumenttype());
                    savedAgent.setDocument(command.getDocument());
                    savedAgent.setBirthdate(command.getBirthdate());
                    savedAgent.setAddress(command.getAddress());
                    savedAgent.setFile(command.getFile());
                    savedAgent.setLeavingdate(command.getLeavingdate());
                    savedAgent.setDeceased(command.isDeceased());
                    savedAgent.setPhone(command.getPhone());
                    savedAgent.setEmail(command.getEmail());

                   return   agentSpiPort.saveAgent(savedAgent);

    }

    @Override
    public boolean existByDocumentAgent(String document) {
        return agentSpiPort.existByDocumentAgent(document);
    }
}
