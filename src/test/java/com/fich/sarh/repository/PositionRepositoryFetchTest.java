package com.fich.sarh.repository;

import com.fich.sarh.agent.infrastructure.adapters.outbound.persistence.entity.AgentEntity;
import com.fich.sarh.common.DocumentType;
import com.fich.sarh.common.StatusOfPositions;
import com.fich.sarh.organizationalsubunit.infrastructure.adapter.outbound.persistence.entity.OrganizationalSubUnitEntity;
import com.fich.sarh.organizationalunit.infrastructure.adapters.outbound.persistence.entity.OrganizationalUnitEntity;
import com.fich.sarh.plantofpositions.infrastructure.adapters.outbound.persistence.entity.PlantOfPositionEntity;
import com.fich.sarh.plantofpositions.infrastructure.adapters.outbound.persistence.repository.PlantOfPositionRepository;
import com.fich.sarh.point.infrastructure.adapter.output.persistence.entity.PointEntity;
import com.fich.sarh.position.infrastructure.adapters.output.persistence.entity.PositionEntity;
import com.fich.sarh.position.infrastructure.adapters.output.persistence.repository.PositionRepository;
import com.fich.sarh.transformation.infrastructure.adapters.outbound.persistence.entity.TransformationEntity;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class PositionRepositoryFetchTest {

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private PlantOfPositionRepository plantRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findAllWithRelationsInitializesFetchPlan() {
        AgentEntity director = persistAgent("11111111A");
        OrganizationalUnitEntity unit = entityManager.persist(OrganizationalUnitEntity.builder()
                .nameUnit("Dirección")
                .director(director)
                .build());
        PointEntity point = entityManager.persist(PointEntity.builder()
                .positionCode(1L)
                .namePosition("Analista")
                .amountPoint(10L)
                .date(new Date())
                .build());
        TransformationEntity resolution = entityManager.persist(TransformationEntity.builder()
                .resolutionNumber("R-1")
                .build());
        entityManager.persist(PositionEntity.builder()
                .active(true)
                .point(point)
                .organizationalUnit(unit)
                .positionStatus(StatusOfPositions.ACTIVO)
                .pointsAvailable(5L)
                .creationResolution(resolution)
                .build());
        entityManager.flush();
        entityManager.clear();

        var positions = positionRepository.findAllWithRelations(PageRequest.of(0, 10));

        assertThat(positions).hasSize(1);
        PositionEntity found = positions.get(0);
        assertThat(Hibernate.isInitialized(found.getPoint())).isTrue();
        assertThat(Hibernate.isInitialized(found.getOrganizationalUnit())).isTrue();
        assertThat(Hibernate.isInitialized(found.getOrganizationalUnit().getDirector())).isTrue();
        assertThat(Hibernate.isInitialized(found.getCreationResolution())).isTrue();
    }

    @Test
    void findByIdInitializesPlantRelations() {
        AgentEntity agent = persistAgent("22222222B");
        PositionEntity position = entityManager.persist(PositionEntity.builder()
                .active(true)
                .positionStatus(StatusOfPositions.ACTIVO)
                .pointsAvailable(1L)
                .build());
        OrganizationalSubUnitEntity subUnit = entityManager.persist(OrganizationalSubUnitEntity.builder()
                .nameSubUnit("Subunidad")
                .build());
        PlantOfPositionEntity plant = entityManager.persist(PlantOfPositionEntity.builder()
                .position(position)
                .agent(agent)
                .organizationalSubUnit(subUnit)
                .characterplantID(com.fich.sarh.common.CharacterPlant.ORDINARIO)
                .currentStatusID(com.fich.sarh.common.PlantStatus.ACTIVO)
                .build());
        entityManager.flush();
        entityManager.clear();

        PlantOfPositionEntity found = plantRepository.findById(plant.getId()).orElseThrow();

        assertThat(Hibernate.isInitialized(found.getPosition())).isTrue();
        assertThat(Hibernate.isInitialized(found.getAgent())).isTrue();
        assertThat(Hibernate.isInitialized(found.getOrganizationalSubUnit())).isTrue();
    }

    private AgentEntity persistAgent(String document) {
        return entityManager.persist(AgentEntity.builder()
                .firstname("Ana")
                .lastname("Pérez")
                .documenttype(DocumentType.DNI)
                .document(document)
                .build());
    }
}
