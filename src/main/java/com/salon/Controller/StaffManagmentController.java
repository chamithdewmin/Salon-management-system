package com.salon.Controller;

import com.salon.Model.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StaffManagmentController {

    @FXML
    private ComboBox<String> staffName;

    @FXML
    private ComboBox<String> Date; // Suggest: rename to `dateType` for clarity

    @FXML
    private Label TotalIncome;

    public void initialize() {
        // Load options
        staffName.setItems(getAllStaffNames());
        Date.setItems(FXCollections.observableArrayList("Daily", "Monthly"));

        // Add event listeners
        staffName.setOnAction(e -> updateIncome());
        Date.setOnAction(e -> updateIncome());
    }

    private void updateIncome() {
        String selectedStaff = staffName.getValue();
        String selectedType = Date.getValue();

        if (selectedStaff == null || selectedType == null) {
            TotalIncome.setText("Please select both");
            return;
        }

        double total = getTotalIncomeForStaff(selectedStaff, selectedType);
        TotalIncome.setText("Rs. " + String.format("%.2f", total));
    }

    private double getTotalIncomeForStaff(String staff, String type) {
        double total = 0.0;

        String query;
        if ("Daily".equalsIgnoreCase(type)) {
            query = "SELECT SUM(amount) FROM income WHERE recipient = ? AND date = CURDATE()";
        } else {
            query = "SELECT SUM(amount) FROM income WHERE recipient = ? " +
                    "AND MONTH(date) = MONTH(CURDATE()) AND YEAR(date) = YEAR(CURDATE())";
        }

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, staff);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                total = rs.getDouble(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return total;
    }

    private ObservableList<String> getAllStaffNames() {
        ObservableList<String> list = FXCollections.observableArrayList();

        String query = "SELECT DISTINCT recipient FROM income WHERE recipient IS NOT NULL";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(rs.getString("recipient"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
