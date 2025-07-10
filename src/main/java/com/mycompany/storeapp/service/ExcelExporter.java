/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelExporter {
    public static boolean exportToExcel(String[] headers, List<?> dataList, String[] fieldNames, String filePath) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Data");

            // Tạo style với viền cho header
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            // Thêm viền cho header
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            // Tạo style với viền cho dữ liệu
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            // Tạo header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Điền dữ liệu
            for (int i = 0; i < dataList.size(); i++) {
                Row row = sheet.createRow(i + 1);
                Object item = dataList.get(i);
                Class<?> clazz = item.getClass();

                for (int j = 0; j < fieldNames.length; j++) {
                    Cell cell = row.createCell(j);
                    String getterName = "get" + fieldNames[j].substring(0, 1).toUpperCase() + fieldNames[j].substring(1);
                    try {
                        java.lang.reflect.Method getter = clazz.getMethod(getterName);
                        Object value = getter.invoke(item);
                        if (value instanceof java.util.Date) {
                            CellStyle dateStyle = workbook.createCellStyle();
                            dateStyle.cloneStyleFrom(dataStyle); // Kế thừa viền từ dataStyle
                            CreationHelper createHelper = workbook.getCreationHelper();
                            dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));
                            cell.setCellStyle(dateStyle);
                            if (value != null) {
                                cell.setCellValue((java.util.Date) value);
                            }
                        } else {
                            cell.setCellValue(value != null ? value.toString() : "");
                            cell.setCellStyle(dataStyle);
                        }
                    } catch (Exception e) {
                        cell.setCellValue("");
                        cell.setCellStyle(dataStyle);
                    }
                }
            }

            // Tự động điều chỉnh độ rộng cột
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Lưu file
            File file = new File(filePath);
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                workbook.write(outputStream);
            }

            JOptionPane.showMessageDialog(null, "Xuất Excel thành công: " + filePath, "Thành công", JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Lỗi khi xuất Excel: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}