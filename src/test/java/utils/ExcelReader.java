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
                if (row == null)
                    continue;

                Map<String, String> dataMap = new HashMap<>();

                for (int j = 0; j < headerRow.getLastCellNum(); j++) {

                    String key = headerRow.getCell(j).getStringCellValue().trim();
                    Cell cell = row.getCell(j);

                    String value = "";
                    if (cell != null) {
                        DataFormatter formatter = new DataFormatter();
                        value = formatter.formatCellValue(cell).trim();
                    }

                    dataMap.put(key, value);
                }

                dataList.add(dataMap);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to read Excel", e);
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

    public static List<Map<String, String>> getCanvasEvents() {
        return getSheetData("CanvasEvents");
    }
}