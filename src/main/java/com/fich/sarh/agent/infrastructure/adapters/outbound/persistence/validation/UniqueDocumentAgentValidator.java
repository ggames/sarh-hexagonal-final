package com.fich.sarh.agent.infrastructure.adapters.outbound.persistence.validation;


import com.fich.sarh.agent.domain.ports.inbound.AgentApiPort;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UniqueDocumentAgentValidator implements ConstraintValidator<UniqueDocumentAgent, String> {

    private final AgentApiPort agentApiPort;

    @Override
    public boolean isValid(String document, ConstraintValidatorContext context) {

        if(document == null || document.isBlank()){
            return true;
        }

        return !agentApiPort.existByDocumentAgent(document);

    }
}
