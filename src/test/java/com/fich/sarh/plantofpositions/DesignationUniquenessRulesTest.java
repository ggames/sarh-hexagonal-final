package com.fich.sarh.plantofpositions;

import com.fich.sarh.agent.domain.model.Agent;
import com.fich.sarh.agent.domain.ports.outbound.AgentSpiPort;
import com.fich.sarh.common.CharacterPlant;
import com.fich.sarh.common.PlantStatus;
import com.fich.sarh.common.exceptions.BusinessRuleViolationException;
import com.fich.sarh.common.exceptions.ResourceNotFoundException;
import com.fich.sarh.movement.domain.model.Movement;
import com.fich.sarh.movement.domain.ports.inbound.MovementApiPort;
import com.fich.sarh.movement.domain.ports.outbound.MovementSpiPort;
import com.fich.sarh.organizationalsubunit.domain.model.OrganizationalSubUnit;
import com.fich.sarh.organizationalsubunit.domain.ports.outbound.OrganizationalSubunitSpiPort;
import com.fich.sarh.planthistory.domain.model.PlantHistory;
import com.fich.sarh.planthistory.domain.ports.inbound.PlantHistoryApiPort;
import com.fich.sarh.planthistory.domain.ports.outbound.PlantHistorySpiPort;
import com.fich.sarh.plantofpositions.application.factory.PlantPositionFactory;
import com.fich.sarh.plantofpositions.application.handlers.MovementHandler;
import com.fich.sarh.plantofpositions.application.handlers.PlantHistoryHandler;
import com.fich.sarh.plantofpositions.application.usecases.PlantPositionUseCases;
import com.fich.sarh.plantofpositions.domain.model.PlantOfPosition;
import com.fich.sarh.plantofpositions.domain.model.PlantOfPositionCommand;
import com.fich.sarh.plantofpositions.domain.ports.outbound.PlantPositionSpiPort;
import com.fich.sarh.plantofpositions.domain.service.PlantPositionService;
import com.fich.sarh.position.domain.model.Position;
import com.fich.sarh.position.domain.ports.outbound.PositionSpiPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Regla de negocio: unicidad de las designaciones.
 *
 * <p>Implementada en {@code PlantPositionService.validateActivePlant}: no puede
 * existir una designación activa del mismo agente en el mismo cargo. La
 * unicidad es por par (cargo, agente): un agente puede tener más de un cargo.</p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("C. Unicidad de las designaciones")
class DesignationUniquenessRulesTest {

    private static final Long AGENTE_ID = 1L;
    private static final Long CARGO_ID = 2L;
    private static final Long MATERIA_ID = 3L;
    private static final LocalDate FECHA_ALTA = LocalDate.of(2026, 3, 1);

    @Mock
    private PlantPositionSpiPort plantSpiPort;

    @Mock
    private PositionSpiPort positionSpiPort;

    @Mock
    private AgentSpiPort agentSpiPort;

    @Mock
    private OrganizationalSubunitSpiPort subunitSpiPort;

    @Mock
    private MovementSpiPort movementSpiPort;

    @Mock
    private PlantHistorySpiPort plantHistorySpiPort;

    @Mock
    private MovementApiPort movementApiPort;

    @Mock
    private PlantHistoryApiPort plantHistoryApiPort;

    private PlantPositionUseCases useCase;

    @BeforeEach
    void construirCasoDeUso() {
        useCase = new PlantPositionUseCases(
                plantSpiPort,
                positionSpiPort,
                agentSpiPort,
                subunitSpiPort,
                new PlantPositionService(plantSpiPort),
                new PlantPositionFactory(),
                new MovementHandler(movementSpiPort),
                new PlantHistoryHandler(plantHistorySpiPort),
                movementApiPort,
                plantHistoryApiPort);
    }

    private PlantOfPositionCommand comando() {
        PlantOfPositionCommand command = new PlantOfPositionCommand();
        command.setAgentId(AGENTE_ID);
        command.setPositionId(CARGO_ID);
        command.setCharacterplantID(CharacterPlant.ORDINARIO);
        command.setCurrentStatusID(PlantStatus.ACTIVO);
        command.setOrganizationalSubUnit(MATERIA_ID);
        command.setDateFrom(FECHA_ALTA);
        command.setReasonForMovement("Designación inicial");
        return command;
    }

    private void dadoFlujoFeliz() {
        when(plantSpiPort.existsByPositionAndAgent(CARGO_ID, AGENTE_ID)).thenReturn(false);
        when(positionSpiPort.findPositionById(CARGO_ID))
                .thenReturn(Optional.of(Position.builder().id(CARGO_ID).build()));
        when(agentSpiPort.findAgentById(AGENTE_ID))
                .thenReturn(Optional.of(Agent.builder().id(AGENTE_ID).build()));
        when(subunitSpiPort.findOrganizationalSubunitById(MATERIA_ID))
                .thenReturn(Optional.of(OrganizationalSubUnit.builder().id(MATERIA_ID).build()));
        when(plantSpiPort.savePlantOfPosition(any())).thenAnswer(invocation -> {
            PlantOfPosition plant = invocation.getArgument(0);
            plant.setId(50L);
            return plant;
        });
    }

