package com.salon.Controller;

import com.salon.Model.Staff.Staff;
import com.salon.Model.Staff.StaffDAO;
import com.salon.Utils.CustomAlert;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

public class StaffController {

    public Button refreshBtn;

    @FXML private TextField nameField;
    @FXML private TextField phoneField;
    @FXML private TextField nicField;
    @FXML private TextField addressField;

    @FXML private TableView<Staff> staffTable;
    @FXML private TableColumn<Staff, String> nameColumn;
    @FXML private TableColumn<Staff, String> phoneColumn;
    @FXML private TableColumn<Staff, String> nicColumn;
    @FXML private TableColumn<Staff, String> addressColumn;

    private Staff selectedStaff;
    private final StaffDAO staffDAO = new StaffDAO();

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(data -> data.getValue().nameProperty());
        phoneColumn.setCellValueFactory(data -> data.getValue().phoneProperty());
        nicColumn.setCellValueFactory(data -> data.getValue().nicProperty());
        addressColumn.setCellValueFactory(data -> data.getValue().addressProperty());

        loadStaff();
        staffTable.setOnMouseClicked(this::handleRowClick);
    }

    private void loadStaff() {
        ObservableList<Staff> staffList = staffDAO.getAllStaff();
        staffTable.setItems(staffList);
    }

    @FXML
    private void handleAddStaff() {
        String name = nameField.getText();
        String phone = phoneField.getText();
        String nic = nicField.getText();
        String address = addressField.getText();

        if (name.isEmpty() || phone.isEmpty() || nic.isEmpty() || address.isEmpty()) {
            CustomAlert.showAlert("Input Error", "Please fill in all fields.");
            return;
        }

        Staff staff = new Staff(0, name, phone, nic, address);
        if (staffDAO.addStaff(staff)) {
            CustomAlert.showAlert("Success", "Staff added successfully.");
            loadStaff();
            clearFields();
        } else {
            CustomAlert.showAlert("Add Failed", "Failed to add staff.");
        }
    }

    @FXML
    private void handleUpdateStaff() {
        if (selectedStaff == null) {
            CustomAlert.showAlert("Selection Error", "Please select a staff member to update.");
            return;
        }

        selectedStaff.setName(nameField.getText());
        selectedStaff.setPhone(phoneField.getText());
        selectedStaff.setNic(nicField.getText());
        selectedStaff.setAddress(addressField.getText());

        if (staffDAO.updateStaff(selectedStaff)) {
            CustomAlert.showAlert("Success", "Staff updated successfully.");
            loadStaff();
            clearFields();
        } else {
            CustomAlert.showAlert("Update Failed", "Failed to update staff.");
        }
    }

    @FXML
    private void handleDeleteStaff() {
        if (selectedStaff == null) {
            CustomAlert.showAlert("Selection Error", "Please select a staff member to delete.");
            return;
        }

        if (staffDAO.deleteStaff(selectedStaff.getId())) {
            CustomAlert.showAlert("Success", "Staff deleted successfully.");
            loadStaff();
            clearFields();
        } else {
            CustomAlert.showAlert("Delete Failed", "Failed to delete staff.");
        }
    }

    @FXML
    private void handleRefresh() {
        loadStaff();
        clearFields();
        CustomAlert.showAlert("Refreshed", "Page refreshed successfully.");
    }

    private void handleRowClick(MouseEvent event) {
        selectedStaff = staffTable.getSelectionModel().getSelectedItem();
        if (selectedStaff != null) {
            nameField.setText(selectedStaff.getName());
            phoneField.setText(selectedStaff.getPhone());
            nicField.setText(selectedStaff.getNic());
            addressField.setText(selectedStaff.getAddress());
        }
    }

    private void clearFields() {
        nameField.clear();
        phoneField.clear();
        nicField.clear();
        addressField.clear();
        selectedStaff = null;
    }
}
