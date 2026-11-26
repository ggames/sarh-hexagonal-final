package com.fich.sarh.agent;

import com.fich.sarh.agent.domain.ports.inbound.AgentApiPort;
import com.fich.sarh.agent.infrastructure.adapters.outbound.persistence.validation.UniqueDocumentAgentValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Regla de negocio: unicidad del documento del agente
 * (constraint {@code @UniqueDocumentAgent}).
 */
@DisplayName("D. Otras reglas — unicidad del documento del agente")
class AgentDocumentUniquenessRulesTest {

    private final ConstraintValidatorContext contexto = mock(ConstraintValidatorContext.class);

    @Test
    @DisplayName("43. Un documento ya registrado invalida la restricción")
    void documentoYaRegistradoMarcaLaRestriccionComoInvalida() {
        AgentApiPort port = mock(AgentApiPort.class);
        when(port.existByDocumentAgent("12345678")).thenReturn(true);
        UniqueDocumentAgentValidator validator = new UniqueDocumentAgentValidator(port);

        boolean valido = validator.isValid("12345678", contexto);

        assertThat(valido).isFalse();
    }

    @Test
    @DisplayName("44. Un documento libre valida la restricción")
    void documentoLibreEsValido() {
        AgentApiPort port = mock(AgentApiPort.class);
        when(port.existByDocumentAgent("12345678")).thenReturn(false);
        UniqueDocumentAgentValidator validator = new UniqueDocumentAgentValidator(port);

        boolean valido = validator.isValid("12345678", contexto);

        assertThat(valido).isTrue();
    }

    @Test
    @DisplayName("45. Un documento nulo o en blanco no consulta el puerto (la obligatoriedad la cubre @NotBlank)")
    void documentoNVacioNoConsultaElPuerto() {
        AgentApiPort port = mock(AgentApiPort.class);
        UniqueDocumentAgentValidator validator = new UniqueDocumentAgentValidator(port);

        assertThat(validator.isValid(null, contexto)).isTrue();
        assertThat(validator.isValid("   ", contexto)).isTrue();
        verifyNoInteractions(port);
    }
}
