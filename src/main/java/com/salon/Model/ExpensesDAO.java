package com.salon.Model;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class ExpensesDAO {

    public static boolean insertExpense(Expenses expense) {
        String sql = "INSERT INTO expenses (date, description, category, amount) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, expense.getDate());
            stmt.setString(2, expense.getDescription());
            stmt.setString(3, expense.getCategory());
            stmt.setDouble(4, expense.getAmount());

            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("❌ Failed to insert expense: " + e.getMessage());
            return false;
        }
    }
}
