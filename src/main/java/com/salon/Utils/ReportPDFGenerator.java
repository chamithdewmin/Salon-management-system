// ReportPDFGenerator.java
package com.salon.Utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;
import java.time.LocalDate;

public class ReportPDFGenerator {

    public static void generateSalesReport(LocalDate date, Connection conn) throws Exception {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Salon Sales Report");
        fileChooser.setInitialFileName("Salon_Sales_Report_" + date.getMonth() + "_" + date.getYear() + ".pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File selectedFile = fileChooser.showSaveDialog(new Stage());

        if (selectedFile == null) {
            CustomAlert.showAlert("Export Cancelled", "You didn't select a file.");
            return;
        }

        Document document = new Document(PageSize.A4, 36, 36, 54, 36);
        PdfWriter.getInstance(document, new FileOutputStream(selectedFile));
        document.open();

        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        Font dataFont = new Font(Font.FontFamily.HELVETICA, 11);
        Font footerFont = new Font(Font.FontFamily.HELVETICA, 10);

        document.add(new Paragraph("Monthly Sales Report", titleFont));
        document.add(new Paragraph("Month: " + date.getMonth() + " " + date.getYear(), dataFont));
        document.add(new Paragraph("Company Name: CodeFlaw Salon", dataFont));
        document.add(new Paragraph("Address: 123 Main Street, Colombo 07", dataFont));
        document.add(new Paragraph("Phone: 0771234567 | Email: codeflawsalon@example.com", dataFont));
        document.add(Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{20, 40, 20, 20});
        addTableHeader(table, headerFont, "DATE", "SERVICE", "AMOUNT", "TYPE");

        double totalIncome = 0;
        double totalExpense = 0;

        // Income Section
        String incomeSQL = "SELECT date, customerName, description, amount FROM income WHERE MONTH(date) = ? AND YEAR(date) = ?";
        try (PreparedStatement stmt = conn.prepareStatement(incomeSQL)) {
            stmt.setInt(1, date.getMonthValue());
            stmt.setInt(2, date.getYear());

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String reportDate = rs.getString("date");
                String customer = rs.getString("customerName");
                String description = rs.getString("description");
                double amount = rs.getDouble("amount");

                addTableRow(table, dataFont, reportDate, customer + " / " + description, amount, "Income");
                totalIncome += amount;
            }
        }

        // Expense Section
        String expenseSQL = "SELECT date, description, amount FROM expense WHERE MONTH(date) = ? AND YEAR(date) = ?";
        try (PreparedStatement stmt = conn.prepareStatement(expenseSQL)) {
            stmt.setInt(1, date.getMonthValue());
            stmt.setInt(2, date.getYear());

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String reportDate = rs.getString("date");
                String description = rs.getString("description");
                double amount = rs.getDouble("amount");

                addTableRow(table, dataFont, reportDate, description, amount, "Expense");
                totalExpense += amount;
            }
        }

        document.add(table);
        document.add(Chunk.NEWLINE);

        double netSaving = totalIncome - totalExpense;
        document.add(new Paragraph("Total Income: Rs. " + String.format("%.2f", totalIncome), headerFont));
        document.add(new Paragraph("Total Expense: Rs. " + String.format("%.2f", totalExpense), headerFont));
        document.add(new Paragraph("Net Saving: Rs. " + String.format("%.2f", netSaving), headerFont));

        document.add(Chunk.NEWLINE);
        document.add(new Paragraph("Seal & Signature: ___________________________", footerFont));
        document.add(new Paragraph("Notes: ______________________________________________________________________", footerFont));
        document.close();
    }

    private static void addTableHeader(PdfPTable table, Font font, String... headers) {
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, font));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            table.addCell(cell);
        }
    }

    private static void addTableRow(PdfPTable table, Font font,
                                    String date, String service, double amount, String type) {
        table.addCell(new Phrase(date != null ? date : "-", font));
        table.addCell(new Phrase(service != null ? service : "-", font));
        table.addCell(new Phrase(String.format("%.2f", amount), font));
        table.addCell(new Phrase(type, font));
    }
}