package com.fich.sarh.agent.infrastructure.adapters.outbound.persistence.adapter;

import com.fich.sarh.agent.domain.model.Agent;
import com.fich.sarh.agent.domain.ports.outbound.AgentSpiPort;
import com.fich.sarh.agent.infrastructure.adapters.outbound.persistence.mapper.AgentMapper;
import com.fich.sarh.agent.infrastructure.adapters.outbound.persistence.repository.AgentRepository;
import com.fich.sarh.common.WebAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@WebAdapter
@RequiredArgsConstructor
public class AgentPersistenceAdapter implements AgentSpiPort {

    private final AgentRepository agentRepository;
    private final AgentMapper agentMapper;

    @Override
    public Page<Agent> findAllAgent(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());

        return AgentMapper.INSTANCE.toAgentPage(agentRepository.findAll(pageable));
    }

    @Override
    public Optional<Agent> findAgentById(Long id) {
        return agentRepository.findById(id).map(agentMapper::toDto);
    }

    @Override
    public Agent findByDocument(String document) {
        return agentRepository.findAgentByDocument(document).map(agentMapper::toDto).get();
    }

    @Override
    public List<Agent> findByLastname(String lastname) {
        return  agentRepository.findAgentByLastname(lastname)
                .stream().map(agentMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public Agent saveAgent(Agent command) {
        return  agentMapper.toDto(agentRepository.save(agentMapper.toEntity(command)));
    }



    @Override
    public boolean existByDocumentAgent(String document) {
        return agentRepository.existsByDocument(document);
    }
}
