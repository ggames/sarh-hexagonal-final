package com.fich.sarh.agent.domain.ports.outbound;

import com.fich.sarh.agent.domain.model.Agent;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface AgentSpiPort {

    Page<Agent> findAllAgent(int page, int size);
    Optional<Agent> findAgentById(Long id);
    Agent findByDocument(String document);
    List<Agent> findByLastname(String lastname);

    Agent saveAgent(Agent command);


    boolean existByDocumentAgent(String document);
}
