package com.salon.Utils;

import com.salon.Model.Expences;
import com.salon.Model.Income;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class ExportUtility {

    public static void exportToPDF(String filePath, List<Income> incomeList, List<Expences> expenseList, LocalDate selectedDate, boolean viewAll) {
        Document document = new Document();
        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
            writer.setPageEvent(new PdfBackgroundEvent());
            document.open();

            BaseColor backgroundColor = new BaseColor(18, 18, 18);
            BaseColor textColor = BaseColor.WHITE;
            BaseColor headerBgColor = new BaseColor(0, 198, 162);
            BaseColor rowAltColor = new BaseColor(30, 30, 30);

            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, textColor);
            Font sectionHeader = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, textColor);
            Font tableHeader = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, textColor);
            Font cellFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, textColor);
            Font summaryFont = new Font(Font.FontFamily.HELVETICA, 13, Font.NORMAL, textColor);

            try {
                Image logo = Image.getInstance("src/main/resources/images/finzo logo.png");
                logo.scaleToFit(90, 90);
                logo.setAlignment(Image.ALIGN_CENTER);
                document.add(logo);
            } catch (Exception e) {
                System.err.println("Logo not found or failed to load.");
            }

            String reportPeriod;
            if (viewAll) {
                reportPeriod = "Annual Report - " + selectedDate.getYear();
            } else {
                String month = selectedDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
                reportPeriod = "Monthly Report - " + month + " " + selectedDate.getYear();
            }

            Paragraph reportTitle = new Paragraph("Finzo " + reportPeriod, titleFont);
            reportTitle.setAlignment(Element.ALIGN_CENTER);
            reportTitle.setSpacingAfter(12f);
            document.add(reportTitle);

            double totalIncome = incomeList.stream().mapToDouble(Income::getValue).sum();
            double totalExpense = expenseList.stream().mapToDouble(Expences::getValue).sum();
            double saved = totalIncome - totalExpense;

            PdfPTable summaryTable = new PdfPTable(3);
            summaryTable.setWidthPercentage(100);
            summaryTable.setSpacingAfter(20f);
            summaryTable.setWidths(new float[]{3, 3, 3});

            addSummaryCell(summaryTable, "Total Income", String.format("Rs. %.2f", totalIncome), summaryFont, headerBgColor);
            addSummaryCell(summaryTable, "Total Expenses", String.format("Rs. %.2f", totalExpense), summaryFont, headerBgColor);
            addSummaryCell(summaryTable, "Saved", String.format("Rs. %.2f", saved), summaryFont, headerBgColor);

            document.add(summaryTable);

            addTableHeader(document, "Income Records", sectionHeader);
            PdfPTable incomeTable = new PdfPTable(3);
            incomeTable.setWidthPercentage(100);
            incomeTable.setWidths(new int[]{2, 5, 2});
            addStyledHeader(incomeTable, new String[]{"Date", "Description", "Value (Rs)"}, tableHeader, headerBgColor);

            boolean even = true;
            for (Income income : incomeList) {
                BaseColor rowColor = even ? rowAltColor : backgroundColor;
                addStyledCell(incomeTable, income.getDate().toString(), cellFont, rowColor);
                addStyledCell(incomeTable, income.getDescription(), cellFont, rowColor);
                addStyledCell(incomeTable, String.format("%.2f", income.getValue()), cellFont, rowColor);
                even = !even;
            }

            if (incomeList.isEmpty()) {
                PdfPCell empty = new PdfPCell(new Phrase("No income records available.", cellFont));
                empty.setColspan(3);
                empty.setHorizontalAlignment(Element.ALIGN_CENTER);
                empty.setBackgroundColor(backgroundColor);
                incomeTable.addCell(empty);
            }

            document.add(incomeTable);
            document.add(Chunk.NEWLINE);

            addTableHeader(document, "Expense Records", sectionHeader);
            PdfPTable expenseTable = new PdfPTable(4);
            expenseTable.setWidthPercentage(100);
            expenseTable.setWidths(new int[]{2, 5, 2, 2});
            addStyledHeader(expenseTable, new String[]{"Date", "Description", "Category", "Value (Rs)"}, tableHeader, headerBgColor);

            even = true;
            for (Expences expense : expenseList) {
                BaseColor rowColor = even ? rowAltColor : backgroundColor;
                addStyledCell(expenseTable, expense.getDate().toString(), cellFont, rowColor);
                addStyledCell(expenseTable, expense.getDescription(), cellFont, rowColor);
                addStyledCell(expenseTable, expense.getCategory(), cellFont, rowColor);
                addStyledCell(expenseTable, String.format("%.2f", expense.getValue()), cellFont, rowColor);
                even = !even;
            }

            if (expenseList.isEmpty()) {
                PdfPCell empty = new PdfPCell(new Phrase("No expense records available.", cellFont));
                empty.setColspan(4);
                empty.setHorizontalAlignment(Element.ALIGN_CENTER);
                empty.setBackgroundColor(backgroundColor);
                expenseTable.addCell(empty);
            }

            document.add(expenseTable);

            // Footer
            Paragraph footer = new Paragraph("\n\nAll rights reserved © LOGOZO | Contact: +94 74 042 9827", cellFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(30f);
            document.add(footer);

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addTableHeader(Document doc, String text, Font font) throws DocumentException {
        Paragraph header = new Paragraph(text, font);
        header.setSpacingBefore(10f);
        header.setSpacingAfter(5f);
        doc.add(header);
    }

    private static void addStyledHeader(PdfPTable table, String[] headers, Font font, BaseColor bgColor) {
        for (String col : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(col, font));
            cell.setBackgroundColor(bgColor);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(8);
            table.addCell(cell);
        }
    }

    private static void addStyledCell(PdfPTable table, String text, Font font, BaseColor bgColor) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bgColor);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setPadding(6);
        table.addCell(cell);
    }

    private static void addSummaryCell(PdfPTable table, String label, String value, Font font, BaseColor bgColor) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(bgColor);
        cell.setPadding(10);
        cell.setPhrase(new Phrase(label + "\n" + value, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
    }
}