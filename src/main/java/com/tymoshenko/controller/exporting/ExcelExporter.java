package com.tymoshenko.controller.exporting;

import com.tymoshenko.model.TaskDto;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.util.List;

/**
 * @author Yakiv Tymoshenko
 * @since 16.02.2016
 */
@Component
public class ExcelExporter implements Exporter {

    private static final Logger LOG = LoggerFactory.getLogger(ExcelExporter.class);
    public static final String EXCEL_CHART_TEMPLATE = "/resources/excel/chart_template.xlt";

    public File export(List<TaskDto> taskList, File file) throws Exception {
        generateChart(taskList, file);
        return file;
    }

    private void generateChart(List<TaskDto> taskList, File excelFile) throws Exception {
        Workbook wb = loadWorkbookFromTemplate();

        copyTaskListToChartData(taskList, wb);

        flushWorkbookToFile(excelFile, wb);
    }

    private Workbook loadWorkbookFromTemplate() throws IOException {
        HSSFWorkbook hssfWorkbook = null;
        try {
            // Load Excel template with chart
            File excelChartTemplate = new File(ExcelExporter.class.getResource(EXCEL_CHART_TEMPLATE).getFile());
            FileInputStream fileInputStream = new FileInputStream(excelChartTemplate);
            // Using Excel 2003 format (*.xls)
            hssfWorkbook = new HSSFWorkbook(fileInputStream);
        } catch (IOException e) {
            LOG.error(String.format("Error creating Excel Workbook. Template file=%s. Error: %s", EXCEL_CHART_TEMPLATE, e.getMessage()));
            throw e;
        }
        return hssfWorkbook;
    }

    private void copyTaskListToChartData(List<TaskDto> taskList, Workbook wb) {
        Sheet sheet = wb.getSheet("Memory usage");
        final int _rows_number = taskList.size();
        Row row;
        Cell cell;
        TaskDto taskDto;
        for (int rowIndex = 0; rowIndex < _rows_number; rowIndex++) {
            row = sheet.createRow(rowIndex);
            taskDto = taskList.get(rowIndex);

            // Name
            cell = row.createCell(0);
            cell.setCellValue(taskDto.getName());

            // PID
            cell = row.createCell(1);
            cell.setCellValue(taskDto.getPid());

            // Memory
            cell = row.createCell(2);
            cell.setCellValue(taskDto.getMemory());
        }
    }

    private void flushWorkbookToFile(File excelFile, Workbook wb) throws IOException {
        try {
            FileOutputStream fileOut = new FileOutputStream(excelFile);
            wb.write(fileOut);
            wb.close();
            fileOut.flush();
            fileOut.close();
        } catch (IOException e) {
            LOG.error(String.format("Error writting excel content to a file: %s. Error: %s", excelFile.getPath(), e.getMessage()));
            throw e;
        }
    }

//
//    void writeToCsv(List<TaskDto> taskDtoList, File csvFile) {
//        final String _csvSeparator = ",";
//        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile), "UTF-8"))) {
//            for (TaskDto taskDto : taskDtoList) {
//                StringBuilder line = new StringBuilder();
//                line.append(taskDto.getName().trim());
//                line.append(_csvSeparator);
//                line.append(taskDto.getPid());
//                line.append(_csvSeparator);
//                line.append(taskDto.getMemory());
//                writer.write(line.toString());
//                writer.newLine();
//            }
//        } catch (IOException e) {
//            LOG.error(e.getMessage());
//        }
//    }
}
