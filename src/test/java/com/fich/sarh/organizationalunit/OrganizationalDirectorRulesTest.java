package com.fich.sarh.organizationalunit;

import com.fich.sarh.agent.domain.model.Agent;
import com.fich.sarh.common.exceptions.BusinessRuleViolationException;
import com.fich.sarh.organizationalsubunit.domain.ports.inbound.OrganizationalSubunitApiPort;
import com.fich.sarh.organizationalunit.application.usecases.OrganizationalunitUseCases;
import com.fich.sarh.organizationalunit.domain.model.OrganizationalUnit;
import com.fich.sarh.organizationalunit.domain.ports.outbound.OrganizationalunitSpiPort;
import com.fich.sarh.position.domain.ports.inbound.PositionApiPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Regla de negocio: el director y el vicedirector de una unidad organizacional
 * no pueden ser la misma persona.
 *
 * <p>La validación vive en el modelo ({@code OrganizationalUnit.validateDirectors()},
 * invocado por su constructor de 4 argumentos); el caso de uso de update hoy no
 * la invoca (ver rojo #42).</p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("D. Otras reglas — directores de la unidad organizacional")
class OrganizationalDirectorRulesTest {

    private static final String MENSAJE =
            "El director y el vicedirector no pueden ser la misma persona en el sistema.";

    @Mock
    private OrganizationalunitSpiPort spiPort;

    @Mock
    private OrganizationalSubunitApiPort subunitApiPort;

    @Mock
    private PositionApiPort positionApiPort;

    private OrganizationalunitUseCases useCase;

    @BeforeEach
    void construirCasoDeUso() {
        useCase = new OrganizationalunitUseCases(spiPort, subunitApiPort, positionApiPort);
    }

    private static Agent agente(long id) {
        return Agent.builder().id(id).build();
    }

    @Test
    @DisplayName("37. El mismo agente como director y vicedirector viola la regla de negocio")
    void mismoAgenteComoDirectorYVicedirectorLanzaBusinessRuleViolation() {
        Agent director = agente(1L);

        assertThatThrownBy(() -> new OrganizationalUnit(1L, "Dirección", director, director))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessage(MENSAJE);
    }

    @Test
    @DisplayName("38. Sin vicedirector (nulo) la regla se respeta: la unidad es válida")
    void vicedirectorNuloEstaPermitido() {
        assertThatCode(() -> new OrganizationalUnit(1L, "Dirección", agente(1L), null))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("39. Sin director (nulo) la regla se respeta: la unidad es válida")
    void directorNuloEstaPermitido() {
        assertThatCode(() -> new OrganizationalUnit(1L, "Dirección", null, agente(2L)))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("40. Director y vicedirector con ids distintos es una unidad válida")
    void directoresDistintosEstanPermitidos() {
        assertThatCode(() -> new OrganizationalUnit(1L, "Dirección", agente(1L), agente(2L)))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("41. El alta persiste una unidad organizativa con directores válidos")
    void elAltaPersisteLaUnidadConDirectoresValidos() {
        OrganizationalUnit valid = new OrganizationalUnit(1L, "Dirección", agente(1L), agente(2L));
        when(spiPort.saveOrganizationUnit(any())).thenAnswer(invocation -> invocation.getArgument(0));

        OrganizationalUnit guardada = useCase.saveOrganizationUnit(valid);

        assertThat(guardada).isSameAs(valid);
        verify(spiPort).saveOrganizationUnit(valid);
    }

    @Test
    @DisplayName("[ROJO] 42. Actualizar una unidad con el mismo director y vicedirector debería rechazarse — el update no invoca validateDirectors()")
    void actualizarUnidadOrganizativaDeberiaValidarLosDirectores() {
        OrganizationalUnit existente = new OrganizationalUnit();
        existente.setId(1L);
        existente.setDirector(agente(1L));
        existente.setViceDirector(agente(2L));
        when(spiPort.findOrganizationalunitById(1L)).thenReturn(Optional.of(existente));
        org.mockito.Mockito.lenient().when(spiPort.saveOrganizationUnit(any())).thenAnswer(invocation -> invocation.getArgument(0));

        OrganizationalUnit comando = new OrganizationalUnit();
        comando.setNameUnit("Dirección");
        comando.setDirector(agente(1L));
        comando.setViceDirector(agente(1L));

        assertThatThrownBy(() -> useCase.updateOrganizationUnit(1L, comando))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessage(MENSAJE);
        verify(spiPort, never()).saveOrganizationUnit(any());
    }
}
