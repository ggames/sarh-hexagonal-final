package com.fich.sarh.agent.domain.ports.inbound;

import com.fich.sarh.agent.domain.model.Agent;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface AgentApiPort {

    Page<Agent> findAllAgent(int page, int size);
    Agent findAgentById(Long id);
    Agent findByDocument(String document);
    List<Agent> findByLastname(String lastname);

    Agent addAgent(Agent command);
    Agent updateAgent(Long id, Agent command);

    boolean existByDocumentAgent(String document);
}
