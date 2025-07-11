package com.salon.Model.Income;

import com.salon.Model.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class IncomeDAO {
    private final Connection conn;

    public IncomeDAO(Connection conn) {
        this.conn = conn;
    }

    public void insertIncome(Income income) throws SQLException {
        String sql = "INSERT INTO income (recipient, amount, date, service, customer_name) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, income.getRecipient());
            stmt.setDouble(2, income.getAmount());
            stmt.setString(3, income.getDate());
            stmt.setString(4, income.getService());
            stmt.setString(5, income.getCustomerName());
            stmt.executeUpdate();
        }
    }


    // ✅ Finance summary method used in ReportController
    public Income getFinanceSummary() throws SQLException {
        double totalIncome = 0.0;
        double totalExpenses = 0.0;

        // Calculate total income
        String incomeQuery = "SELECT SUM(amount) AS total_income FROM income";
        try (PreparedStatement stmt = conn.prepareStatement(incomeQuery);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                totalIncome = rs.getDouble("total_income");
            }
        }

        // Calculate total expenses
        String expenseQuery = "SELECT SUM(amount) AS total_expenses FROM expenses";
        try (PreparedStatement stmt = conn.prepareStatement(expenseQuery);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                totalExpenses = rs.getDouble("total_expenses");
            }
        }

        double totalSaved = totalIncome - totalExpenses;
        return new Income(totalIncome, totalExpenses, totalSaved);
    }
}
