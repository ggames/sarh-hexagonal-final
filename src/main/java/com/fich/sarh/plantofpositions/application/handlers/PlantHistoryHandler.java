package com.fich.sarh.plantofpositions.application.handlers;

import com.fich.sarh.common.PlantStatus;
import com.fich.sarh.planthistory.domain.model.PlantHistory;
import com.fich.sarh.planthistory.domain.ports.outbound.PlantHistorySpiPort;
import com.fich.sarh.plantofpositions.domain.model.PlantOfPosition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class PlantHistoryHandler {

    private final PlantHistorySpiPort historySpiPort;
    public PlantHistory register(PlantOfPosition plant,
                                 LocalDate dateFrom,
                                 PlantStatus status){
        var history = PlantHistory.builder()
                .plantOfPosition(plant)
                .dateFrom(dateFrom)
                .plantStatus(status).build();

        return historySpiPort.savePlantHistory(history);
    }
}
