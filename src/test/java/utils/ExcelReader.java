package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelReader {

    private static final String EXCEL_PATH = "src/test/resources/test_data.xlsx";

    public static List<Map<String, String>> getSheetData(String sheetName) {
        List<Map<String, String>> data = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(EXCEL_PATH);
             Workbook wb = new XSSFWorkbook(fis)) {

            Sheet sheet = wb.getSheet(sheetName);
            Row headerRow = sheet.getRow(0);
            int colCount = headerRow.getLastCellNum();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                Map<String, String> rowMap = new HashMap<>();
                for (int j = 0; j < colCount; j++) {
                    String key = getCellValue(headerRow.getCell(j));
                    String val = getCellValue(row.getCell(j));
                    rowMap.put(key, val);
                }
                data.add(rowMap);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read Excel: " + e.getMessage());
        }
        return data;
    }

    public static Map<String, String> getLoginCredentials() {
        return getSheetData("Login").get(0);
    }

    public static Map<String, String> getAcademicCalendarInfo() {
        return getSheetData("AcademicCalendar").get(0);
    }

    private static String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:  return cell.getStringCellValue().trim();
            case NUMERIC: return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            default:      return "";
        }
    }
}