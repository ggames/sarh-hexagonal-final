package com.fich.sarh.movement.infrastructure.adapters.outbound.persistence.adapter;

import com.fich.sarh.movement.domain.model.Movement;
import com.fich.sarh.movement.infrastructure.adapters.outbound.persistence.entity.MovementEntity;
import com.fich.sarh.movement.infrastructure.adapters.outbound.persistence.mapper.MovementMapper;
import com.fich.sarh.movement.infrastructure.adapters.outbound.persistence.repository.MovementRepository;
import com.fich.sarh.plantofpositions.domain.model.PlantOfPosition;
import com.fich.sarh.plantofpositions.domain.ports.outbound.PlantPositionSpiPort;
import com.fich.sarh.position.domain.model.Position;
import com.fich.sarh.position.domain.ports.outbound.PositionSpiPort;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MovementPersistenceAdapterTest {

    @Test
    void saveMovementPersistsEntityWithResolvedRelations() {
        MovementRepository repository = mock(MovementRepository.class);
        PlantPositionSpiPort plants = mock(PlantPositionSpiPort.class);
        PositionSpiPort positions = mock(PositionSpiPort.class);
        MovementMapper mapper = mock(MovementMapper.class);
        MovementEntity entity = new MovementEntity();
        when(mapper.toMovementEntity(any())).thenReturn(entity);
        when(positions.findPositionById(10L)).thenReturn(Optional.of(mock(Position.class)));
        when(plants.findPlantPositionById(20L)).thenReturn(Optional.of(mock(PlantOfPosition.class)));

        MovementPersistenceAdapter adapter =
                new MovementPersistenceAdapter(repository, plants, positions, mapper);
        adapter.saveMovement(Movement.builder().positionId(10L).plantId(20L).build());

        ArgumentCaptor<MovementEntity> captor = ArgumentCaptor.forClass(MovementEntity.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue()).isSameAs(entity);
        assertThat(entity.getPosition()).isNotNull();
        assertThat(entity.getPlant()).isNotNull();
    }
}
