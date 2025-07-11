package com.salon.Controller;

import com.salon.Model.Expenses;
import com.salon.Model.ExpensesDAO;
import com.salon.Utils.CustomAlert;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.sql.Date;
import java.time.LocalDate;

public class AddExpensesController {

    @FXML private DatePicker expens_date;
    @FXML private TextField expens_description;
    @FXML private TextField expens_value;
    @FXML private ComboBox<String> categoryCombo;

    @FXML
    public void initialize() {
        categoryCombo.getItems().addAll("Needs", "Wants", "Savings", "Other");
    }

    @FXML
    public void handleAddExpense() {
        LocalDate date = expens_date.getValue();
        String description = expens_description.getText().trim();
        String category = categoryCombo.getValue();
        String amountStr = expens_value.getText().trim();

        if (date == null || description.isEmpty() || category == null || amountStr.isEmpty()) {
            CustomAlert.showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all fields.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            Expenses expense = new Expenses(Date.valueOf(date), description, category, amount);
            boolean success = ExpensesDAO.insertExpense(expense);

            if (success) {
                CustomAlert.showAlert(Alert.AlertType.INFORMATION, "Expense Added", "The expense was saved successfully.");
                clearForm();
            } else {
                CustomAlert.showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to save the expense.");
            }
        } catch (NumberFormatException e) {
            CustomAlert.showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid number for the amount.");
        }
    }

    private void clearForm() {
        expens_date.setValue(null);
        expens_description.clear();
        expens_value.clear();
        categoryCombo.setValue(null);
    }
}
