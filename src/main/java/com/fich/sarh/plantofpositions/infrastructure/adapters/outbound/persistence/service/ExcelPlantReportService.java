package com.fich.sarh.plantofpositions.infrastructure.adapters.outbound.persistence.service;

import com.fich.sarh.plantofpositions.domain.model.PlantProjectionDTO;
import com.fich.sarh.plantofpositions.domain.ports.outbound.PlantExportReportPort;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@Service
public class ExcelPlantReportService implements PlantExportReportPort, IExcelPlantReport {

    private static final String[] HEADERS =
            {"ID", "Apellido", "Nombre", "Materia", "Cargo", "Puntos", "Estado Planta"};

    @Override
    public ByteArrayInputStream createExcel(List<PlantProjectionDTO> plants) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            writeExcel(plants, out);
        } catch (IOException e) {
            throw new IllegalStateException("Error al generar el reporte Excel de plantas", e);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    @Override
    public void writeExcel(List<PlantProjectionDTO> plants, OutputStream out) throws IOException {
        try (SXSSFWorkbook workbook = new SXSSFWorkbook(100)) {
            workbook.setCompressTempFiles(true);
            Sheet sheet = workbook.createSheet("Plantas");

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(HEADERS[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 18 * 256);
            }

            int rowIdx = 1;
            for (PlantProjectionDTO plant : plants) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(plant.getId());
                row.createCell(1).setCellValue(plant.getFirstname());
                row.createCell(2).setCellValue(plant.getLastname());
                row.createCell(3).setCellValue(plant.getNameSubUnit());
                row.createCell(4).setCellValue(plant.getNamePosition());
                row.createCell(5).setCellValue(plant.getPointsAvailable() + "%");
                row.createCell(6).setCellValue(plant.getCurrentStatusID());
            }

            workbook.write(out);
            workbook.dispose();
        }
    }
}