    @Test
    @DisplayName("28. Designar un agente que ya tiene una designación activa en el mismo cargo viola la regla de unicidad")
    void designacionActivaDuplicadaLanzaBusinessRuleViolation() {
        when(plantSpiPort.existsByPositionAndAgent(CARGO_ID, AGENTE_ID)).thenReturn(true);

        assertThatThrownBy(() -> useCase.addPlantOfPosition(comando()))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessage("Ya hay un agente activo en el cargo en el sistema.");
        verify(plantSpiPort, never()).savePlantOfPosition(any());
    }

    @Test
    @DisplayName("29. Sin designación activa para el par cargo-agente, la planta se registra con su estado inicial")
    void sinDesignacionActivaRegistraLaPlanta() {
        dadoFlujoFeliz();

        PlantOfPosition registrada = useCase.addPlantOfPosition(comando());

        assertThat(registrada).isNotNull();
        assertThat(registrada.getId()).isEqualTo(50L);
        assertThat(registrada.getCurrentStatusID()).isEqualTo(PlantStatus.ACTIVO);
        verify(plantSpiPort).savePlantOfPosition(registrada);
    }

    @Test
    @DisplayName("30. Designar un cargo inexistente lanza ResourceNotFoundException")
    void cargoInexistenteLanzaResourceNotFound() {
        when(plantSpiPort.existsByPositionAndAgent(CARGO_ID, AGENTE_ID)).thenReturn(false);
        when(positionSpiPort.findPositionById(CARGO_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.addPlantOfPosition(comando()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No existe al cargo seleccionado no  encontrado");
        verify(plantSpiPort, never()).savePlantOfPosition(any());
    }

    @Test
    @DisplayName("31. Designar a un agente inexistente lanza ResourceNotFoundException")
    void agenteInexistenteLanzaResourceNotFound() {
        when(plantSpiPort.existsByPositionAndAgent(CARGO_ID, AGENTE_ID)).thenReturn(false);
        when(positionSpiPort.findPositionById(CARGO_ID))
                .thenReturn(Optional.of(Position.builder().id(CARGO_ID).build()));
        when(agentSpiPort.findAgentById(AGENTE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.addPlantOfPosition(comando()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No existe el agente seleccionado no  encontrado");
        verify(plantSpiPort, never()).savePlantOfPosition(any());
    }

    @Test
    @DisplayName("32. Designar una materia (subunidad) inexistente lanza ResourceNotFoundException")
    void materiaInexistenteLanzaResourceNotFound() {
        when(plantSpiPort.existsByPositionAndAgent(CARGO_ID, AGENTE_ID)).thenReturn(false);
        when(positionSpiPort.findPositionById(CARGO_ID))
                .thenReturn(Optional.of(Position.builder().id(CARGO_ID).build()));
        when(agentSpiPort.findAgentById(AGENTE_ID))
                .thenReturn(Optional.of(Agent.builder().id(AGENTE_ID).build()));
        when(subunitSpiPort.findOrganizationalSubunitById(MATERIA_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.addPlantOfPosition(comando()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No existe la materia seleccionada no  encontrado");
        verify(plantSpiPort, never()).savePlantOfPosition(any());
    }

    @Test
    @DisplayName("33. El alta de la designación registra el movimiento con la fecha y el motivo indicados")
    void elAltaRegistraElMovimientoConLaFechaYElMotivo() {
        dadoFlujoFeliz();

        useCase.addPlantOfPosition(comando());

        ArgumentCaptor<Movement> captor = ArgumentCaptor.forClass(Movement.class);
        verify(movementSpiPort).saveMovement(captor.capture());
        assertThat(captor.getValue().getMovementDate()).isEqualTo(FECHA_ALTA);
        assertThat(captor.getValue().getReasonForMovement()).isEqualTo("Designación inicial");
        assertThat(captor.getValue().getPositionId()).isEqualTo(CARGO_ID);
        assertThat(captor.getValue().getPlantId()).isEqualTo(50L);
    }

    @Test
    @DisplayName("34. El alta de la designación abre un historial con el estado inicial y sin fecha de fin")
    void elAltaRegistraElHistorialAbiertoConElEstadoInicial() {
        dadoFlujoFeliz();

        useCase.addPlantOfPosition(comando());

        ArgumentCaptor<PlantHistory> captor = ArgumentCaptor.forClass(PlantHistory.class);
        verify(plantHistorySpiPort).savePlantHistory(captor.capture());
        assertThat(captor.getValue().getDateFrom()).isEqualTo(FECHA_ALTA);
        assertThat(captor.getValue().getDateTo()).isNull();
        assertThat(captor.getValue().getPlantStatus()).isEqualTo(PlantStatus.ACTIVO);
        assertThat(captor.getValue().getPlantOfPosition().getId()).isEqualTo(50L);
    }

    @Test
    @DisplayName("35. Un agente con designación activa en OTRO cargo puede recibir una segunda designación (un agente puede tener más de un cargo)")
    void agenteConDesignacionActivaEnOtroCargoPuedeRecibirOtraDesignacion() {
        dadoFlujoFeliz();

        PlantOfPosition registrada = useCase.addPlantOfPosition(comando());

        assertThat(registrada).isNotNull();
        assertThat(registrada.getId()).isEqualTo(50L);
        verify(plantSpiPort).savePlantOfPosition(registrada);
    }
}
