package com.fich.sarh.plantofpositions.infrastructure.adapters.outbound.persistence.mapper;

import com.fich.sarh.plantofpositions.domain.model.PlantOfPosition;
import com.fich.sarh.plantofpositions.infrastructure.adapters.outbound.persistence.entity.PlantOfPositionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface PlantOfPositionMapper {

   PlantOfPositionMapper INSTANCE = Mappers.getMapper(PlantOfPositionMapper.class);

   PlantOfPosition toPlantOfPosition(PlantOfPositionEntity entity);

   PlantOfPositionEntity toPlantOfPositionEntity(PlantOfPosition plantposition);

   List<PlantOfPosition> toPlantOfPositionList(List<PlantOfPositionEntity> entityList);
}
