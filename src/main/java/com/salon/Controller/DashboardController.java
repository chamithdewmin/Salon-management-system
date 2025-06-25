package com.salon.Controller;

import com.salon.Model.*;
import com.salon.Model.Customers.Customer;
import com.salon.Model.Customers.CustomerDAO;
import com.salon.Model.Services.Service;
import com.salon.Model.Services.ServiceDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DashboardController {

    @FXML private Label income_value;
    @FXML private Label expenses_value;
    @FXML private Label saved_value;
    @FXML private PieChart PieChart;

    @FXML private ComboBox<String> customerNameComboBox;
    @FXML private ComboBox<String> serviceComboBox;
    @FXML private ListView<String> selectedServiceList;
    @FXML private TextField totalAmountField;
    @FXML private TextField cashGivenField;
    @FXML private TextField changeField;

    @FXML private Button addServiceButton;
    @FXML private Button refreshButton;
    @FXML private Button addPaymentButton;

    private Connection conn;
    private ObservableList<String> allCustomerNames = FXCollections.observableArrayList();
    private List<Service> allServices;
    private ObservableList<String> selectedServices = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        conn = DatabaseConnection.connect();
        if (conn != null) {
            loadServiceOptions();
            loadCustomerNames();
            setupCustomerAutoComplete();
            updateFinanceBlocks();
        } else {
            showAlert("Database Connection Failed", "Unable to connect to the database.");
        }

        selectedServiceList.setItems(selectedServices);
    }

    private void loadServiceOptions() {
        try {
            ServiceDAO serviceDAO = new ServiceDAO(conn);
            allServices = serviceDAO.getAllServices();
            serviceComboBox.getItems().clear();
            for (Service service : allServices) {
                serviceComboBox.getItems().add(service.getName());
            }
        } catch (SQLException e) {
            showAlert("Error loading services", e.getMessage());
        }
    }

    private void loadCustomerNames() {
        try {
            CustomerDAO customerDAO = new CustomerDAO(conn);
            List<Customer> customers = customerDAO.getAllCustomers();
            allCustomerNames.clear();
            for (Customer customer : customers) {
                allCustomerNames.add(customer.getFullName());
            }
            customerNameComboBox.setItems(allCustomerNames);
        } catch (SQLException e) {
            showAlert("Error loading customers", e.getMessage());
        }
    }

    private void setupCustomerAutoComplete() {
        customerNameComboBox.setEditable(true);
        customerNameComboBox.getEditor().textProperty().addListener((obs, oldText, newText) -> {
            if (!customerNameComboBox.isShowing()) {
                customerNameComboBox.show();
            }

            ObservableList<String> filtered = FXCollections.observableArrayList(
                    allCustomerNames.stream()
                            .filter(name -> name.toLowerCase().startsWith(newText.toLowerCase()))
                            .collect(Collectors.toList())
            );

            customerNameComboBox.setItems(filtered);
            customerNameComboBox.getEditor().positionCaret(newText.length());
        });
    }

    @FXML
    private void handleAddService() {
        String selected = serviceComboBox.getValue();
        if (selected != null && !selectedServices.contains(selected)) {
            selectedServices.add(selected);
            calculateTotalAmount();
        }
    }

    private void calculateTotalAmount() {
        double total = 0.0;

        for (String selected : selectedServices) {
            for (Service service : allServices) {
                if (service.getName().equals(selected)) {
                    total += service.getPrice();
                    break;
                }
            }
        }

        totalAmountField.setText(String.format("%.2f", total));
    }


    @FXML
    private void calculateBalance() {
        try {
            double total = Double.parseDouble(totalAmountField.getText().trim());
            double cash = Double.parseDouble(cashGivenField.getText().trim());
            double change = cash - total;
            changeField.setText(String.format("%.2f", change));
        } catch (NumberFormatException e) {
            changeField.setText("");
        }
    }

    @FXML
    private void handleAddPayment() {
        String customerName = customerNameComboBox.getValue();
        String services = String.join(", ", selectedServices);
        String totalStr = totalAmountField.getText().trim();

        if (customerName == null || customerName.isEmpty() || services.isEmpty() || totalStr.isEmpty()) {
            showAlert("Input Error", "Please ensure all fields are filled before adding payment.");
            return;
        }

        try {
            double totalAmount = Double.parseDouble(totalStr);
            String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            String description = customerName + " - " + services;

            FinanceRecord record = new FinanceRecord(currentDate, description, totalAmount);
            FinanceRecordDAO financeDAO = new FinanceRecordDAO(conn);

            boolean success = financeDAO.insertIncome(record);
            if (success) {
                showAlert("Success", "Payment recorded successfully.");
                handleRefresh();
            } else {
                showAlert("Failure", "Could not record payment.");
            }
        } catch (Exception e) {
            showAlert("Database Error", "Failed to add payment: " + e.getMessage());
        }
    }

    @FXML
    private void handleRefresh() {
        if (conn != null) {
            customerNameComboBox.getItems().clear();
            serviceComboBox.getItems().clear();
            selectedServices.clear();
            totalAmountField.clear();
            cashGivenField.clear();
            changeField.clear();
            allCustomerNames.clear();

            try {
                loadCustomerNames();
                loadServiceOptions();
                updateFinanceBlocks();
            } catch (Exception e) {
                showAlert("Refresh Failed", e.getMessage());
            }
        } else {
            showAlert("Database Error", "Connection is not available.");
        }
    }

    private void updateFinanceBlocks() {
        try {
            FinanceRecordDAO financeDAO = new FinanceRecordDAO(conn);
            FinanceRecord record = financeDAO.getFinanceSummary();

            income_value.setText(String.format("%.2f", record.getIncome()));
            expenses_value.setText(String.format("%.2f", record.getExpenses()));
            saved_value.setText(String.format("%.2f", record.getSaved()));

            updatePieChart(record);
        } catch (SQLException e) {
            showAlert("Finance Load Error", e.getMessage());
        }
    }

    private void updatePieChart(FinanceRecord record) {
        PieChart.getData().clear();
        PieChart.getData().add(new PieChart.Data("Income", record.getIncome()));
        PieChart.getData().add(new PieChart.Data("Expenses", record.getExpenses()));
        PieChart.getData().add(new PieChart.Data("Saved", record.getSaved()));
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
