package com.fich.sarh.agent.infrastructure.adapters.outbound.persistence.adapter;

import com.fich.sarh.agent.domain.model.Agent;
import com.fich.sarh.agent.domain.ports.outbound.AgentSpiPort;
import com.fich.sarh.agent.infrastructure.adapters.outbound.persistence.entity.AgentEntity;
import com.fich.sarh.agent.infrastructure.adapters.outbound.persistence.mapper.AgentMapper;
import com.fich.sarh.agent.infrastructure.adapters.outbound.persistence.repository.AgentRepository;
import com.fich.sarh.common.PersistenceAdapter;
import com.fich.sarh.common.domain.PageResponse;
import com.fich.sarh.common.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@PersistenceAdapter
@RequiredArgsConstructor
public class AgentPersistenceAdapter implements AgentSpiPort {

    private final AgentRepository agentRepository;
    private final AgentMapper agentMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<Agent> findAllAgent(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<AgentEntity> entityPage = agentRepository.findAll(pageable);
        List<Agent> agents = agentMapper.toAgenList(entityPage.getContent());
        return PageResponse.of(agents, entityPage.getNumber(), entityPage.getSize(), entityPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Agent> findAgentById(Long id) {
        return agentRepository.findById(id).map(agentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Agent findByDocument(String document) {
        return agentRepository.findAgentByDocument(document)
                .map(agentMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Agente con documento " + document + " no encontrado"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Agent> findByLastname(String lastname) {
        return  agentRepository.findAgentByLastname(lastname)
                .stream().map(agentMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public Agent saveAgent(Agent command) {
        return  agentMapper.toDto(agentRepository.save(agentMapper.toEntity(command)));
    }



    @Override
    @Transactional(readOnly = true)
    public boolean existByDocumentAgent(String document) {
        return agentRepository.existsByDocument(document);
    }

    @Override
    public void deleteAgent(Long id) {
        agentRepository.deleteById(id);
    }
}
