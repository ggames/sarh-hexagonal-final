package com.fich.sarh.position.infrastructure.config;

import com.fich.sarh.plantofpositions.domain.ports.outbound.PlantPositionSpiPort;
import com.fich.sarh.plantofpositions.infrastructure.adapters.outbound.persistence.service.PlantPositionService;
import com.fich.sarh.position.domain.service.PositionDomainService;
import com.fich.sarh.relationshipofposition.application.usecases.port.out.RelationshipQueryPort;
import com.fich.sarh.relationshipofposition.domain.service.RelationshipPositionDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public RelationshipPositionDomainService relationshipPositionDomainService(RelationshipQueryPort relationQuery ){
        return new RelationshipPositionDomainService(relationQuery);
    }

    @Bean
    public PositionDomainService positionDomainService(){

        return new PositionDomainService();
    }

    @Bean
    public PlantPositionService plantPositionService(PlantPositionSpiPort plantPositionSpiPort){
        return new PlantPositionService(plantPositionSpiPort);
    }
}
