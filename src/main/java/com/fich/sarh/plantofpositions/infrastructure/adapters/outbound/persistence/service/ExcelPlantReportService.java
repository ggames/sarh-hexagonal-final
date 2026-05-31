package com.fich.sarh.plantofpositions.infrastructure.adapters.outbound.persistence.service;

import com.fich.sarh.plantofpositions.domain.model.PlantProjectionDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExcelPlantReportService implements IExcelPlantReport{
    @Override
    public ByteArrayInputStream createExcel(List<PlantProjectionDTO> plants) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (Workbook workbook = new XSSFWorkbook() ) {
            Sheet sheet = workbook.createSheet("Plantas");

            // ✅ Estilo de encabezado
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // ✅ Encabezados
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Apellido", "Nombre", "Materia", "Cargo", "Puntos", "Estado Planta"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // ✅ Cuerpo
            int rowIdx = 1;
            for (PlantProjectionDTO plant : plants) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(plant.getId());
                row.createCell(1).setCellValue(plant.getFirstname());
                row.createCell(2).setCellValue(plant.getLastname());
                row.createCell(3).setCellValue(plant.getNameSubUnit());
                row.createCell(4).setCellValue(plant.getNamePosition());
                row.createCell(5).setCellValue(plant.getPointsAvailable()+ "%");
                row.createCell(6).setCellValue(plant.getCurrentStatusID());
            }

            // ✅ Autoajustar columnas
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // ✅ Escribir el workbook al stream
            workbook.write(out);


        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return new ByteArrayInputStream(out.toByteArray());

    }
}
