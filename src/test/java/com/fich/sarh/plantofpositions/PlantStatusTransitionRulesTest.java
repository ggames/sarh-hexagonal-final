package com.fich.sarh.plantofpositions;

import com.fich.sarh.agent.domain.ports.outbound.AgentSpiPort;
import com.fich.sarh.common.CharacterPlant;
import com.fich.sarh.common.PlantStatus;
import com.fich.sarh.common.exceptions.ResourceNotFoundException;
import com.fich.sarh.movement.domain.ports.inbound.MovementApiPort;
import com.fich.sarh.movement.domain.ports.outbound.MovementSpiPort;
import com.fich.sarh.organizationalsubunit.domain.model.OrganizationalSubUnit;
import com.fich.sarh.organizationalsubunit.domain.ports.outbound.OrganizationalSubunitSpiPort;
import com.fich.sarh.planthistory.domain.ports.inbound.PlantHistoryApiPort;
import com.fich.sarh.planthistory.domain.ports.outbound.PlantHistorySpiPort;
import com.fich.sarh.plantofpositions.application.factory.PlantPositionFactory;
import com.fich.sarh.plantofpositions.application.handlers.MovementHandler;
import com.fich.sarh.plantofpositions.application.handlers.PlantHistoryHandler;
import com.fich.sarh.plantofpositions.application.usecases.PlantPositionUseCases;
import com.fich.sarh.plantofpositions.domain.model.PlantOfPosition;
import com.fich.sarh.plantofpositions.domain.ports.outbound.PlantPositionSpiPort;
import com.fich.sarh.plantofpositions.domain.service.PlantPositionService;
import com.fich.sarh.position.domain.ports.outbound.PositionSpiPort;
import com.fich.sarh.agent.domain.model.Agent;
import com.fich.sarh.position.domain.model.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Regla de negocio: transiciones de estado de la designación (planta).
 *
 * <p>El update de la planta ({@code PlantPositionUseCases.updatePlantOfPosition})
 * fija el estado solicitado sin máquina de estados: hoy el sistema acepta
 * cualquier transición (ver rojo #24) y no abre/cierra historial.</p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("B. Transiciones de estado de la designación (planta)")
class PlantStatusTransitionRulesTest {

    private static final Long PLANTA_ID = 5L;

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

    private PlantOfPosition planta;

    @BeforeEach
    void preparar() {
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

        planta = PlantOfPosition.builder()
                .id(PLANTA_ID)
                .position(Position.builder().id(10L).build())
                .agent(Agent.builder().id(1L).build())
                .organizationalSubUnit(OrganizationalSubUnit.builder().id(3L).build())
                .characterplantID(CharacterPlant.ORDINARIO)
                .currentStatusID(PlantStatus.ACTIVO)
                .build();
    }

    private PlantOfPosition comando(PlantStatus nuevoEstado) {
        return PlantOfPosition.builder()
                .position(Position.builder().id(10L).build())
                .agent(Agent.builder().id(1L).build())
                .organizationalSubUnit(OrganizationalSubUnit.builder().id(3L).build())
                .characterplantID(CharacterPlant.ORDINARIO)
                .currentStatusID(nuevoEstado)
                .build();
    }

    private void dadoPlantaExistente() {
        when(plantSpiPort.findPlantPositionById(PLANTA_ID)).thenReturn(Optional.of(planta));
        org.mockito.Mockito.lenient().when(plantSpiPort.savePlantOfPosition(org.mockito.ArgumentMatchers.any()))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    @DisplayName("17. La transición ACTIVO -> LICENCIA_TRANSITORIA se acepta y persiste")
    void transicionActivoALicenciaTransitoriaSeAcepta() {
        dadoPlantaExistente();

        PlantOfPosition actualizada = useCase.updatePlantOfPosition(PLANTA_ID, comando(PlantStatus.LICENCIA_TRANSITORIA));

        assertThat(actualizada.getCurrentStatusID()).isEqualTo(PlantStatus.LICENCIA_TRANSITORIA);
        verify(plantSpiPort).savePlantOfPosition(actualizada);
    }

    @Test
    @DisplayName("18. La transición LICENCIA_TRANSITORIA -> OCUPADO_TRANSITORIAMENTE se acepta y persiste")
    void transicionLicenciaAOcupadoTransitoriamenteSeAcepta() {
        planta.setCurrentStatusID(PlantStatus.LICENCIA_TRANSITORIA);
        dadoPlantaExistente();

        PlantOfPosition actualizada = useCase.updatePlantOfPosition(PLANTA_ID, comando(PlantStatus.OCUPADO_TRANSITORIAMENTE));

        assertThat(actualizada.getCurrentStatusID()).isEqualTo(PlantStatus.OCUPADO_TRANSITORIAMENTE);
        verify(plantSpiPort).savePlantOfPosition(actualizada);
    }

    @Test
    @DisplayName("19. La transición OCUPADO_TRANSITORIAMENTE -> ACTIVO se acepta y persiste")
    void transicionOcupadoTransitoriamenteAActivoSeAcepta() {
        planta.setCurrentStatusID(PlantStatus.OCUPADO_TRANSITORIAMENTE);
        dadoPlantaExistente();

        PlantOfPosition actualizada = useCase.updatePlantOfPosition(PLANTA_ID, comando(PlantStatus.ACTIVO));

        assertThat(actualizada.getCurrentStatusID()).isEqualTo(PlantStatus.ACTIVO);
        verify(plantSpiPort).savePlantOfPosition(actualizada);
    }

    @Test
    @DisplayName("20. La transición ACTIVO -> FINALIZADO se acepta y persiste el estado final")
    void transicionActivoAFinalizadoSeAcepta() {
        dadoPlantaExistente();

        PlantOfPosition actualizada = useCase.updatePlantOfPosition(PLANTA_ID, comando(PlantStatus.FINALIZADO));

        assertThat(actualizada.getCurrentStatusID()).isEqualTo(PlantStatus.FINALIZADO);
        verify(plantSpiPort).savePlantOfPosition(actualizada);
    }

    @Test
    @DisplayName("21. El update copia posición, agente, materia y carácter además del estado")
    void elUpdateCopiaLosDemasCamposAdemasDelEstado() {
        dadoPlantaExistente();
        Position otroCargo = Position.builder().id(11L).build();
        Agent otroAgente = Agent.builder().id(2L).build();
        OrganizationalSubUnit otraMateria = OrganizationalSubUnit.builder().id(4L).build();
        PlantOfPosition comando = PlantOfPosition.builder()
                .position(otroCargo)
                .agent(otroAgente)
                .organizationalSubUnit(otraMateria)
                .characterplantID(CharacterPlant.INTERINO)
                .currentStatusID(PlantStatus.LICENCIA_TRANSITORIA)
                .build();

        PlantOfPosition actualizada = useCase.updatePlantOfPosition(PLANTA_ID, comando);

        assertThat(actualizada.getPosition()).isSameAs(otroCargo);
        assertThat(actualizada.getAgent()).isSameAs(otroAgente);
        assertThat(actualizada.getOrganizationalSubUnit()).isSameAs(otraMateria);
        assertThat(actualizada.getCharacterplantID()).isEqualTo(CharacterPlant.INTERINO);
        assertThat(actualizada.getCurrentStatusID()).isEqualTo(PlantStatus.LICENCIA_TRANSITORIA);
    }

    @Test
    @DisplayName("22. Actualizar una plaza inexistente lanza ResourceNotFoundException")
    void actualizarPlantaInexistenteLanzaResourceNotFound() {
        when(plantSpiPort.findPlantPositionById(77L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.updatePlantOfPosition(77L, comando(PlantStatus.ACTIVO)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("La plaza seleccionada no fue encontrada no  encontrado");
        verify(plantSpiPort, never()).savePlantOfPosition(any());
    }

    @Test
    @DisplayName("23. El update no genera movimientos ni historial (el historial solo lo abre el alta)")
    void elUpdateNoGeneraMovimientosNiHistorial() {
        dadoPlantaExistente();

        useCase.updatePlantOfPosition(PLANTA_ID, comando(PlantStatus.FINALIZADO));

        verifyNoInteractions(movementSpiPort, plantHistorySpiPort);
    }

    @Test
    @DisplayName("[ROJO] 24. Una transición inválida (FINALIZADO -> OCUPADO_TRANSITORIAMENTE) debería rechazarse — no existe máquina de estados")
    void transicionInvalidaDeberiaSerRechazada() {
        planta.setCurrentStatusID(PlantStatus.FINALIZADO);
        when(plantSpiPort.findPlantPositionById(PLANTA_ID)).thenReturn(Optional.of(planta));

        assertThatThrownBy(() -> useCase.updatePlantOfPosition(PLANTA_ID, comando(PlantStatus.OCUPADO_TRANSITORIAMENTE)))
                .isInstanceOf(com.fich.sarh.common.exceptions.BusinessRuleViolationException.class);
    }
}
