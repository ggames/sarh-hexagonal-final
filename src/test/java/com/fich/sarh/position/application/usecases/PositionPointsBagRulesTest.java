package com.fich.sarh.position.application.usecases;

import com.fich.sarh.common.StatusOfPositions;
import com.fich.sarh.movement.domain.ports.inbound.MovementApiPort;
import com.fich.sarh.organizationalunit.domain.model.OrganizationalUnit;
import com.fich.sarh.organizationalunit.domain.ports.inbound.OrganizationalunitApiPort;
import com.fich.sarh.plantofpositions.domain.ports.inbound.PlantPositionApiPort;
import com.fich.sarh.point.domain.model.Point;
import com.fich.sarh.point.domain.ports.inbound.PointApiPort;
import com.fich.sarh.position.application.strategy.AvailablePointUpdatePositionStrategy;
import com.fich.sarh.position.application.strategy.FullUpdatePositionStrategy;
import com.fich.sarh.position.application.strategy.OriginatorUpdateStrategy;
import com.fich.sarh.position.domain.model.Position;
import com.fich.sarh.position.domain.model.PositionCommand;
import com.fich.sarh.position.domain.ports.outbound.PositionSpiPort;
import com.fich.sarh.position.domain.service.PositionDomainService;
import com.fich.sarh.relationshipofposition.domain.ports.inbound.RelationshipPositionApiPort;
import com.fich.sarh.relationshipofposition.domain.service.RelationshipPositionDomainService;
import com.fich.sarh.transformation.domain.model.Transformation;
import com.fich.sarh.transformation.domain.ports.inbound.TransformationApiPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Regla de negocio: reparto de los puntos de la bolsa de cargos entre los
 * cargos origen y puntos disponibles del cargo nuevo.
 *
 * <p>{@link PositionDomainService#calculatePosition} es lógica pura (se usa la
 * instancia real); el alta del cargo se aísla con los puertos mockeados.</p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("A. Puntos de la bolsa de cargos — reparto entre cargos origen")
class PositionPointsBagRulesTest {

    @Mock
    private PositionSpiPort positionSpiPort;

    @Mock
    private PointApiPort pointApiPort;

    @Mock
    private TransformationApiPort transformationApiPort;

    @Mock
    private OrganizationalunitApiPort organizationalUnitApiPort;

    @Mock
    private RelationshipPositionDomainService relationService;

    @Mock
    private FullUpdatePositionStrategy fullStrategy;

    @Mock
    private AvailablePointUpdatePositionStrategy availableStrategy;

    @Mock
    private OriginatorUpdateStrategy originatorStrategy;

    @Mock
    private PlantPositionApiPort plantPositionApiPort;

    @Mock
    private RelationshipPositionApiPort relationshipPositionApiPort;

    @Mock
    private MovementApiPort movementApiPort;

    private final PositionDomainService domainService = new PositionDomainService();

    private PositionApiPortUseCases useCase;

    @BeforeEach
    void construirCasoDeUso() {
        useCase = new PositionApiPortUseCases(
                positionSpiPort,
                domainService,
                relationService,
                pointApiPort,
                transformationApiPort,
                organizationalUnitApiPort,
                fullStrategy,
                availableStrategy,
                originatorStrategy,
                plantPositionApiPort,
                relationshipPositionApiPort,
                movementApiPort);
    }

    private Position origen(long id, long puntosDelCargo, long disponibles, StatusOfPositions estado) {
        return Position.builder()
                .id(id)
                .point(Point.builder().amountPoint(puntosDelCargo).build())
                .pointsAvailable(disponibles)
                .positionStatus(estado)
                .build();
    }

    @Test
    @DisplayName("10. calculatePosition con lista vacía (o nula) no opera ningún cargo")
    void calculatePositionSinOrigenesNoOpera() {
        assertThat(domainService.calculatePosition(List.of(), 100L)).isEmpty();
        assertThat(domainService.calculatePosition(null, 100L)).isNull();
    }

    @Test
    @DisplayName("11. Con excedente, el único origen se vacía en 0 (el remanente no deja saldo negativo)")
    void calculatePositionExcedenteVaciaElOrigenEnCero() {
        Position unico = origen(1L, 100L, 100L, StatusOfPositions.ACTIVO);

        domainService.calculatePosition(List.of(unico), 150L);

        assertThat(unico.getPointsAvailable()).isZero();
    }

