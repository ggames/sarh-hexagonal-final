package com.fich.sarh.position.application.strategy;

import com.fich.sarh.common.StatusOfPositions;
import com.fich.sarh.common.exceptions.ResourceNotFoundException;
import com.fich.sarh.organizationalunit.domain.model.OrganizationalUnit;
import com.fich.sarh.organizationalunit.domain.ports.inbound.OrganizationalunitApiPort;
import com.fich.sarh.point.domain.model.Point;
import com.fich.sarh.point.domain.ports.inbound.PointApiPort;
import com.fich.sarh.position.domain.model.Position;
import com.fich.sarh.position.domain.model.PositionCommand;
import com.fich.sarh.position.domain.ports.outbound.PositionSpiPort;
import com.fich.sarh.transformation.domain.model.Transformation;
import com.fich.sarh.transformation.domain.ports.inbound.TransformationApiPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de estrategia FullUpdatePositionStrategy")
class FullUpdatePositionStrategyTest {

    @Mock
    private PositionSpiPort positionSpiPort;

    @Mock
    private PointApiPort pointApiPort;

    @Mock
    private TransformationApiPort transformationApiPort;

    @Mock
    private OrganizationalunitApiPort organizationalUnitApiPort;

    @InjectMocks
    private FullUpdatePositionStrategy strategy;

    private Position existingPosition;

    @BeforeEach
    void setUp() {
        existingPosition = Position.builder()
                .id(1L)
                .positionStatus(StatusOfPositions.ACTIVO)
                .build();
    }

    @Test
    @DisplayName("Debe actualizar usando el ID de unidad organizativa y persistir el cargo")
    void debeActualizarCorrectamenteYPersistir() {
        Long positionId = 1L;
        Long pointId = 10L;
        Long organizationalId = 20L;
        Long resolutionId = 30L;

        PositionCommand command = PositionCommand.builder()
                .point(pointId)
                .organizational(organizationalId)
                .positionStatus(StatusOfPositions.VACANTE_DEFINITIVA)
                .resolutionTransformation(resolutionId)
                .build();

        Point point = Point.builder().id(pointId).amountPoint(100L).build();
        Transformation transformation = Transformation.builder().id(resolutionId).build();
        OrganizationalUnit orgUnit = new OrganizationalUnit();
        orgUnit.setId(organizationalId);

        when(positionSpiPort.findPositionById(positionId)).thenReturn(Optional.of(existingPosition));
        when(pointApiPort.findPointById(pointId)).thenReturn(Optional.of(point));
        when(transformationApiPort.findTransformationById(resolutionId)).thenReturn(transformation);
        when(organizationalUnitApiPort.findOrganizationalunitById(organizationalId)).thenReturn(Optional.of(orgUnit));
        when(positionSpiPort.savePosition(any(Position.class))).thenAnswer(inv -> inv.getArgument(0));

        Position updated = strategy.update(positionId, command);

        assertThat(updated).isNotNull();
        assertThat(updated.getPositionStatus()).isEqualTo(StatusOfPositions.VACANTE_DEFINITIVA);
        assertThat(updated.getPoint()).isEqualTo(point);
        assertThat(updated.getOrganizationalUnit()).isEqualTo(orgUnit);
        assertThat(updated.getCreationResolution()).isEqualTo(transformation);

        verify(organizationalUnitApiPort).findOrganizationalunitById(organizationalId);
        verify(positionSpiPort).savePosition(existingPosition);
    }
}
