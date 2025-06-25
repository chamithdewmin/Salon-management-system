package com.salon.Controller;

import com.salon.Model.*;
import com.salon.Model.Customers.Customer;
import com.salon.Model.Customers.CustomerDAO;
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
    @FXML private Button expenceBtn;

    @FXML private TextField desTxt;
    @FXML private TextField amountTxt;
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
            setupCustomerAutoComplete();
            updateFinanceBlocks();
        } else {
            CustomAlert.showAlert("Database Error", "Unable to connect to the database.");
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
            CustomAlert.showAlert("Service Load Error", e.getMessage());
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
            CustomAlert.showAlert("Customer Load Error", e.getMessage());
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
                            .filter(name -> name.toLowerCase().contains(newText.toLowerCase()))
                            .collect(Collectors.toList())
            );
            customerNameComboBox.setItems(filtered);
            customerNameComboBox.getEditor().positionCaret(newText.length());
        });
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
        String totalStr = totalAmountField.getText().trim();
        String cashStr = cashGivenField.getText().trim();

        try {
            double total = Double.parseDouble(totalStr);
            double cash = Double.parseDouble(cashStr);

            if (cash >= total) {
                double change = cash - total;
                changeField.setText(String.format("%.2f", change));
            } else {
                // If cash is less than total, just clear the change field silently
                changeField.setText("");
            }

        } catch (NumberFormatException e) {
            // If input is invalid, just clear the change field silently
            changeField.setText("");
        }
    }


    @FXML
    private void handleAddPayment() {
        if (selectedServices.isEmpty()) {
            CustomAlert.showAlert("Service Error", "Please add at least one service.");
            return;
        }

        String totalStr = totalAmountField.getText().trim();
        if (totalStr.isEmpty()) {
            CustomAlert.showAlert("Input Error", "Total amount is missing.");
            return;
        }

        try {
            double totalAmount = Double.parseDouble(totalStr);
            String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            String description = String.join(", ", selectedServices);

            FinanceRecord record = new FinanceRecord(currentDate, description, totalAmount);
            FinanceRecordDAO financeDAO = new FinanceRecordDAO(conn);

            if (financeDAO.insertIncome(record)) {
                CustomAlert.showSuccess("Payment recorded successfully.");
                handleRefresh();
            } else {
                CustomAlert.showAlert("Database Error", "Could not record payment.");
            }
        } catch (NumberFormatException e) {
            CustomAlert.showAlert("Input Error", "Total must be a number.");
        }
    }

    @FXML
    private void handleAddExpense() {
        String description = desTxt.getText().trim();
        String amountStr = amountTxt.getText().trim();
        LocalDate date = expenceDate.getValue();

        if (description.isEmpty() || amountStr.isEmpty() || date == null) {
            CustomAlert.showAlert("Input Error", "Please fill in the date, description, and amount fields.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            String formattedDate = date.toString();

            FinanceRecord record = new FinanceRecord(formattedDate, description, amount);
            FinanceRecordDAO financeDAO = new FinanceRecordDAO(conn);

            if (financeDAO.insertExpense(record)) {
                CustomAlert.showSuccess("Expense recorded successfully.");
                desTxt.clear();
                amountTxt.clear();
                expenceDate.setValue(null);
                updateFinanceBlocks();
            } else {
                CustomAlert.showAlert("Database Error", "Could not record expense.");
            }
        } catch (NumberFormatException e) {
            CustomAlert.showAlert("Input Error", "Amount must be a valid number.");
        }
    }

    @FXML
    private void handleRefresh() {
        if (conn != null) {
            try {
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
                updateFinanceBlocks();
            } catch (Exception e) {
                CustomAlert.showAlert("Refresh Error", e.getMessage());
            }
        } else {
            CustomAlert.showAlert("Database Error", "No database connection.");
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
            CustomAlert.showAlert("Finance Load Error", e.getMessage());
        }
    }

    private void updatePieChart(FinanceRecord record) {
        PieChart.getData().clear();
        PieChart.getData().add(new PieChart.Data("Income", record.getIncome()));
        PieChart.getData().add(new PieChart.Data("Expenses", record.getExpenses()));
        PieChart.getData().add(new PieChart.Data("Saved", record.getSaved()));
    }
}
