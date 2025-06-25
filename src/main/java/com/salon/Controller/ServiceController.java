package com.salon.Controller;

import com.salon.Model.Services.Service;
import com.salon.Model.Services.ServiceDAO;
import com.salon.Utils.CustomAlert;
import com.salon.Model.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class ServiceController {

    public Button refreshBtn;
    @FXML private TextField nameField;
    @FXML private TextField priceField;
    @FXML private TableView<Service> serviceTable;
    @FXML private TableColumn<Service, String> nameCol;
    @FXML private TableColumn<Service, Double> priceCol;
    @FXML private Button addButton;
    @FXML private Button deleteButton;

    private ServiceDAO serviceDAO;
    private final ObservableList<Service> serviceList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        Connection conn = DatabaseConnection.connect();
        if (conn == null) {
            CustomAlert.showAlert(Alert.AlertType.ERROR, "DB Error", "Database connection failed.");
            return;
        }

        serviceDAO = new ServiceDAO(conn);

        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        loadServices();
    }

    @FXML
    private void onAddService() {
        String name = nameField.getText();
        String priceText = priceField.getText();

        if (name.isEmpty() || priceText.isEmpty()) {
            CustomAlert.showAlert(Alert.AlertType.WARNING, "Validation Error", "Fill in all fields.");
            return;
        }

        try {
            double price = Double.parseDouble(priceText);
            Service service = new Service(0, name, price);
            serviceDAO.addService(service);
            CustomAlert.showSuccess("Service added.");
            clearForm();
            loadServices();
        } catch (NumberFormatException e) {
            CustomAlert.showAlert(Alert.AlertType.ERROR, "Invalid Price", "Enter a valid price.");
        } catch (SQLException e) {
            CustomAlert.showAlert(Alert.AlertType.ERROR, "Database Error", "Could not add service.");
        }
    }

    @FXML
    private void onDeleteService() {
        Service selected = serviceTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            CustomAlert.showAlert(Alert.AlertType.WARNING, "No Selection", "Select a service to delete.");
            return;
        }

        try {
            serviceDAO.deleteService(selected.getId());
            CustomAlert.showSuccess("Service deleted.");
            loadServices();
        } catch (SQLException e) {
            CustomAlert.showAlert(Alert.AlertType.ERROR, "Database Error", "Could not delete service.");
        }
    }

    private void loadServices() {
        serviceList.clear();
        try {
            serviceList.addAll(serviceDAO.getAllServices());
            serviceTable.setItems(serviceList);
        } catch (SQLException e) {
            CustomAlert.showAlert(Alert.AlertType.ERROR, "Load Error", "Could not load services.");
        }
    }

    @FXML
    private void handleRefresh() {
        loadServices();
    }


    private void clearForm() {
        nameField.clear();
        priceField.clear();
    }
}
