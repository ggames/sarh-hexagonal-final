package com.fich.sarh.plantofpositions.infrastructure.adapters.outbound.persistence.service;

import com.fich.sarh.plantofpositions.domain.model.PlantProjectionDTO;

import java.io.ByteArrayInputStream;
import java.util.List;

public interface IExcelPlantReport {

    ByteArrayInputStream createExcel(List<PlantProjectionDTO> plants);
}
