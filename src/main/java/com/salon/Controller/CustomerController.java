package com.salon.Controller;

import com.salon.Model.Customers.Customer;
import com.salon.Model.Customers.CustomerDAO;
import com.salon.Model.DatabaseConnection;
import com.salon.Utils.CustomAlert;
import com.salon.Utils.ValidatorUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class CustomerController {

    public Button addCustomerButton;
    public Button updateBtn;
    public Button deleteBtn;
    public TextField searchBox;
    public Button searchBtn;

    @FXML private TextField nameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private ComboBox<String> genderBox;
    @FXML private DatePicker joinDatePicker;

    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, String> nameCol;
    @FXML private TableColumn<Customer, String> phoneCol;
    @FXML private TableColumn<Customer, String> emailCol;
    @FXML private TableColumn<Customer, String> genderCol;
    @FXML private TableColumn<Customer, Date> joinDateCol;

    private CustomerDAO customerDAO;
    private final ObservableList<Customer> customerList = FXCollections.observableArrayList();
    private Customer selectedCustomer = null;

    @FXML
    public void initialize() {
        Connection conn = DatabaseConnection.connect();
        if (conn == null) {
            CustomAlert.showAlert(Alert.AlertType.ERROR, "Database Error", "Could not connect to database.");
            return;
        }

        customerDAO = new CustomerDAO(conn);

        genderBox.setItems(FXCollections.observableArrayList("Male", "Female", "Other"));

        nameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        genderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));
        joinDateCol.setCellValueFactory(new PropertyValueFactory<>("joinDate"));

        customerTable.setItems(customerList);
        customerTable.setOnMouseClicked(event -> {
            selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
            if (selectedCustomer != null) {
                fillFormWithSelectedCustomer();
            }
        });

        searchBox.textProperty().addListener((obs, oldText, newText) -> {
            searchCustomers(newText.trim());
        });

        loadCustomers();
    }

    @FXML
    public void onAddCustomer() {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String gender = genderBox.getValue();
        LocalDate joinDate = joinDatePicker.getValue();

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || gender == null || joinDate == null) {
            CustomAlert.showAlert(Alert.AlertType.WARNING, "Missing Fields", "Please fill all customer details.");
            return;
        }

        if (!ValidatorUtil.isValidPhoneNumber(phone)) {
            CustomAlert.showAlert(Alert.AlertType.WARNING, "Invalid Phone", "Phone number must match Sri Lankan format (e.g., 077XXXXXXX)");
            return;
        }

        if (!ValidatorUtil.isValidEmail(email)) {
            CustomAlert.showAlert(Alert.AlertType.WARNING, "Invalid Email", "Please enter a valid email address.");
            return;
        }

        Customer customer = new Customer(0, name, phone, email, gender, Date.valueOf(joinDate));
        try {
            customerDAO.addCustomer(customer);
            CustomAlert.showSuccess("Customer added successfully!");
            clearFields();
            loadCustomers();
        } catch (SQLException e) {
            CustomAlert.showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to add customer.");
            e.printStackTrace();
        }
    }

    @FXML
    public void onUpdateCustomer() {
        if (selectedCustomer == null) {
            CustomAlert.showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a customer to update.");
            return;
        }

        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String gender = genderBox.getValue();
        LocalDate joinDate = joinDatePicker.getValue();

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || gender == null || joinDate == null) {
            CustomAlert.showAlert(Alert.AlertType.WARNING, "Missing Fields", "Please fill all customer details.");
            return;
        }

        if (!ValidatorUtil.isValidPhoneNumber(phone)) {
            CustomAlert.showAlert(Alert.AlertType.WARNING, "Invalid Phone", "Phone number must match Sri Lankan format.");
            return;
        }

        if (!ValidatorUtil.isValidEmail(email)) {
            CustomAlert.showAlert(Alert.AlertType.WARNING, "Invalid Email", "Please enter a valid email address.");
            return;
        }

        Customer updatedCustomer = new Customer(selectedCustomer.getId(), name, phone, email, gender, Date.valueOf(joinDate));
        try {
            customerDAO.updateCustomer(updatedCustomer);
            CustomAlert.showSuccess("Customer updated successfully!");
            clearFields();
            loadCustomers();
        } catch (SQLException e) {
            CustomAlert.showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to update customer.");
            e.printStackTrace();
        }
    }

    @FXML
    public void onDeleteCustomer() {
        if (selectedCustomer == null) {
            CustomAlert.showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a customer to delete.");
            return;
        }

        try {
            customerDAO.deleteCustomer(selectedCustomer.getId());
            CustomAlert.showSuccess("Customer deleted successfully!");
            clearFields();
            loadCustomers();
        } catch (SQLException e) {
            CustomAlert.showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to delete customer.");
            e.printStackTrace();
        }
    }

    private void loadCustomers() {
        customerList.clear();
        try {
            customerList.addAll(customerDAO.getAllCustomers());
            customerTable.setItems(customerList);
        } catch (SQLException e) {
            CustomAlert.showAlert(Alert.AlertType.ERROR, "Load Error", "Failed to load customer data.");
            e.printStackTrace();
        }
    }

    private void searchCustomers(String keyword) {
        if (keyword.isEmpty()) {
            customerTable.setItems(customerList);
            return;
        }

        try {
            List<Customer> filtered = customerDAO.searchCustomersByName(keyword);
            customerTable.setItems(FXCollections.observableArrayList(filtered));
        } catch (SQLException e) {
            CustomAlert.showAlert(Alert.AlertType.ERROR, "Search Error", "Failed to search customers.");
            e.printStackTrace();
        }
    }

    private void clearFields() {
        nameField.clear();
        phoneField.clear();
        emailField.clear();
        genderBox.setValue(null);
        joinDatePicker.setValue(null);
        selectedCustomer = null;
        customerTable.getSelectionModel().clearSelection();
    }

    private void fillFormWithSelectedCustomer() {
        nameField.setText(selectedCustomer.getFullName());
        phoneField.setText(selectedCustomer.getPhone());
        emailField.setText(selectedCustomer.getEmail());
        genderBox.setValue(selectedCustomer.getGender());
        joinDatePicker.setValue(selectedCustomer.getJoinDate().toLocalDate());
    }
}
