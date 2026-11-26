package com.fich.sarh.point;

import com.fich.sarh.common.exceptions.ResourceNotFoundException;
import com.fich.sarh.point.application.usecases.PointUseCases;
import com.fich.sarh.point.domain.model.Point;
import com.fich.sarh.point.domain.ports.outbound.PointSpiPort;
import com.fich.sarh.position.domain.ports.inbound.PositionApiPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Regla de negocio: puntos de la bolsa de cargos (paridad).
 *
 * <p>La cantidad de puntos de {@code Point.amountPoint} es un valor absoluto
 * (no un porcentaje) y el porcentaje de aumento no tiene límite.</p>
 *
 * <p>Aísla {@link PointUseCases} mockeando {@link PointSpiPort} y el puerto
 * inverso {@code PositionApiPort}.</p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("A. Puntos de la bolsa de cargos — paridad de puntos")
class PointParityRulesTest {

    @Mock
    private PointSpiPort pointSpiPort;

    @Mock
    private PositionApiPort positionApiPort;

    @InjectMocks
    private PointUseCases useCase;

    @Test
    @DisplayName("1. La paridad global del 10% recalcula cada punto (100 puntos -> 110 puntos)")
    void paridadGlobalDiezPorcientoIncrementaLosPuntos() {
        Point punto = Point.builder().id(1L).amountPoint(100L).build();
        when(pointSpiPort.findAllPoints()).thenReturn(List.of(punto));

        useCase.applyGlobalParity(10);

        ArgumentCaptor<Point> captor = ArgumentCaptor.forClass(Point.class);
        verify(pointSpiPort).findAllPoints();
        verify(pointSpiPort).updateSave(captor.capture());
        assertThat(captor.getValue().getAmountPoint()).isEqualTo(110L);
    }

    @Test
    @DisplayName("2. La paridad global del 0% no modifica la cantidad de puntos existente")
    void paridadGlobalCeroPorcientoNoModificaMontos() {
        Point punto = Point.builder().id(1L).amountPoint(250L).build();
        when(pointSpiPort.findAllPoints()).thenReturn(List.of(punto));

        useCase.applyGlobalParity(0);

        ArgumentCaptor<Point> captor = ArgumentCaptor.forClass(Point.class);
        verify(pointSpiPort).updateSave(captor.capture());
        assertThat(captor.getValue().getAmountPoint()).isEqualTo(250L);
    }

    @Test
    @DisplayName("3. La paridad global negativa decrementa la cantidad de puntos (no hay límite inferior)")
    void paridadGlobalNegativaDecrementaMontos() {
        Point punto = Point.builder().id(1L).amountPoint(200L).build();
        when(pointSpiPort.findAllPoints()).thenReturn(List.of(punto));

        useCase.applyGlobalParity(-10);

        ArgumentCaptor<Point> captor = ArgumentCaptor.forClass(Point.class);
        verify(pointSpiPort).updateSave(captor.capture());
        assertThat(captor.getValue().getAmountPoint()).isEqualTo(180L);
    }

    @Test
    @DisplayName("4. La paridad global redondea al entero más cercano (100 puntos + 5.5% = 106 puntos)")
    void paridadGlobalRedondeaAlEnteroMasCercano() {
        Point punto = Point.builder().id(1L).amountPoint(100L).build();
        when(pointSpiPort.findAllPoints()).thenReturn(List.of(punto));

        useCase.applyGlobalParity(5.5);

        ArgumentCaptor<Point> captor = ArgumentCaptor.forClass(Point.class);
        verify(pointSpiPort).updateSave(captor.capture());
        assertThat(captor.getValue().getAmountPoint()).isEqualTo(106L);
    }

    @Test
    @DisplayName("5. Aplicar paridad por cargo inexistente lanza ResourceNotFoundException")
    void paridadPorCargoInexistenteLanzaResourceNotFound() {
        when(pointSpiPort.findPointById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.applyParityByPositionType(99L, 10L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No existe el tipo de cargo seleccionado no  encontrado");
    }

    @Test
    @DisplayName("6. Aplicar la paridad de un cargo existente actualiza exactamente la cantidad de puntos (75 -> 120 puntos)")
    void paridadPorCargoExistenteActualizaLaCantidadDePuntos() {
        Point punto = Point.builder().id(5L).amountPoint(75L).build();
        when(pointSpiPort.findPointById(5L)).thenReturn(Optional.of(punto));

        useCase.applyParityByPositionType(5L, 120L);

        assertThat(punto.getAmountPoint()).isEqualTo(120L);
        verify(pointSpiPort).updateSave(punto);
    }

    @Test
    @DisplayName("7. Actualizar un punto inexistente lanza ResourceNotFoundException")
    void actualizarPuntoInexistenteLanzaResourceNotFound() {
        when(pointSpiPort.findPointById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.updatePoint(404L, Point.builder().build()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No existe el tipo de cargo seleccionado no  encontrado");
    }

    @Test
    @DisplayName("8. Actualizar un punto existente copia todos los campos del comando")
    void actualizarPuntoExistenteActualizaTodosLosCampos() {
        Point guardado = Point.builder().id(1L).amountPoint(10L).positionCode(1L)
                .namePosition("Viejo").dedication("Vieja").date(new Date(0)).build();
        Date nuevaFecha = new Date(1_700_000_000_000L);
        Point comando = Point.builder().amountPoint(42L).positionCode(7L)
                .namePosition("Nuevo").dedication("Tiempo completo").date(nuevaFecha).build();
        when(pointSpiPort.findPointById(1L)).thenReturn(Optional.of(guardado));
        when(pointSpiPort.updateSave(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Point resultado = useCase.updatePoint(1L, comando);

        assertThat(resultado).isSameAs(guardado);
        assertThat(guardado.getAmountPoint()).isEqualTo(42L);
        assertThat(guardado.getPositionCode()).isEqualTo(7L);
        assertThat(guardado.getNamePosition()).isEqualTo("Nuevo");
        assertThat(guardado.getDedication()).isEqualTo("Tiempo completo");
        assertThat(guardado.getDate()).isEqualTo(nuevaFecha);
        verify(pointSpiPort).updateSave(guardado);
    }

    @Test
    @DisplayName("9. El porcentaje de aumento no tiene límite: la paridad global del 150% se aplica sin rechazo (100 puntos -> 250 puntos)")
    void porcentajeDeAumentoSinLimiteSeAplica() {
        Point punto = Point.builder().id(1L).amountPoint(100L).build();
        when(pointSpiPort.findAllPoints()).thenReturn(List.of(punto));

        useCase.applyGlobalParity(150);

        ArgumentCaptor<Point> captor = ArgumentCaptor.forClass(Point.class);
        verify(pointSpiPort).updateSave(captor.capture());
        assertThat(captor.getValue().getAmountPoint()).isEqualTo(250L);
    }
}
