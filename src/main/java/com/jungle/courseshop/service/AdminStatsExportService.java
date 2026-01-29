package com.jungle.courseshop.service;

import com.jungle.courseshop.dto.response.AdminStatsResponse;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AdminStatsExportService {

    public byte[] exportExcel(AdminStatsResponse stats, String period) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Admin Stats");

            CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            int rowIdx = 0;
            Row titleRow = sheet.createRow(rowIdx++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Admin Statistics (" + period + ")");
            titleCell.setCellStyle(headerStyle);

            rowIdx++;

            rowIdx = writeKeyValue(sheet, rowIdx, headerStyle, "Total users", stats.getTotalUsers());
            rowIdx = writeKeyValue(sheet, rowIdx, headerStyle, "Total courses", stats.getTotalCourses());
            rowIdx = writeKeyValue(sheet, rowIdx, headerStyle, "Total topics", stats.getTotalTopics());
            rowIdx = writeKeyValue(sheet, rowIdx, headerStyle, "Paid orders", stats.getTotalPaidOrders());
            rowIdx = writeKeyValue(sheet, rowIdx, headerStyle, "Total revenue", safe(stats.getTotalRevenue()));

            rowIdx++;

            Row chartHeader = sheet.createRow(rowIdx++);
            chartHeader.createCell(0).setCellValue("Label");
            chartHeader.createCell(1).setCellValue("Revenue");
            chartHeader.createCell(2).setCellValue("Paid orders");
            for (int i = 0; i <= 2; i++) {
                chartHeader.getCell(i).setCellStyle(headerStyle);
            }

            for (int i = 0; i < stats.getLabels().size(); i++) {
                Row r = sheet.createRow(rowIdx++);
                r.createCell(0).setCellValue(stats.getLabels().get(i));
                r.createCell(1).setCellValue(safe(stats.getRevenueSeries().get(i)).doubleValue());
                r.createCell(2).setCellValue(stats.getPaidOrdersSeries().get(i));
            }

            rowIdx++;
            Row topHeader = sheet.createRow(rowIdx++);
            topHeader.createCell(0).setCellValue("Top courses");
            topHeader.getCell(0).setCellStyle(headerStyle);

            Row topCols = sheet.createRow(rowIdx++);
            topCols.createCell(0).setCellValue("Course ID");
            topCols.createCell(1).setCellValue("Title");
            topCols.createCell(2).setCellValue("Enrollments");
            for (int i = 0; i <= 2; i++) {
                topCols.getCell(i).setCellStyle(headerStyle);
            }

            for (AdminStatsResponse.TopCourseStat c : stats.getTopCourses()) {
                Row r = sheet.createRow(rowIdx++);
                r.createCell(0).setCellValue(c.getCourseId());
                r.createCell(1).setCellValue(c.getTitle());
                r.createCell(2).setCellValue(c.getEnrollments());
            }

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Export Excel failed: " + e.getMessage(), e);
        }
    }

    public byte[] exportPdf(AdminStatsResponse stats, String period) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);
            Font headerFont = new Font(Font.HELVETICA, 12, Font.BOLD);
            Font normalFont = new Font(Font.HELVETICA, 11, Font.NORMAL);

            document.add(new Paragraph("Admin Statistics (" + period + ")", titleFont));
            document.add(Chunk.NEWLINE);

            PdfPTable summary = new PdfPTable(2);
            summary.setWidthPercentage(100);
            summary.addCell(new Phrase("Total users", headerFont));
            summary.addCell(new Phrase(String.valueOf(stats.getTotalUsers()), normalFont));
            summary.addCell(new Phrase("Total courses", headerFont));
            summary.addCell(new Phrase(String.valueOf(stats.getTotalCourses()), normalFont));
            summary.addCell(new Phrase("Total topics", headerFont));
            summary.addCell(new Phrase(String.valueOf(stats.getTotalTopics()), normalFont));
            summary.addCell(new Phrase("Paid orders", headerFont));
            summary.addCell(new Phrase(String.valueOf(stats.getTotalPaidOrders()), normalFont));
            summary.addCell(new Phrase("Total revenue", headerFont));
            summary.addCell(new Phrase(String.valueOf(safe(stats.getTotalRevenue())), normalFont));
            document.add(summary);

            document.add(Chunk.NEWLINE);
            document.add(new Paragraph("Time series", headerFont));

            PdfPTable ts = new PdfPTable(3);
            ts.setWidthPercentage(100);
            ts.addCell(new Phrase("Label", headerFont));
            ts.addCell(new Phrase("Revenue", headerFont));
            ts.addCell(new Phrase("Paid orders", headerFont));

            for (int i = 0; i < stats.getLabels().size(); i++) {
                ts.addCell(new Phrase(stats.getLabels().get(i), normalFont));
                ts.addCell(new Phrase(String.valueOf(safe(stats.getRevenueSeries().get(i))), normalFont));
                ts.addCell(new Phrase(String.valueOf(stats.getPaidOrdersSeries().get(i)), normalFont));
            }
            document.add(ts);

            document.add(Chunk.NEWLINE);
            document.add(new Paragraph("Top courses", headerFont));

            PdfPTable top = new PdfPTable(3);
            top.setWidthPercentage(100);
            top.addCell(new Phrase("Course ID", headerFont));
            top.addCell(new Phrase("Title", headerFont));
            top.addCell(new Phrase("Enrollments", headerFont));

            for (AdminStatsResponse.TopCourseStat c : stats.getTopCourses()) {
                top.addCell(new Phrase(String.valueOf(c.getCourseId()), normalFont));
                top.addCell(new Phrase(c.getTitle(), normalFont));
                top.addCell(new Phrase(String.valueOf(c.getEnrollments()), normalFont));
            }
            document.add(top);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Export PDF failed: " + e.getMessage(), e);
        }
    }

    private int writeKeyValue(Sheet sheet, int rowIdx, CellStyle headerStyle, String key, Object value) {
        Row row = sheet.createRow(rowIdx++);
        Cell c0 = row.createCell(0);
        c0.setCellValue(key);
        c0.setCellStyle(headerStyle);
        row.createCell(1).setCellValue(String.valueOf(value));
        return rowIdx;
    }

    private BigDecimal safe(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }
}
