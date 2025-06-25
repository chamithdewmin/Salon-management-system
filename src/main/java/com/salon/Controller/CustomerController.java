package com.salon.Controller;

import com.salon.Model.Customer;
import com.salon.Model.CustomerDAO;
import com.salon.Model.DatabaseConnection;
import com.salon.Utils.CustomAlert;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;

public class CustomerController {

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

        loadCustomers();
    }

    @FXML
    public void onAddCustomer() {
        String name = nameField.getText();
        String phone = phoneField.getText();
        String email = emailField.getText();
        String gender = genderBox.getValue();
        LocalDate joinDate = joinDatePicker.getValue();

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || gender == null || joinDate == null) {
            CustomAlert.showAlert(Alert.AlertType.WARNING, "Missing Fields", "Please fill all customer details.");
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

    private void clearFields() {
        nameField.clear();
        phoneField.clear();
        emailField.clear();
        genderBox.setValue(null);
        joinDatePicker.setValue(null);
    }
}
