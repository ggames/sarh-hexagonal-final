package com.fich.sarh.plantofpositions.domain.ports.outbound;

import com.fich.sarh.plantofpositions.domain.model.PlantProjectionDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public interface PlantExportReportPort {
    ByteArrayInputStream createExcel(List<PlantProjectionDTO> plants);

    void writeExcel(List<PlantProjectionDTO> plants, OutputStream out) throws IOException;
}
