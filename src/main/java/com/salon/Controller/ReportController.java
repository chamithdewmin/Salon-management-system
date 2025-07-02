package com.salon.Controller;

import com.salon.Model.DatabaseConnection;
import com.salon.Model.Finance.FinanceRecord;
import com.salon.Model.Finance.FinanceRecordDAO;
import com.salon.Utils.CustomAlert;
import javafx.beans.property.SimpleObjectProperty;
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

public class ReportController {

    @FXML private Label totalIncomeLabel;
    @FXML private Label totalExpenseLabel;
    @FXML private Label totalSavingLabel;

    @FXML private ComboBox<String> typeComboBox;
    @FXML private ComboBox<String> filterComboBox;
    @FXML private DatePicker filterDatePicker;

    @FXML private TableView<FinanceRecord> reportTable;
    @FXML private TableColumn<FinanceRecord, String> dateColumn;
    @FXML private TableColumn<FinanceRecord, String> customerColumn;
    @FXML private TableColumn<FinanceRecord, String> serviceColumn;
    @FXML private TableColumn<FinanceRecord, String> staffColumn;
    @FXML private TableColumn<FinanceRecord, String> visitColumn;
    @FXML private TableColumn<FinanceRecord, Double> amountColumn;

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

        setupTableColumns();

        typeComboBox.setItems(FXCollections.observableArrayList("Income", "Expense", "Loyalty Customers"));
        filterComboBox.setItems(FXCollections.observableArrayList("Daily", "Monthly"));

        typeComboBox.setOnAction(e -> reloadData());
        filterComboBox.setOnAction(e -> reloadData());
        filterDatePicker.setOnAction(e -> reloadData());

        refreshUI();
        loadSummary();
    }

    private void setupTableColumns() {
        dateColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDate()));
        customerColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCustomerName()));
        serviceColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDescription()));
        staffColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStaffName()));
        visitColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getVisitCount()));
        amountColumn.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getAmount()));
    }

    private void reloadData() {
        String selectedType = typeComboBox.getValue();
        String filterType = filterComboBox.getValue();
        LocalDate selectedDate = filterDatePicker.getValue();

        if (selectedType == null || (!"Loyalty Customers".equals(selectedType) && (filterType == null || selectedDate == null))) {
            reportTable.getItems().clear();
            return;
        }

        if ("Loyalty Customers".equals(selectedType)) {
            loadLoyaltyCustomers();
        } else {
            loadFinanceData(selectedType);
        }
    }

    private void loadFinanceData(String type) {
        ObservableList<FinanceRecord> data = FXCollections.observableArrayList();

        boolean isIncome = "Income".equalsIgnoreCase(type);
        String sql;

        if ("Monthly".equals(filterComboBox.getValue())) {
            sql = isIncome
                    ? "SELECT date, customerName, description, staffName, amount FROM income WHERE MONTH(date) = ? AND YEAR(date) = ?"
                    : "SELECT date, description, amount FROM expense WHERE MONTH(date) = ? AND YEAR(date) = ?";
        } else {
            sql = isIncome
                    ? "SELECT date, customerName, description, staffName, amount FROM income WHERE date = ?"
                    : "SELECT date, description, amount FROM expense WHERE date = ?";
        }

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
                String date = rs.getString("date");
                String desc = rs.getString("description");
                double amt = rs.getDouble("amount");

                if (isIncome) {
                    String customer = rs.getString("customerName");
                    String staff = rs.getString("staffName");
                    data.add(new FinanceRecord(date, desc, amt, customer, staff, "-"));
                } else {
                    data.add(new FinanceRecord(date, desc, amt, "-", "-", "-"));
                }
            }

            reportTable.setItems(data);
        } catch (SQLException e) {
            e.printStackTrace();
            CustomAlert.showAlert("SQL Error", "Failed to load " + type + " data.");
        }
    }

    private void loadLoyaltyCustomers() {
        ObservableList<FinanceRecord> data = FXCollections.observableArrayList();
        String sql = "SELECT customerName, COUNT(DISTINCT date) AS visits, " +
                "SUM(LENGTH(description) - LENGTH(REPLACE(description, ',', '')) + 1) AS serviceCount, " +
                "SUM(amount) AS total, staffName " +
                "FROM income WHERE description IS NOT NULL GROUP BY customerName, staffName";

        try (PreparedStatement stmt = financeDAO.conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String customerName = rs.getString("customerName");
                String staffName = rs.getString("staffName");
                String visits = String.valueOf(rs.getInt("visits"));
                String serviceCount = String.valueOf(rs.getInt("serviceCount"));
                double total = rs.getDouble("total");

                data.add(new FinanceRecord("-", serviceCount, total, customerName, staffName, visits));
            }
            reportTable.setItems(data);
        } catch (SQLException e) {
            e.printStackTrace();
            CustomAlert.showAlert("SQL Error", "Failed to load loyalty customer data.");
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