    @Test
    @DisplayName("12. Un origen intermedio con excedente se agota en 0 y el resto se descuenta del siguiente")
    void calculatePositionAgotaElOrigenConExcedenteEnCero() {
        Position primero = origen(1L, 100L, 100L, StatusOfPositions.ACTIVO);
        Position segundo = origen(2L, 100L, 100L, StatusOfPositions.ACTIVO);

        domainService.calculatePosition(List.of(primero, segundo), 150L);

        assertThat(primero.getPointsAvailable()).isZero();
        assertThat(segundo.getPointsAvailable()).isEqualTo(50L);
    }

    @Test
    @DisplayName("13. Cuando el remanente es exactamente cero, el origen queda con 0 puntos disponibles")
    void calculatePositionRestoExactoCeroDejaCeroDisponible() {
        Position unico = origen(1L, 100L, 100L, StatusOfPositions.ACTIVO);

        domainService.calculatePosition(List.of(unico), 100L);

        assertThat(unico.getPointsAvailable()).isZero();
    }

    @Test
    @DisplayName("14. Con déficit, el origen queda con la diferencia de puntos faltante y el reparto se corta ahí")
    void calculatePositionConDeficitAsignaPuntosFaltantesYDetieneElReparto() {
        Position primero = origen(1L, 100L, 100L, StatusOfPositions.ACTIVO);
        Position segundo = origen(2L, 100L, 100L, StatusOfPositions.ACTIVO);

        domainService.calculatePosition(List.of(primero, segundo), 40L);

        assertThat(primero.getPointsAvailable()).isEqualTo(60L);
        assertThat(segundo.getPointsAvailable()).isEqualTo(100L);
    }

    @Test
    @DisplayName("15. Un cargo nuevo nace con 100 puntos disponibles")
    void crearCargoNuevoNaceConCienPuntosDisponibles() {
        when(pointApiPort.findPointById(1L))
                .thenReturn(java.util.Optional.of(Point.builder().id(1L).amountPoint(80L).build()));
        when(transformationApiPort.findTransformationById(2L))
                .thenReturn(Transformation.builder().id(2L).build());
        when(organizationalUnitApiPort.findOrganizationalunitById(3L))
                .thenReturn(java.util.Optional.of(new OrganizationalUnit()));
        when(positionSpiPort.findAllByIdIn(anyList())).thenReturn(List.of());
        when(positionSpiPort.savePosition(org.mockito.ArgumentMatchers.any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PositionCommand comando = PositionCommand.builder()
                .point(1L)
                .organizational(3L)
                .positionStatus(StatusOfPositions.ACTIVO)
                .originPositionIds(List.of())
                .resolutionTransformation(2L)
                .build();

        useCase.addPosition(comando);

        ArgumentCaptor<Position> captor = ArgumentCaptor.forClass(Position.class);
        verify(positionSpiPort).savePosition(captor.capture());
        assertThat(captor.getValue().getPointsAvailable()).isEqualTo(100L);
        assertThat(captor.getValue().getPositionStatus()).isEqualTo(StatusOfPositions.ACTIVO);
    }

    @Test
    @DisplayName("16. Al crear un cargo con orígenes, cada origen queda suprimido con su resolución y enlazado al cargo nuevo")
    void crearCargoConOrigenesLosSuprimeConLaResolucion() {
        Transformation resolucion = Transformation.builder().id(2L).build();
        Position origenPosicion = origen(10L, 100L, 100L, StatusOfPositions.ACTIVO);
        when(pointApiPort.findPointById(1L))
                .thenReturn(java.util.Optional.of(Point.builder().id(1L).amountPoint(100L).build()));
        when(transformationApiPort.findTransformationById(2L)).thenReturn(resolucion);
        when(organizationalUnitApiPort.findOrganizationalunitById(3L))
                .thenReturn(java.util.Optional.of(new OrganizationalUnit()));
        when(positionSpiPort.findAllByIdIn(anyList())).thenReturn(List.of(origenPosicion));
        when(positionSpiPort.savePosition(org.mockito.ArgumentMatchers.any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PositionCommand comando = PositionCommand.builder()
                .point(1L)
                .organizational(3L)
                .positionStatus(StatusOfPositions.ACTIVO)
                .originPositionIds(List.of(10L))
                .resolutionTransformation(2L)
                .build();

        Position creada = useCase.addPosition(comando);

        assertThat(creada).isNotNull();
        assertThat(creada.getParents()).contains(origenPosicion);

        ArgumentCaptor<Position> supresion = ArgumentCaptor.forClass(Position.class);
        verify(availableStrategy).update(eq(10L), supresion.capture());
        assertThat(supresion.getValue().getPositionStatus()).isEqualTo(StatusOfPositions.SUPRIMIDO);
        assertThat(supresion.getValue().getResolutionSuppression()).isSameAs(resolucion);
        assertThat(supresion.getValue().getPointsAvailable()).isZero();
    }
}
