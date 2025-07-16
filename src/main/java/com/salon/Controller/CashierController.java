package com.salon.Controller;

import com.salon.Model.*;
import com.salon.Model.Customers.LoyaltyCustomer;
import com.salon.Model.Customers.LoyaltyCustomerDAO;
import com.salon.Model.Income.Income;
import com.salon.Model.Income.IncomeDAO;
import com.salon.Model.Services.Service;
import com.salon.Model.Services.ServiceDAO;
import com.salon.Utils.CustomAlert;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class CashierController {

    @FXML private ComboBox<String> customerNameComboBox, serviceComboBox, staffComboBox;
    @FXML private TextField phoneNumberTxt, totalAmountField, cashGivenField, changeField;
    @FXML private Button addServiceButton, addPaymentButton, refreshButton;
    @FXML private TableView<PaymentSummary> paymentSummaryTable;
    @FXML private TableColumn<PaymentSummary, String> colCustomerName, colService, colStaff, colDescription;
    @FXML private TableColumn<PaymentSummary, Double> colAmount;

    private Connection conn;
    private ObservableList<String> allCustomerNames = FXCollections.observableArrayList();
    private ObservableList<String> allStaffNames = FXCollections.observableArrayList();
    private ObservableList<PaymentSummary> summaryData = FXCollections.observableArrayList();
    private List<Service> allServices;

    private LoyaltyCustomerDAO loyaltyCustomerDAO;

    @FXML
    public void initialize() {
        conn = DatabaseConnection.connect();
        if (conn != null) {
            loyaltyCustomerDAO = new LoyaltyCustomerDAO(conn);
            loadCustomerNames();
            loadStaffNames();
            loadServiceOptions();

            customerNameComboBox.setEditable(true);
            customerNameComboBox.setItems(allCustomerNames);
            customerNameComboBox.setOnKeyReleased(this::handleCustomerAutoComplete);
            staffComboBox.setItems(allStaffNames);

            setupTable();
        } else {
            CustomAlert.showAlert("Database Error", "Unable to connect to the database.");
        }
    }

    private void setupTable() {
        colCustomerName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colService.setCellValueFactory(new PropertyValueFactory<>("service"));
        colStaff.setCellValueFactory(new PropertyValueFactory<>("staff"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
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
            List<LoyaltyCustomer> customers = loyaltyCustomerDAO.getAllCustomers();
            allCustomerNames.setAll(customers.stream().map(LoyaltyCustomer::getName).collect(Collectors.toList()));
        } catch (SQLException e) {
            CustomAlert.showAlert("Customer Load Error", e.getMessage());
        }
    }

    private void loadStaffNames() {
        try {
            com.salon.Model.Staff.StaffDAO staffDAO = new com.salon.Model.Staff.StaffDAO(conn);
            List<com.salon.Model.Staff.Staff> staffList = staffDAO.getAllStaff();
            allStaffNames.setAll(staffList.stream().map(com.salon.Model.Staff.Staff::getName).collect(Collectors.toList()));
        } catch (Exception e) {
            CustomAlert.showAlert("Staff Load Error", e.getMessage());
        }
    }

    private void handleCustomerAutoComplete(KeyEvent event) {
        String input = customerNameComboBox.getEditor().getText().toLowerCase();
        try {
            List<LoyaltyCustomer> filtered = loyaltyCustomerDAO.searchCustomersByName(input);
            ObservableList<String> matches = FXCollections.observableArrayList(
                    filtered.stream().map(LoyaltyCustomer::getName).collect(Collectors.toList()));
            customerNameComboBox.setItems(matches);
            customerNameComboBox.show();
        } catch (SQLException e) {
            CustomAlert.showAlert("Auto-complete Error", e.getMessage());
        }
    }


    @FXML
    private void handleAddService() {
        String customer = customerNameComboBox.getValue();
        String service = serviceComboBox.getValue();
        String staff = staffComboBox.getValue();

        if (customer == null || customer.trim().isEmpty() || service == null || staff == null) {
            CustomAlert.showAlert("Input Error", "Please fill all fields.");
            return;
        }

        for (PaymentSummary summary : summaryData) {
            if (summary.getCustomerName().equals(customer) && summary.getService().equals(service)) {
                CustomAlert.showAlert("Duplicate", "Customer already has this service.");
                return;
            }
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

        String phone = phoneNumberTxt.getText().trim();
        String name = customerNameComboBox.getValue();
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());

        if (phone.isEmpty() || name == null || name.trim().isEmpty()) {
            CustomAlert.showAlert("Missing Info", "Enter customer name and phone number.");
            return;
        }

        try {
            conn.setAutoCommit(false);

            // Insert customer if not exists
            String checkSQL = "SELECT COUNT(*) FROM customer WHERE phone = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSQL);
            checkStmt.setString(1, phone);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                String insertCustomer = "INSERT INTO customer (name, phone, join_date) VALUES (?, ?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertCustomer);
                insertStmt.setString(1, name);
                insertStmt.setString(2, phone);
                insertStmt.setString(3, date);
                insertStmt.executeUpdate();
            }

            // Insert into sales
            String insertSale = "INSERT INTO sales (customer_name, phone_number, services, staff, amount, description, date) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement insertSaleStmt = conn.prepareStatement(insertSale);
            String services = summaryData.stream().map(PaymentSummary::getService).collect(Collectors.joining(", "));
            String staff = summaryData.get(0).getStaff();
            String description = summaryData.stream().map(PaymentSummary::getDescription).collect(Collectors.joining("; "));
            double totalAmount = Double.parseDouble(totalAmountField.getText().trim());

            insertSaleStmt.setString(1, name);
            insertSaleStmt.setString(2, phone);
            insertSaleStmt.setString(3, services);
            insertSaleStmt.setString(4, staff);
            insertSaleStmt.setDouble(5, totalAmount);
            insertSaleStmt.setString(6, description);
            insertSaleStmt.setString(7, date);
            insertSaleStmt.executeUpdate();

            // Loyalty program update
            loyaltyCustomerDAO.addOrUpdateCustomerIfNeeded(name, phone, totalAmount, summaryData.get(0).getService());

            // Income saving with split logic
            IncomeDAO incomeDAO = new IncomeDAO(conn);
            Set<String> addedToOwner = new HashSet<>();

            for (PaymentSummary summary : summaryData) {
                String staffName = summary.getStaff();
                double amount = summary.getAmount();
                String service = summary.getService();

                if (staffName.equalsIgnoreCase("owner")) {
                    if (!addedToOwner.contains(service)) {
                        incomeDAO.insertIncome(new Income("owner", amount, date, service, name));
                        addedToOwner.add(service);
                    }
                } else {
                    double half = amount / 2;
                    incomeDAO.insertIncome(new Income(staffName, half, date, service, name));
                    incomeDAO.insertIncome(new Income("owner", half, date, service, name));
                }
            }

            conn.commit();
            CustomAlert.showSuccess("Payment recorded successfully.");
            clearPaymentFields();

        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            CustomAlert.showAlert("Error", "Error saving payment: " + e.getMessage());
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void clearPaymentFields() {
        customerNameComboBox.setValue(null);
        serviceComboBox.setValue(null);
        staffComboBox.setValue(null);
        phoneNumberTxt.clear();
        summaryData.clear();
        totalAmountField.clear();
        cashGivenField.clear();
        changeField.clear();
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
            phoneNumberTxt.clear();
            loadCustomerNames();
            loadServiceOptions();
            loadStaffNames();
        } else {
            CustomAlert.showAlert("Database Error", "No database connection.");
        }
    }
}
