package com.fich.sarh.plantofpositions;

import com.fich.sarh.agent.domain.ports.outbound.AgentSpiPort;
import com.fich.sarh.movement.domain.ports.inbound.MovementApiPort;
import com.fich.sarh.organizationalsubunit.domain.ports.outbound.OrganizationalSubunitSpiPort;
import com.fich.sarh.planthistory.domain.ports.inbound.PlantHistoryApiPort;
import com.fich.sarh.plantofpositions.application.factory.PlantPositionFactory;
import com.fich.sarh.plantofpositions.application.handlers.MovementHandler;
import com.fich.sarh.plantofpositions.application.handlers.PlantHistoryHandler;
import com.fich.sarh.plantofpositions.application.usecases.PlantPositionUseCases;
import com.fich.sarh.plantofpositions.domain.ports.outbound.PlantExportReportPort;
import com.fich.sarh.plantofpositions.domain.ports.outbound.PlantPositionSpiPort;
import com.fich.sarh.plantofpositions.domain.service.PlantPositionService;
import com.fich.sarh.position.domain.ports.outbound.PositionSpiPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DisplayName("Verificación de Instanciación de Spring para PlantPositionUseCases")
class PlantPositionUseCasesBeanCreationTest {

    @Test
    @DisplayName("Spring debe instanciar PlantPositionUseCases exitosamente usando el constructor @Autowired")
    void springInstanciaPlantPositionUseCasesExitosamente() {
        DefaultListableBeanFactory factory = new DefaultListableBeanFactory();

        // Registrar procesador de @Autowired
        AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
        bpp.setBeanFactory(factory);
        factory.addBeanPostProcessor(bpp);

        // Registrar dependencias simuladas
        factory.registerSingleton("plantPositionSpiPort", mock(PlantPositionSpiPort.class));
        factory.registerSingleton("positionSpiPort", mock(PositionSpiPort.class));
        factory.registerSingleton("agentSpiPort", mock(AgentSpiPort.class));
        factory.registerSingleton("subunitSpiPort", mock(OrganizationalSubunitSpiPort.class));
        factory.registerSingleton("plantPositionService", mock(PlantPositionService.class));
        factory.registerSingleton("factory", mock(PlantPositionFactory.class));
        factory.registerSingleton("movementHandler", mock(MovementHandler.class));
        factory.registerSingleton("plantHistoryHandler", mock(PlantHistoryHandler.class));
        factory.registerSingleton("movementApiPort", mock(MovementApiPort.class));
        factory.registerSingleton("plantHistoryApiPort", mock(PlantHistoryApiPort.class));
        factory.registerSingleton("plantExportReportPort", mock(PlantExportReportPort.class));

        // Registrar bean bajo prueba
        factory.registerBeanDefinition("plantPositionUseCases", new RootBeanDefinition(PlantPositionUseCases.class));

        // Debe resolver e instanciar sin lanzar BeanCreationException
        PlantPositionUseCases useCase = factory.getBean(PlantPositionUseCases.class);
        assertThat(useCase).isNotNull();
    }
}
