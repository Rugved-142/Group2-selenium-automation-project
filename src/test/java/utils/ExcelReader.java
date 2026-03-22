package utils;

import org.apache.poi.ss.usermodel.*;
import java.io.FileInputStream;
import java.util.*;

public class ExcelReader {

    private static final String FILE_PATH = "src/test/resources/test_data.xlsx";

    private static List<Map<String, String>> getSheetData(String sheetName) {

        List<Map<String, String>> dataList = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(FILE_PATH);
                Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);

            if (sheet == null) {
                throw new RuntimeException("Sheet not found: " + sheetName);
            }

            Row headerRow = sheet.getRow(0);

            if (headerRow == null) {
                throw new RuntimeException("Header row missing in sheet: " + sheetName);
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);

                if (row == null) {
                    continue;
                }

                Map<String, String> dataMap = new HashMap<>();

                for (int j = 0; j < headerRow.getLastCellNum(); j++) {

                    Cell headerCell = headerRow.getCell(j);
                    if (headerCell == null) {
                        continue;
                    }

                    String key = headerCell.getStringCellValue();

                    Cell cell = row.getCell(j);
                    String value = "";

                    if (cell != null) {

                        switch (cell.getCellType()) {
                            case STRING:
                                value = cell.getStringCellValue();
                                break;
                            case NUMERIC:
                                value = String.valueOf(cell.getNumericCellValue());
                                break;
                            case BOOLEAN:
                                value = String.valueOf(cell.getBooleanCellValue());
                                break;
                            case FORMULA:
                                value = cell.getCellFormula();
                                break;
                            default:
                                value = "";
                        }
                    }

                    dataMap.put(key, value);
                }

                if (!dataMap.isEmpty()) {
                    dataList.add(dataMap);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to read Excel: " + e.getMessage(), e);
        }

        return dataList;
    }

    public static Map<String, String> getLoginCredentials() {
        return getSheetData("Login").get(0);
    }

    public static Map<String, String> getAcademicCalendarInfo() {
        return getSheetData("AcademicCalendar").get(0);
    }

    public static Map<String, String> getDatasetInfo() {
        return getSheetData("Dataset").get(0);
    }
}