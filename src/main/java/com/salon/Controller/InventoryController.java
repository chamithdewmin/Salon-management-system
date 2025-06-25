package com.salon.Controller;

import com.salon.Model.InventoryDAO;
import com.salon.Model.InventoryItem;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

public class InventoryController {

    @FXML
    private TextField itemNameField;

    @FXML
    private TextField itemQuantityField;

    @FXML
    private TextField itemPriceField;

    @FXML
    private TableView<InventoryItem> inventoryTable;

    @FXML
    private TableColumn<InventoryItem, String> nameColumn;

    @FXML
    private TableColumn<InventoryItem, Integer> quantityColumn;

    @FXML
    private TableColumn<InventoryItem, Double> priceColumn;

    private InventoryItem selectedItem;
    private final InventoryDAO inventoryDAO = new InventoryDAO();

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        quantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());

        loadInventory();
        inventoryTable.setOnMouseClicked(this::handleRowClick);
    }

    private void loadInventory() {
        ObservableList<InventoryItem> items = inventoryDAO.getAllItems();
        inventoryTable.setItems(items);
    }

    @FXML
    private void handleAddItem() {
        String name = itemNameField.getText();
        String quantityText = itemQuantityField.getText();

        if (name.isEmpty() || quantityText.isEmpty()) {
            showAlert("Please fill in all fields.");
            return;
        }

        try {
            int quantity = Integer.parseInt(quantityText);

            InventoryItem newItem = new InventoryItem(0, name, quantity); // ID will be auto-generated
            boolean success = inventoryDAO.addItem(newItem);

            if (success) {
                showInfo("Item added successfully.");
                loadInventory();
                clearFields();
            } else {
                showAlert("Failed to add item.");
            }
        } catch (NumberFormatException e) {
            showAlert("Quantity and Price must be valid numbers.");
        }
    }

    @FXML
    private void handleUpdateItem() {
        if (selectedItem == null) {
            showAlert("Please select an item to update.");
            return;
        }

        try {
            selectedItem.setName(itemNameField.getText());
            selectedItem.setQuantity(Integer.parseInt(itemQuantityField.getText()));

            boolean success = inventoryDAO.updateItem(selectedItem);
            if (success) {
                showInfo("Item updated successfully.");
                loadInventory();
                clearFields();
            } else {
                showAlert("Failed to update item.");
            }

        } catch (NumberFormatException e) {
            showAlert("Quantity and Price must be valid numbers.");
        }
    }

    @FXML
    private void handleDeleteItem() {
        if (selectedItem != null) {
            boolean success = inventoryDAO.deleteItem(selectedItem.getId());
            if (success) {
                showInfo("Item deleted successfully.");
                loadInventory();
                clearFields();
            } else {
                showAlert("Failed to delete item.");
            }
        } else {
            showAlert("Please select an item to delete.");
        }
    }

    private void handleRowClick(MouseEvent event) {
        selectedItem = inventoryTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            itemNameField.setText(selectedItem.getName());
            itemQuantityField.setText(String.valueOf(selectedItem.getQuantity()));
        }
    }

    private void clearFields() {
        itemNameField.clear();
        itemQuantityField.clear();
        itemPriceField.clear();
        selectedItem = null;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
