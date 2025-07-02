package com.salon.Controller;

import com.salon.Model.*;
import com.salon.Model.Customers.Customer;
import com.salon.Model.Customers.CustomerDAO;
import com.salon.Model.Finance.FinanceRecord;
import com.salon.Model.Finance.FinanceRecordDAO;
import com.salon.Model.Services.Service;
import com.salon.Model.Services.ServiceDAO;
import com.salon.Model.PaymentSummary;
import com.salon.Utils.CustomAlert;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class DashboardController {

    public Button addServiceButton;
    @FXML private PieChart pieChart;
    @FXML private ComboBox<String> customerNameComboBox;
    @FXML private ComboBox<String> serviceComboBox;
    @FXML private ComboBox<String> staffComboBox;
    @FXML private ListView<String> selectedServiceList;
    @FXML private TextField totalAmountField, cashGivenField, changeField;
    @FXML private Button refreshButton, addPaymentButton, expenceBtn;
    @FXML private TextField desTxt, amountTxt;
    @FXML private DatePicker expenceDate;

    @FXML private TableView<PaymentSummary> paymentSummaryTable;
    @FXML private TableColumn<PaymentSummary, String> colCustomerName;
    @FXML private TableColumn<PaymentSummary, String> colService;
    @FXML private TableColumn<PaymentSummary, String> colStaff;
    @FXML private TableColumn<PaymentSummary, Double> colAmount;

    private Connection conn;
    private ObservableList<String> allCustomerNames = FXCollections.observableArrayList();
    private ObservableList<String> allStaffNames = FXCollections.observableArrayList();
    private List<Service> allServices;
    private ObservableList<String> selectedServices = FXCollections.observableArrayList();
    private ObservableList<PaymentSummary> summaryData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        conn = DatabaseConnection.connect();
        if (conn != null) {
            loadServiceOptions();
            loadCustomerNames();
            loadStaffNames();
            customerNameComboBox.setEditable(true);
            customerNameComboBox.setItems(allCustomerNames);
            customerNameComboBox.setOnKeyReleased(this::handleCustomerAutoComplete);
            staffComboBox.setItems(allStaffNames);
            if (selectedServiceList != null) {
                selectedServiceList.setItems(selectedServices);
            }
            setupTable();
            updatePieChartFromDatabase();
        } else {
            CustomAlert.showAlert("Database Error", "Unable to connect to the database.");
        }
    }

    private void setupTable() {
        colCustomerName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colService.setCellValueFactory(new PropertyValueFactory<>("service"));
        colStaff.setCellValueFactory(new PropertyValueFactory<>("staff"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        paymentSummaryTable.setItems(summaryData);
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
        } catch (SQLException e) {
            CustomAlert.showAlert("Customer Load Error", e.getMessage());
        }
    }

    private void loadStaffNames() {
        try {
            com.salon.Model.Staff.StaffDAO staffDAO = new com.salon.Model.Staff.StaffDAO();
            List<com.salon.Model.Staff.Staff> staffList = staffDAO.getAllStaff();
            allStaffNames.setAll(staffList.stream().map(com.salon.Model.Staff.Staff::getName).collect(Collectors.toList()));
        } catch (Exception e) {
            CustomAlert.showAlert("Staff Load Error", e.getMessage());
        }
    }

    private void handleCustomerAutoComplete(KeyEvent event) {
        String input = customerNameComboBox.getEditor().getText().toLowerCase();
        try {
            CustomerDAO customerDAO = new CustomerDAO(conn);
            List<Customer> filtered = customerDAO.searchCustomersByName(input);
            ObservableList<String> matches = FXCollections.observableArrayList(
                    filtered.stream().map(Customer::getFullName).collect(Collectors.toList()));
            customerNameComboBox.setItems(matches);
            customerNameComboBox.show();
        } catch (SQLException e) {
            CustomAlert.showAlert("Auto-complete Error", e.getMessage());
        }
    }

    @FXML
    private void handleAddService() {
        String service = serviceComboBox.getValue();
        String customer = customerNameComboBox.getValue();
        String staff = staffComboBox.getValue();

        if (customer == null || customer.trim().isEmpty()) {
            CustomAlert.showAlert("Input Error", "Please select a customer.");
            return;
        }

        if (service == null || service.trim().isEmpty()) {
            CustomAlert.showAlert("Input Error", "Please select a service.");
            return;
        }

        if (staff == null || staff.trim().isEmpty()) {
            CustomAlert.showAlert("Input Error", "Please select a staff member.");
            return;
        }

        double amount = allServices.stream()
                .filter(s -> s.getName().equals(service))
                .mapToDouble(Service::getPrice)
                .findFirst().orElse(0.0);

        summaryData.add(new PaymentSummary(customer, service, staff, amount));
        calculateTotalAmount();
    }

    private void calculateTotalAmount() {
        double total = summaryData.stream().mapToDouble(PaymentSummary::getAmount).sum();
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
        if (summaryData.isEmpty()) {
            CustomAlert.showAlert("Service Error", "Please add at least one service.");
            return;
        }

        try {
            double totalAmount = Double.parseDouble(totalAmountField.getText().trim());
            String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            String serviceDescription = summaryData.stream()
                    .map(PaymentSummary::getService)
                    .collect(Collectors.joining(", "));
            String customerName = summaryData.get(0).getCustomerName();
            String staffName = summaryData.get(0).getStaff(); // Get staff name from summary

            FinanceRecord record = new FinanceRecord(currentDate, serviceDescription, totalAmount, customerName, staffName);

            FinanceRecordDAO financeDAO = new FinanceRecordDAO(conn);
            if (financeDAO.insertIncome(record)) {
                CustomAlert.showSuccess("Payment recorded successfully.");
                clearPaymentFields();
                updatePieChartFromDatabase();
            } else {
                CustomAlert.showAlert("Database Error", "Could not record payment.");
            }
        } catch (NumberFormatException e) {
            CustomAlert.showAlert("Input Error", "Total must be a number.");
        }
    }

    private void clearPaymentFields() {
        customerNameComboBox.setValue(null);
        serviceComboBox.setValue(null);
        staffComboBox.setValue(null);
        summaryData.clear();
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
                updatePieChartFromDatabase();
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
            staffComboBox.getItems().clear();
            summaryData.clear();
            totalAmountField.clear();
            cashGivenField.clear();
            changeField.clear();
            desTxt.clear();
            amountTxt.clear();
            expenceDate.setValue(null);
            loadCustomerNames();
            loadServiceOptions();
            loadStaffNames();
            updatePieChartFromDatabase();
        } else {
            CustomAlert.showAlert("Database Error", "No database connection.");
        }
    }

    private void updatePieChart(FinanceRecord record) {
        pieChart.getData().clear();
        pieChart.getData().add(new PieChart.Data("Income", record.getIncome()));
        pieChart.getData().add(new PieChart.Data("Expenses", record.getExpenses()));
        pieChart.getData().add(new PieChart.Data("Saved", record.getSaved()));
    }

    private void updatePieChartFromDatabase() {
        try {
            FinanceRecordDAO financeDAO = new FinanceRecordDAO(conn);
            FinanceRecord summary = financeDAO.getFinanceSummary();
            updatePieChart(summary);
        } catch (SQLException e) {
            CustomAlert.showAlert("Summary Error", "Could not load finance summary.");
        }
    }
}
