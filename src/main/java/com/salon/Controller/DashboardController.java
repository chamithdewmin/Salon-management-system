package com.salon.Controller;

import com.salon.Model.*;
import com.salon.Model.Customers.Customer;
import com.salon.Model.Customers.CustomerDAO;
import com.salon.Model.Finance.FinanceRecord;
import com.salon.Model.Finance.FinanceRecordDAO;
import com.salon.Model.Services.Service;
import com.salon.Model.Services.ServiceDAO;
import com.salon.Utils.CustomAlert;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class DashboardController {

    @FXML private PieChart PieChart;
    @FXML private ComboBox<String> customerNameComboBox;
    @FXML private ComboBox<String> serviceComboBox;
    @FXML private ListView<String> selectedServiceList;
    @FXML private TextField totalAmountField, cashGivenField, changeField;
    @FXML private Button refreshButton, addPaymentButton, expenceBtn;
    @FXML private TextField desTxt, amountTxt;
    @FXML private DatePicker expenceDate;

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
            customerNameComboBox.setEditable(false); // Make ComboBox non-editable
            selectedServiceList.setItems(selectedServices);
        } else {
            CustomAlert.showAlert("Database Error", "Unable to connect to the database.");
        }
    }

    private void loadServiceOptions() {
        try {
            ServiceDAO serviceDAO = new ServiceDAO(conn);
            allServices = serviceDAO.getAllServices();
            serviceComboBox.setItems(FXCollections.observableArrayList(
                    allServices.stream().map(Service::getName).collect(Collectors.toList())));
        } catch (SQLException e) {
            CustomAlert.showAlert("Service Load Error", e.getMessage());
        }
    }

    private void loadCustomerNames() {
        try {
            CustomerDAO customerDAO = new CustomerDAO(conn);
            List<Customer> customers = customerDAO.getAllCustomers();
            allCustomerNames.setAll(customers.stream().map(Customer::getFullName).collect(Collectors.toList()));
            customerNameComboBox.setItems(allCustomerNames);
        } catch (SQLException e) {
            CustomAlert.showAlert("Customer Load Error", e.getMessage());
        }
    }

    @FXML
    private void handleAddService() {
        String selected = serviceComboBox.getValue();
        if (selected == null || selected.isEmpty()) {
            CustomAlert.showAlert("Selection Error", "Please select a service.");
            return;
        }
        if (!selectedServices.contains(selected)) {
            selectedServices.add(selected);
            calculateTotalAmount();
        } else {
            CustomAlert.showAlert("Duplicate Service", "Service already added.");
        }
    }

    private void calculateTotalAmount() {
        double total = selectedServices.stream()
                .mapToDouble(serviceName -> allServices.stream()
                        .filter(s -> s.getName().equals(serviceName))
                        .mapToDouble(Service::getPrice)
                        .findFirst().orElse(0.0)).sum();
        totalAmountField.setText(String.format("%.2f", total));
    }

    @FXML
    private void calculateBalance() {
        try {
            double total = Double.parseDouble(totalAmountField.getText().trim());
            double cash = Double.parseDouble(cashGivenField.getText().trim());
            changeField.setText(cash >= total ? String.format("%.2f", cash - total) : "");
        } catch (NumberFormatException e) {
            changeField.setText("");
        }
    }

    @FXML
    private void handleAddPayment() {
        if (selectedServices.isEmpty()) {
            CustomAlert.showAlert("Service Error", "Please add at least one service.");
            return;
        }

        String customerName = customerNameComboBox.getValue();
        if (customerName == null || customerName.trim().isEmpty()) {
            CustomAlert.showAlert("Input Error", "Please select a customer.");
            return;
        }

        try {
            double totalAmount = Double.parseDouble(totalAmountField.getText().trim());
            String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            String serviceDescription = String.join(", ", selectedServices);

            FinanceRecord record = new FinanceRecord(currentDate, serviceDescription, totalAmount, customerName);
            FinanceRecordDAO financeDAO = new FinanceRecordDAO(conn);

            if (financeDAO.insertIncome(record)) {
                CustomAlert.showSuccess("Payment recorded successfully.");
                clearPaymentFields();
            } else {
                CustomAlert.showAlert("Database Error", "Could not record payment.");
            }
        } catch (NumberFormatException e) {
            CustomAlert.showAlert("Input Error", "Total must be a number.");
        }
    }

    private void clearPaymentFields() {
        customerNameComboBox.setValue(null);
        selectedServices.clear();
        totalAmountField.clear();
        cashGivenField.clear();
        changeField.clear();
    }

    @FXML
    private void handleAddExpense() {
        String description = desTxt.getText().trim();
        String amountStr = amountTxt.getText().trim();
        LocalDate date = expenceDate.getValue();

        if (description.isEmpty() || amountStr.isEmpty() || date == null) {
            CustomAlert.showAlert("Input Error", "Please fill in all expense fields.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            FinanceRecord record = new FinanceRecord(date.toString(), description, amount);
            FinanceRecordDAO financeDAO = new FinanceRecordDAO(conn);

            if (financeDAO.insertExpense(record)) {
                CustomAlert.showSuccess("Expense recorded successfully.");
                desTxt.clear();
                amountTxt.clear();
                expenceDate.setValue(null);
            } else {
                CustomAlert.showAlert("Database Error", "Could not record expense.");
            }
        } catch (NumberFormatException e) {
            CustomAlert.showAlert("Input Error", "Amount must be a number.");
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
            desTxt.clear();
            amountTxt.clear();
            expenceDate.setValue(null);
            loadCustomerNames();
            loadServiceOptions();
        } else {
            CustomAlert.showAlert("Database Error", "No database connection.");
        }
    }

    private void updatePieChart(FinanceRecord record) {
        PieChart.getData().clear();
        PieChart.getData().add(new PieChart.Data("Income", record.getIncome()));
        PieChart.getData().add(new PieChart.Data("Expenses", record.getExpenses()));
        PieChart.getData().add(new PieChart.Data("Saved", record.getSaved()));
    }
}