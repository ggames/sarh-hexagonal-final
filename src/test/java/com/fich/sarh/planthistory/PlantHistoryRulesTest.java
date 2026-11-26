package com.fich.sarh.planthistory;

import com.fich.sarh.common.PlantStatus;
import com.fich.sarh.common.exceptions.ResourceNotFoundException;
import com.fich.sarh.planthistory.application.usecases.PlantHistoryUseCases;
import com.fich.sarh.planthistory.domain.model.PlantHistory;
import com.fich.sarh.planthistory.domain.ports.outbound.PlantHistorySpiPort;
import com.fich.sarh.plantofpositions.domain.model.PlantOfPosition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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
 * Reglas del historial de la designación: no actualizar ni eliminar
 * historiales inexistentes y copiar estado/planta/fechas al actualizar.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("B. Transiciones de estado — historial de la designación")
class PlantHistoryRulesTest {

    @Mock
    private PlantHistorySpiPort plantHistorySpiPort;

    @InjectMocks
    private PlantHistoryUseCases useCase;

    @Test
    @DisplayName("25. Actualizar un historial inexistente lanza ResourceNotFoundException")
    void actualizarHistorialInexistenteLanzaResourceNotFound() {
        when(plantHistorySpiPort.findPlantHistoryById(9L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.updatePlantHistory(9L, PlantHistory.builder().build()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Historia de planta no encontrada no  encontrado");
        verify(plantHistorySpiPort, never()).savePlantHistory(any());
    }

    @Test
    @DisplayName("26. Actualizar un historial existente copia estado, planta y fechas del comando y guarda")
    void actualizarHistorialExistenteCopiaLosCamposYGuarda() {
        PlantHistory guardado = PlantHistory.builder()
                .id(1L)
                .plantOfPosition(PlantOfPosition.builder().id(5L).build())
                .plantStatus(PlantStatus.ACTIVO)
                .dateFrom(LocalDate.of(2025, 1, 1))
                .build();
        when(plantHistorySpiPort.findPlantHistoryById(1L)).thenReturn(Optional.of(guardado));
        PlantHistory comando = PlantHistory.builder()
                .plantOfPosition(PlantOfPosition.builder().id(6L).build())
                .plantStatus(PlantStatus.FINALIZADO)
                .dateFrom(LocalDate.of(2026, 2, 1))
                .dateTo(LocalDate.of(2026, 3, 1))
                .build();

        useCase.updatePlantHistory(1L, comando);

        assertThat(guardado.getPlantStatus()).isEqualTo(PlantStatus.FINALIZADO);
        assertThat(guardado.getPlantOfPosition().getId()).isEqualTo(6L);
        assertThat(guardado.getDateFrom()).isEqualTo(LocalDate.of(2026, 2, 1));
        assertThat(guardado.getDateTo()).isEqualTo(LocalDate.of(2026, 3, 1));
        verify(plantHistorySpiPort).savePlantHistory(guardado);
    }

    @Test
    @DisplayName("27. Eliminar un historial inexistente lanza ResourceNotFoundException")
    void eliminarHistorialInexistenteLanzaResourceNotFound() {
        when(plantHistorySpiPort.findPlantHistoryById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.deletePlantHistory(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Historia de planta no encontrada con ID: 99 no  encontrado");
        verify(plantHistorySpiPort, never()).deletePlantHistory(99L);
    }
}
