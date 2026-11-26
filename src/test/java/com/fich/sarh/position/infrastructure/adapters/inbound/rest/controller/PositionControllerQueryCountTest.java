package com.fich.sarh.position.infrastructure.adapters.inbound.rest.controller;

import com.fich.sarh.position.domain.model.Position;
import com.fich.sarh.position.domain.ports.inbound.PositionApiPort;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PositionControllerQueryCountTest {

    @Test
    void findAllQueriesThePortOnlyOnce() {
        PositionApiPort port = mock(PositionApiPort.class);
        when(port.findAllPosition(0, 100)).thenReturn(List.<Position>of());

        new PositionController(port).findAll(0, 100);

        verify(port, times(1)).findAllPosition(0, 100);
    }
}
