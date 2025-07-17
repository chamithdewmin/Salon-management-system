package com.salon.Controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.salon.Model.DatabaseConnection;
import com.salon.Model.Expenses;
import com.salon.Model.Income.Income;
import com.salon.Model.Income.IncomeDAO;
import com.salon.Utils.CustomAlert;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class ReportController {

    @FXML private Label totalIncomeLabel;
    @FXML private Label totalExpenseLabel;
    @FXML private Label totalSavingLabel;

    @FXML private ComboBox<String> typeComboBox;
    @FXML private DatePicker filterDatePicker;

    @FXML private TableView<Income> reportTable;
    @FXML private TableColumn<Income, String> dateColumn;
    @FXML private TableColumn<Income, String> customerColumn;
    @FXML private TableColumn<Income, String> serviceColumn;
    @FXML private TableColumn<Income, String> staffColumn;
    @FXML private TableColumn<Income, Double> amountColumn;

    @FXML private Button downloadButton;
    @FXML public Button refreshButton;

    private IncomeDAO financeDAO;
    private Connection conn;

    @FXML
    public void initialize() {
        conn = DatabaseConnection.connect();
        if (conn == null) {
            CustomAlert.showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to connect to the database.");
            return;
        }

        financeDAO = new IncomeDAO(conn);
        setupTableColumns();

        typeComboBox.setItems(FXCollections.observableArrayList("Income", "Expense"));
        typeComboBox.setOnAction(e -> reloadData());
        filterDatePicker.setOnAction(e -> reloadData());
        downloadButton.setOnAction(e -> handleDownloadPDF());

        refreshUI();
        loadSummary();
    }

    private void setupTableColumns() {
        dateColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDate()));
        customerColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCustomerName()));
        serviceColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDescription()));
        staffColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStaffName()));
        amountColumn.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getAmount()));
    }

    private void reloadData() {
        String selectedType = typeComboBox.getValue();
        LocalDate selectedDate = filterDatePicker.getValue();
        if (selectedType == null || selectedDate == null) return;

        int year = selectedDate.getYear();
        int month = selectedDate.getMonthValue();

        try {
            if (selectedType.equals("Income")) {
                List<Income> incomeList = getIncomeDataByMonth(year, month);
                reportTable.getItems().setAll(incomeList);
            } else if (selectedType.equals("Expense")) {
                List<Expenses> expenseList = getExpenseDataByMonth(year, month);
                List<Income> wrapped = new ArrayList<>();
                for (Expenses e : expenseList) {
                    wrapped.add(new Income(
                            e.getDate().toString(),
                            e.getDescription(),
                            e.getAmount(),
                            "-", "-", "-"
                    ));
                }
                reportTable.getItems().setAll(wrapped);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            CustomAlert.showAlert(Alert.AlertType.ERROR, "Load Error", "Failed to load filtered data.");
        }
    }


    private void loadFinanceData(String type) {
        // same as original, not related to monthly PDF
    }

    private void loadSummary() {
        try {
            Income summary = financeDAO.getFinanceSummary();
            totalIncomeLabel.setText("Rs. " + String.format("%.2f", summary.getIncome()));
            totalExpenseLabel.setText("Rs. " + String.format("%.2f", summary.getExpenses()));
            totalSavingLabel.setText("Rs. " + String.format("%.2f", summary.getSaved()));
        } catch (SQLException e) {
            e.printStackTrace();
            CustomAlert.showAlert(Alert.AlertType.ERROR, "Load Error", "Failed to load summary data.");
        }
    }

    @FXML
    private void handleRefresh() {
        refreshUI();
        loadSummary();
    }

    private void refreshUI() {
        typeComboBox.getSelectionModel().clearSelection();
        filterDatePicker.setValue(null);
        reportTable.getItems().clear();
    }

    @FXML
    private void handleDownloadPDF() {
        Dialog<LocalDate> dialog = new Dialog<>();
        dialog.setTitle("Select Month");
        dialog.setHeaderText("Please select a date from the desired month");

        DatePicker monthPicker = new DatePicker();
        dialog.getDialogPane().setContent(monthPicker);

        ButtonType generateButton = new ButtonType("Generate", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(generateButton, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == generateButton) {
                return monthPicker.getValue();
            }
            return null;
        });

        Optional<LocalDate> result = dialog.showAndWait();

        result.ifPresent(selectedDate -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Report as PDF");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

            String fileName = "Financial_Report_" + selectedDate.getYear() + "_" + selectedDate.getMonthValue() + ".pdf";
            fileChooser.setInitialFileName(fileName);

            Stage stage = (Stage) downloadButton.getScene().getWindow();
            File file = fileChooser.showSaveDialog(stage);

            if (file != null) {
                try {
                    generatePDFReport(file, selectedDate.getYear(), selectedDate.getMonthValue());
                    CustomAlert.showSuccess("PDF report generated successfully.");
                } catch (Exception e) {
                    e.printStackTrace();
                    CustomAlert.showAlert(Alert.AlertType.ERROR, "Error", "PDF generation failed.");
                }
            }
        });
    }



    private void generatePDFReport(File file, int year, int month) throws DocumentException, IOException, SQLException {
        Document document = new Document(PageSize.A4, 40, 40, 60, 40);
        PdfWriter.getInstance(document, new FileOutputStream(file));
        document.open();

        // Add header with salon name and report title
        addReportHeader(document, year, month);

        // Get data for calculations
        List<Income> incomeList = getIncomeDataByMonth(year, month);
        List<Expenses> expenseList = getExpenseDataByMonth(year, month);

        // Add comprehensive summary at the top
        addComprehensiveSummary(document, incomeList, expenseList);

        // Add detailed sections
        addIncomeTable(document, incomeList);
        addStaffWiseIncomeSummary(document, incomeList);
        addExpenseTable(document, expenseList);
        addExpenseCategorySummary(document, expenseList);

        // Add footer
        addReportFooter(document);

        document.close();
    }

    private void addReportHeader(Document document, int year, int month) throws DocumentException {
        // Company/Salon Header
        Font companyFont = new Font(Font.FontFamily.HELVETICA, 22, Font.BOLD, BaseColor.DARK_GRAY);
        Paragraph companyName = new Paragraph("SALON MANAGEMENT SYSTEM", companyFont);
        companyName.setAlignment(Element.ALIGN_CENTER);
        companyName.setSpacingAfter(10);
        document.add(companyName);

        // Report Title
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.BLACK);
        Paragraph title = new Paragraph("Monthly Financial Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Report Period
        Month selectedMonth = Month.of(month);
        Font periodFont = new Font(Font.FontFamily.HELVETICA, 14, Font.NORMAL, BaseColor.BLUE);
        Paragraph period = new Paragraph("Report Period: " + selectedMonth + " " + year, periodFont);
        period.setAlignment(Element.ALIGN_CENTER);
        period.setSpacingAfter(10);
        document.add(period);

        // Generation Date
        Font dateFont = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, BaseColor.GRAY);
        Paragraph generationDate = new Paragraph("Generated on: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")), dateFont);
        generationDate.setAlignment(Element.ALIGN_CENTER);
        generationDate.setSpacingAfter(30);
        document.add(generationDate);

        // Add separator line
        LineSeparator separator = new LineSeparator();
        separator.setLineColor(BaseColor.GRAY);
        document.add(new Chunk(separator));
        document.add(Chunk.NEWLINE);
    }

    private void addComprehensiveSummary(Document document, List<Income> incomeList, List<Expenses> expenseList) throws DocumentException {
        Font sectionFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, BaseColor.DARK_GRAY);
        Paragraph sectionTitle = new Paragraph("FINANCIAL SUMMARY", sectionFont);
        sectionTitle.setAlignment(Element.ALIGN_CENTER);
        sectionTitle.setSpacingAfter(20);
        document.add(sectionTitle);

        // Calculate totals
        double totalIncome = incomeList.stream().mapToDouble(Income::getAmount).sum();
        double totalExpenses = expenseList.stream().mapToDouble(Expenses::getAmount).sum();
        double netProfit = totalIncome - totalExpenses;

        // Create summary table
        PdfPTable summaryTable = new PdfPTable(2);
        summaryTable.setWidthPercentage(70);
        summaryTable.setHorizontalAlignment(Element.ALIGN_CENTER);
        summaryTable.setWidths(new float[]{3, 2});

        Font labelFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.BLACK);
        Font valueFont = new Font(Font.FontFamily.HELVETICA, 14, Font.NORMAL, BaseColor.BLACK);

        // Add summary rows
        addSummaryRow(summaryTable, "Total Income:", String.format("Rs. %.2f", totalIncome), labelFont, valueFont, BaseColor.GREEN);
        addSummaryRow(summaryTable, "Total Expenses:", String.format("Rs. %.2f", totalExpenses), labelFont, valueFont, BaseColor.RED);
        addSummaryRow(summaryTable, "Net Profit/Loss:", String.format("Rs. %.2f", netProfit), labelFont, valueFont,
                netProfit >= 0 ? BaseColor.GREEN : BaseColor.RED);

        document.add(summaryTable);

        // Add transaction counts
        Paragraph transactionInfo = new Paragraph("\nTransaction Summary:",
                new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD));
        transactionInfo.setSpacingAfter(10);
        document.add(transactionInfo);

        PdfPTable countTable = new PdfPTable(2);
        countTable.setWidthPercentage(50);
        countTable.setHorizontalAlignment(Element.ALIGN_CENTER);

        Font countFont = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL);
        addCountRow(countTable, "Total Income Transactions:", String.valueOf(incomeList.size()), countFont);
        addCountRow(countTable, "Total Expense Transactions:", String.valueOf(expenseList.size()), countFont);

        document.add(countTable);
        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);
    }

    private void addSummaryRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont, BaseColor valueColor) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(10);
        labelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(labelCell);

        Font coloredValueFont = new Font(valueFont.getFamily(), valueFont.getSize(), valueFont.getStyle(), valueColor);
        PdfPCell valueCell = new PdfPCell(new Phrase(value, coloredValueFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(10);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(valueCell);
    }

    private void addCountRow(PdfPTable table, String label, String value, Font font) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, font));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(5);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, font));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(5);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(valueCell);
    }

    private void addIncomeTable(Document document, List<Income> incomeList) throws DocumentException {
        Font sectionFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.DARK_GRAY);
        Paragraph section = new Paragraph("INCOME DETAILS", sectionFont);
        section.setSpacingBefore(20);
        section.setSpacingAfter(15);
        document.add(section);

        if (incomeList.isEmpty()) {
            Paragraph noData = new Paragraph("No income data available for this month.",
                    new Font(Font.FontFamily.HELVETICA, 11, Font.ITALIC, BaseColor.GRAY));
            noData.setSpacingAfter(20);
            document.add(noData);
            return;
        }

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2, 3, 3, 2.5f, 2});

        Font headerFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.WHITE);
        Font cellFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);

        // Enhanced header styling
        String[] headers = {"Date", "Customer", "Service", "Staff", "Amount (Rs.)"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(BaseColor.DARK_GRAY);
            cell.setPadding(10);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        // Add data rows with alternating colors
        double total = 0;
        boolean alternate = false;
        for (Income income : incomeList) {
            BaseColor rowColor = alternate ? new BaseColor(245, 245, 245) : BaseColor.WHITE;

            addDataCell(table, income.getDate(), cellFont, rowColor, Element.ALIGN_CENTER);
            addDataCell(table, income.getCustomerName(), cellFont, rowColor, Element.ALIGN_LEFT);
            addDataCell(table, income.getDescription(), cellFont, rowColor, Element.ALIGN_LEFT);
            addDataCell(table, income.getStaffName(), cellFont, rowColor, Element.ALIGN_CENTER);
            addDataCell(table, String.format("%.2f", income.getAmount()), cellFont, rowColor, Element.ALIGN_RIGHT);

            total += income.getAmount();
            alternate = !alternate;
        }

        // Add total row
        Font totalFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);
        for (int i = 0; i < 4; i++) {
            PdfPCell cell = new PdfPCell(new Phrase(i == 3 ? "TOTAL:" : "", totalFont));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setPadding(10);
            cell.setHorizontalAlignment(i == 3 ? Element.ALIGN_RIGHT : Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        PdfPCell totalCell = new PdfPCell(new Phrase(String.format("%.2f", total), totalFont));
        totalCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        totalCell.setPadding(10);
        totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(totalCell);

        document.add(table);
    }

    private void addDataCell(PdfPTable table, String text, Font font, BaseColor backgroundColor, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(backgroundColor);
        cell.setPadding(8);
        cell.setHorizontalAlignment(alignment);
        table.addCell(cell);
    }

    private void addStaffWiseIncomeSummary(Document document, List<Income> incomeList) throws DocumentException {
        if (incomeList.isEmpty()) return;

        Font sectionFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.DARK_GRAY);
        Paragraph sectionTitle = new Paragraph("STAFF-WISE INCOME SUMMARY", sectionFont);
        sectionTitle.setSpacingBefore(30);
        sectionTitle.setSpacingAfter(15);
        document.add(sectionTitle);

        Map<String, Double> staffTotals = new HashMap<>();
        for (Income income : incomeList) {
            staffTotals.merge(income.getStaffName(), income.getAmount(), Double::sum);
        }

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(70);
        table.setWidths(new float[]{3, 2, 2});

        Font headerFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.WHITE);
        Font cellFont = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL);

        // Headers
        String[] headers = {"Staff Name", "Total Income (Rs.)", "Percentage"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(BaseColor.DARK_GRAY);
            cell.setPadding(10);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        double totalIncome = staffTotals.values().stream().mapToDouble(Double::doubleValue).sum();
        boolean alternate = false;

        for (Map.Entry<String, Double> entry : staffTotals.entrySet()) {
            BaseColor rowColor = alternate ? new BaseColor(245, 245, 245) : BaseColor.WHITE;
            double percentage = (entry.getValue() / totalIncome) * 100;

            addDataCell(table, entry.getKey(), cellFont, rowColor, Element.ALIGN_LEFT);
            addDataCell(table, String.format("%.2f", entry.getValue()), cellFont, rowColor, Element.ALIGN_RIGHT);
            addDataCell(table, String.format("%.1f%%", percentage), cellFont, rowColor, Element.ALIGN_RIGHT);

            alternate = !alternate;
        }

        document.add(table);
    }

    private void addExpenseTable(Document document, List<Expenses> expenseList) throws DocumentException {
        Font sectionFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.DARK_GRAY);
        Paragraph section = new Paragraph("EXPENSE DETAILS", sectionFont);
        section.setSpacingBefore(30);
        section.setSpacingAfter(15);
        document.add(section);

        if (expenseList.isEmpty()) {
            Paragraph noData = new Paragraph("No expense data available for this month.",
                    new Font(Font.FontFamily.HELVETICA, 11, Font.ITALIC, BaseColor.GRAY));
            noData.setSpacingAfter(20);
            document.add(noData);
            return;
        }

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2, 4, 2, 2});

        Font headerFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.WHITE);
        Font cellFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);

        String[] headers = {"Date", "Description", "Category", "Amount (Rs.)"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(BaseColor.DARK_GRAY);
            cell.setPadding(10);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        double total = 0;
        boolean alternate = false;
        for (Expenses expense : expenseList) {
            BaseColor rowColor = alternate ? new BaseColor(245, 245, 245) : BaseColor.WHITE;

            addDataCell(table, expense.getDate().toString(), cellFont, rowColor, Element.ALIGN_CENTER);
            addDataCell(table, expense.getDescription(), cellFont, rowColor, Element.ALIGN_LEFT);
            addDataCell(table, expense.getCategory(), cellFont, rowColor, Element.ALIGN_CENTER);
            addDataCell(table, String.format("%.2f", expense.getAmount()), cellFont, rowColor, Element.ALIGN_RIGHT);

            total += expense.getAmount();
            alternate = !alternate;
        }

        // Add total row
        Font totalFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);
        for (int i = 0; i < 3; i++) {
            PdfPCell cell = new PdfPCell(new Phrase(i == 2 ? "TOTAL:" : "", totalFont));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setPadding(10);
            cell.setHorizontalAlignment(i == 2 ? Element.ALIGN_RIGHT : Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        PdfPCell totalCell = new PdfPCell(new Phrase(String.format("%.2f", total), totalFont));
        totalCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        totalCell.setPadding(10);
        totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(totalCell);

        document.add(table);
    }

    private void addExpenseCategorySummary(Document document, List<Expenses> expenseList) throws DocumentException {
        if (expenseList.isEmpty()) return;

        Font sectionFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.DARK_GRAY);
        Paragraph sectionTitle = new Paragraph("EXPENSE CATEGORY SUMMARY", sectionFont);
        sectionTitle.setSpacingBefore(30);
        sectionTitle.setSpacingAfter(15);
        document.add(sectionTitle);

        Map<String, Double> categoryTotals = new HashMap<>();
        for (Expenses expense : expenseList) {
            categoryTotals.merge(expense.getCategory(), expense.getAmount(), Double::sum);
        }

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(70);
        table.setWidths(new float[]{3, 2, 2});

        Font headerFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.WHITE);
        Font cellFont = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL);

        String[] headers = {"Category", "Total Amount (Rs.)", "Percentage"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(BaseColor.DARK_GRAY);
            cell.setPadding(10);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        double totalExpenses = categoryTotals.values().stream().mapToDouble(Double::doubleValue).sum();
        boolean alternate = false;

        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            BaseColor rowColor = alternate ? new BaseColor(245, 245, 245) : BaseColor.WHITE;
            double percentage = (entry.getValue() / totalExpenses) * 100;

            addDataCell(table, entry.getKey(), cellFont, rowColor, Element.ALIGN_LEFT);
            addDataCell(table, String.format("%.2f", entry.getValue()), cellFont, rowColor, Element.ALIGN_RIGHT);
            addDataCell(table, String.format("%.1f%%", percentage), cellFont, rowColor, Element.ALIGN_RIGHT);

            alternate = !alternate;
        }

        document.add(table);
    }

    private void addReportFooter(Document document) throws DocumentException {
        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);

        LineSeparator separator = new LineSeparator();
        separator.setLineColor(BaseColor.GRAY);
        document.add(new Chunk(separator));

        Font footerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, BaseColor.GRAY);
        Paragraph footer = new Paragraph("This report was generated automatically by the Salon Management System.", footerFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(10);
        document.add(footer);

        Paragraph disclaimer = new Paragraph("All amounts are in Sri Lankan Rupees (LKR). Please verify all financial data before making business decisions.", footerFont);
        disclaimer.setAlignment(Element.ALIGN_CENTER);
        disclaimer.setSpacingBefore(5);
        document.add(disclaimer);
    }

    private List<Income> getIncomeDataByMonth(int year, int month) throws SQLException {
        List<Income> incomeList = new ArrayList<>();
        String sql = "SELECT * FROM income WHERE YEAR(date) = ? AND MONTH(date) = ? ORDER BY date DESC";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, year);
            stmt.setInt(2, month);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                incomeList.add(new Income(
                        rs.getString("date"),
                        rs.getString("service"),
                        rs.getDouble("amount"),
                        rs.getString("customer_name"),
                        rs.getString("recipient"),
                        "-"
                ));
            }
        }
        return incomeList;
    }


    private List<Expenses> getExpenseDataByMonth(int year, int month) throws SQLException {
        List<Expenses> expenses = new ArrayList<>();
        String sql = "SELECT * FROM expenses WHERE YEAR(date) = ? AND MONTH(date) = ? ORDER BY date DESC";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, year);
            stmt.setInt(2, month);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                expenses.add(new Expenses(
                        rs.getDate("date"),
                        rs.getString("description"),
                        rs.getString("category"),
                        rs.getDouble("amount")
                ));
            }
        }
        return expenses;
    }

}