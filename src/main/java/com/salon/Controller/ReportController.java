package com.salon.Controller;

import com.salon.Model.Appointment.Appointment;
import com.salon.Model.Appointment.AppointmentDAO;
import com.salon.Model.DatabaseConnection;
import com.salon.Model.Finance.FinanceRecord;
import com.salon.Model.Finance.FinanceRecordDAO;
import com.salon.Utils.CustomAlert;
import com.salon.Utils.ReportPDFGenerator;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ReportController {

    @FXML private Label totalIncomeLabel;
    @FXML private Label totalExpenseLabel;
    @FXML private Label totalSavingLabel;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private ComboBox<String> filterComboBox;
    @FXML private DatePicker filterDatePicker;
    @FXML private TableView reportTable;
    @FXML private TableColumn<?, ?> dateColumn;
    @FXML private TableColumn<?, ?> descriptionColumn;
    @FXML private TableColumn<?, ?> amountColumn;
    @FXML private Button downloadButton;
    @FXML public Button refreshButton;

    private FinanceRecordDAO financeDAO;

    @FXML
    public void initialize() {
        Connection conn = DatabaseConnection.connect();
        if (conn == null) {
            CustomAlert.showAlert("Database Error", "Failed to connect to the database.");
            return;
        }

        financeDAO = new FinanceRecordDAO(conn);

        typeComboBox.setItems(FXCollections.observableArrayList("Income", "Expense", "Appointment", "Loyalty Customers"));
        filterComboBox.setItems(FXCollections.observableArrayList("Daily", "Monthly"));

        typeComboBox.setOnAction(e -> reloadData());
        filterComboBox.setOnAction(e -> reloadData());
        filterDatePicker.setOnAction(e -> reloadData());

        refreshUI();
        loadSummary();

        downloadButton.setOnAction(e -> {
            String filterType = filterComboBox.getValue();
            LocalDate selectedDate = filterDatePicker.getValue();

            if (!"Monthly".equals(filterType) || selectedDate == null) {
                CustomAlert.showAlert("Missing Input", "Please select 'Monthly' and pick a valid month.");
                return;
            }

            try (Connection exportConn = DatabaseConnection.connect()) {
                ReportPDFGenerator.generateSalesReport(selectedDate, exportConn);
                CustomAlert.showSuccess("Monthly report exported successfully!");
            } catch (Exception ex) {
                ex.printStackTrace();
                CustomAlert.showAlert("Export Error", "Failed to export PDF report.");
            }
        });
    }

    private void reloadData() {
        String selectedType = typeComboBox.getValue();
        String filterType = filterComboBox.getValue();
        LocalDate selectedDate = filterDatePicker.getValue();

        if (selectedType == null || (!"Loyalty Customers".equals(selectedType) && (filterType == null || selectedDate == null))) {
            reportTable.getItems().clear();
            return;
        }

        switch (selectedType) {
            case "Appointment":
                loadAppointments();
                break;
            case "Loyalty Customers":
                loadLoyaltyCustomers();
                break;
            default:
                loadFinanceData(selectedType);
                break;
        }
    }

    private void loadSummary() {
        try {
            FinanceRecord summary = financeDAO.getFinanceSummary();
            totalIncomeLabel.setText("Rs. " + String.format("%.2f", summary.getIncome()));
            totalExpenseLabel.setText("Rs. " + String.format("%.2f", summary.getExpenses()));
            totalSavingLabel.setText("Rs. " + String.format("%.2f", summary.getSaved()));
        } catch (SQLException e) {
            e.printStackTrace();
            CustomAlert.showAlert("Load Error", "Failed to load summary data.");
        }
    }

    private void loadFinanceData(String type) {
        ObservableList<FinanceRecord> data = FXCollections.observableArrayList();

        dateColumn.setText("Date");
        descriptionColumn.setText("Customer / Service");
        amountColumn.setText("Amount (Rs)");

        ((TableColumn<FinanceRecord, String>) dateColumn).setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDate()));
        ((TableColumn<FinanceRecord, String>) descriptionColumn).setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCustomerName() + " / " + cell.getValue().getDescription()));
        ((TableColumn<FinanceRecord, Double>) amountColumn).setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getAmount()));

        String sql = "Monthly".equals(filterComboBox.getValue()) ?
                "SELECT date, description, customerName, amount FROM " + type.toLowerCase() + " WHERE MONTH(date) = ? AND YEAR(date) = ?" :
                "SELECT date, description, customerName, amount FROM " + type.toLowerCase() + " WHERE date = ?";

        try (PreparedStatement stmt = financeDAO.conn.prepareStatement(sql)) {
            LocalDate selectedDate = filterDatePicker.getValue();
            if ("Monthly".equals(filterComboBox.getValue())) {
                stmt.setInt(1, selectedDate.getMonthValue());
                stmt.setInt(2, selectedDate.getYear());
            } else {
                stmt.setDate(1, java.sql.Date.valueOf(selectedDate));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                data.add(new FinanceRecord(
                        rs.getString("date"),
                        rs.getString("description"),
                        rs.getDouble("amount"),
                        rs.getString("customerName")
                ));
            }
            reportTable.setItems(data);
        } catch (SQLException e) {
            e.printStackTrace();
            CustomAlert.showAlert("SQL Error", "Failed to load " + type + " data.");
        }
    }

    private void loadAppointments() {
        ObservableList<Appointment> data = FXCollections.observableArrayList();

        dateColumn.setText("Date");
        descriptionColumn.setText("Time");
        amountColumn.setText("Customer / Service");

        ((TableColumn<Appointment, String>) dateColumn).setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDate().toString()));
        ((TableColumn<Appointment, String>) descriptionColumn).setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTime().toString()));
        ((TableColumn<Appointment, String>) amountColumn).setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getClientName() + " - " + cell.getValue().getServiceType()));

        try {
            data.addAll(AppointmentDAO.getAllAppointments());
            reportTable.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
            CustomAlert.showAlert("Error", "Failed to load appointment data.");
        }
    }

    private void loadLoyaltyCustomers() {
        ObservableList<String[]> data = FXCollections.observableArrayList();

        dateColumn.setText("Customer Name");
        descriptionColumn.setText("Month");
        amountColumn.setText("Visits / Services / Amount");

        ((TableColumn<String[], String>) dateColumn).setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue()[0]));
        ((TableColumn<String[], String>) descriptionColumn).setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue()[1]));
        ((TableColumn<String[], String>) amountColumn).setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue()[2]));

        String sql = "SELECT customerName AS customer_name, " +
                "DATE_FORMAT(date, '%Y-%m') AS month, " +
                "COUNT(DISTINCT date) AS visits, " +
                "SUM(CHAR_LENGTH(description) - CHAR_LENGTH(REPLACE(description, ',', '')) + 1) AS services, " +
                "SUM(amount) AS total " +
                "FROM income " +
                "GROUP BY customer_name, month ORDER BY total DESC";

        try (PreparedStatement stmt = financeDAO.conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String name = rs.getString("customer_name");
                String month = rs.getString("month");
                int visits = rs.getInt("visits");
                int services = rs.getInt("services");
                double total = rs.getDouble("total");

                String[] row = { name, month, visits + " / " + services + " / Rs. " + String.format("%.2f", total) };
                data.add(row);
            }
            reportTable.setItems(data);
        } catch (SQLException e) {
            e.printStackTrace();
            CustomAlert.showAlert("SQL Error", "Failed to load loyalty customer data.");
        }
    }

    @FXML
    private void handleRefresh() {
        refreshUI();
        loadSummary();
    }

    private void refreshUI() {
        typeComboBox.getSelectionModel().clearSelection();
        filterComboBox.getSelectionModel().clearSelection();
        filterDatePicker.setValue(null);
        reportTable.getItems().clear();
    }
}