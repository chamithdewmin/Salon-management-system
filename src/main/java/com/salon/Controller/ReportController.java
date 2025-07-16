package com.salon.Controller;

import com.salon.Model.DatabaseConnection;
import com.salon.Model.Income.Income;
import com.salon.Model.Income.IncomeDAO;
import com.salon.Utils.CustomAlert;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;
import java.time.LocalDate;

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
        if (selectedType == null) return;

        loadFinanceData(selectedType);
    }

    private void loadFinanceData(String type) {
        ObservableList<Income> data = FXCollections.observableArrayList();
        LocalDate selectedDate = filterDatePicker.getValue();

        try {
            if ("Income".equalsIgnoreCase(type)) {
                String sql = (selectedDate == null)
                        ? "SELECT date, customer_name, service, recipient, amount FROM income"
                        : "SELECT date, customer_name, service, recipient, amount FROM income WHERE date = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    if (selectedDate != null) stmt.setDate(1, Date.valueOf(selectedDate));
                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        data.add(new Income(
                                rs.getString("date"),
                                rs.getString("service"),          // used as description
                                rs.getDouble("amount"),
                                rs.getString("customer_name"),
                                rs.getString("recipient"),        // used as staff
                                "-"                               // placeholder for visit count
                        ));
                    }
                }
            } else if ("Expense".equalsIgnoreCase(type)) {
                String sql = (selectedDate == null)
                        ? "SELECT date, description, category, amount FROM expenses"
                        : "SELECT date, description, category, amount FROM expenses WHERE date = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    if (selectedDate != null) stmt.setDate(1, Date.valueOf(selectedDate));
                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        data.add(new Income(
                                rs.getString("date"),
                                rs.getString("description") + " (" + rs.getString("category") + ")",
                                rs.getDouble("amount"),
                                "-", "-", "-"
                        ));
                    }
                }
            }

            reportTable.setItems(data);

        } catch (SQLException e) {
            e.printStackTrace();
            CustomAlert.showAlert(Alert.AlertType.ERROR, "SQL Error", "Failed to load " + type + " data.");
        }
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
}
