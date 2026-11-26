package com.fich.sarh.agent.application.usecases;

import com.fich.sarh.agent.domain.model.Agent;
import com.fich.sarh.agent.domain.ports.inbound.AgentApiPort;
import com.fich.sarh.agent.domain.ports.outbound.AgentSpiPort;
import com.fich.sarh.common.UseCase;
import com.fich.sarh.common.domain.PageResponse;
import com.fich.sarh.common.exceptions.BusinessRuleViolationException;
import com.fich.sarh.common.exceptions.ResourceNotFoundException;
import com.fich.sarh.organizationalunit.domain.ports.inbound.OrganizationalunitApiPort;
import com.fich.sarh.plantofpositions.domain.ports.inbound.PlantPositionApiPort;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

@UseCase
public class AgentApiPortUseCases implements AgentApiPort {

    private final AgentSpiPort agentSpiPort;
    private final PlantPositionApiPort plantPositionApiPort;
    private final OrganizationalunitApiPort organizationalUnitApiPort;

    public AgentApiPortUseCases(
            AgentSpiPort agentSpiPort,
            @Lazy PlantPositionApiPort plantPositionApiPort,
            @Lazy OrganizationalunitApiPort organizationalUnitApiPort) {
        this.agentSpiPort = agentSpiPort;
        this.plantPositionApiPort = plantPositionApiPort;
        this.organizationalUnitApiPort = organizationalUnitApiPort;
    }

    @Override
    public PageResponse<Agent> findAllAgent(int page, int size) {

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
        if (command.getDocument() != null && agentSpiPort.existByDocumentAgent(command.getDocument())) {
            throw new BusinessRuleViolationException("Ya existe un agente con el documento " + command.getDocument());
        }
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

    @Override
    @Transactional
    public void deleteAgent(Long id) {
        agentSpiPort.findAgentById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agente no encontrado con ID: " + id));

        if (plantPositionApiPort.existsByAgentId(id)) {
            throw new BusinessRuleViolationException("No se puede eliminar el agente porque está asignado a un cargo en la planta de cargos.");
        }

        if (organizationalUnitApiPort.existsByDirectorOrViceDirectorId(id)) {
            throw new BusinessRuleViolationException("No se puede eliminar el agente porque figura como director o vicedirector de un departamento.");
        }

        agentSpiPort.deleteAgent(id);
    }
}
