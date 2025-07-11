package com.salon.Controller;

import com.salon.Model.Customers.LoyaltyCustomer;
import com.salon.Model.Customers.LoyaltyCustomerDAO;
import com.salon.Model.DatabaseConnection;
import com.salon.Utils.CustomAlert;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class LoyaltyCustomersConroller {

    @FXML private Button refreshBtn;
    @FXML private Button saveChangesBtn;
    @FXML private TableView<LoyaltyCustomer> loyaltyTable;
    @FXML private TableColumn<LoyaltyCustomer, String> colName, colPhone, colCategory, colService;
    @FXML private TableColumn<LoyaltyCustomer, Double> colTotal;
    @FXML private TableColumn<LoyaltyCustomer, Boolean> colHasDiscount;
    @FXML private TextField editName, editPhone, editTotal;

    private final ObservableList<LoyaltyCustomer> loyaltyData = FXCollections.observableArrayList();
    private LoyaltyCustomer selectedCustomer;

    @FXML
    public void initialize() {
        setupTableColumns();
        refreshLoyaltyData();
    }

    private void setupTableColumns() {
        colName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        colPhone.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPhone()));
        colTotal.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getTotalSpent()).asObject());
        colHasDiscount.setCellValueFactory(data -> new javafx.beans.property.SimpleBooleanProperty(data.getValue().isHasDiscount()).asObject());
        colCategory.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDiscountCategory()));
        colService.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getServiceType()));
        loyaltyTable.setItems(loyaltyData);

        loyaltyTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            selectedCustomer = newSel;
            if (newSel != null) {
                editName.setText(newSel.getName());
                editPhone.setText(newSel.getPhone());
                editTotal.setText(String.valueOf(newSel.getTotalSpent()));
            }
        });
    }

    @FXML
    private void refreshLoyaltyData() {
        try (Connection conn = DatabaseConnection.connect()) {
            LoyaltyCustomerDAO dao = new LoyaltyCustomerDAO(conn);
            List<LoyaltyCustomer> customers = dao.getAllLoyaltyCustomers();
            loyaltyData.setAll(customers);
            loyaltyTable.refresh();
        } catch (Exception e) {
            CustomAlert.showAlert("Refresh Error", "Failed to refresh loyalty data:\n" + e.getMessage());
        }
    }

    @FXML
    private void handleRefreshButton() {
        refreshLoyaltyData();
    }

    @FXML
    private void saveChanges() {
        if (selectedCustomer == null) {
            CustomAlert.showAlert("No Selection", "Please select a customer to edit.");
            return;
        }

        try {
            selectedCustomer.setName(editName.getText());
            selectedCustomer.setPhone(editPhone.getText());

            boolean eligible = selectedCustomer.getTotalSpent() >= 10000;
            selectedCustomer.setHasDiscount(eligible);
            selectedCustomer.setDiscountCategory(eligible ? "Yes" : "No");

            try (Connection conn = DatabaseConnection.connect()) {
                LoyaltyCustomerDAO dao = new LoyaltyCustomerDAO(conn);
                dao.updateLoyaltyCustomer(selectedCustomer);
                CustomAlert.showSuccess("Customer updated successfully.");
                refreshLoyaltyData();
            }

        } catch (Exception e) {
            CustomAlert.showAlert("Update Error", "Failed to update customer:\n" + e.getMessage());
        }
    }
}
