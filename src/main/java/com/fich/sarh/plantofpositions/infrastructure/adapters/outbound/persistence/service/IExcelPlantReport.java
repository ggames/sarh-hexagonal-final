package com.fich.sarh.plantofpositions.infrastructure.adapters.outbound.persistence.service;

import com.fich.sarh.plantofpositions.domain.model.PlantProjectionDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public interface IExcelPlantReport {

    ByteArrayInputStream createExcel(List<PlantProjectionDTO> plants);

    void writeExcel(List<PlantProjectionDTO> plants, OutputStream out) throws IOException;
}
