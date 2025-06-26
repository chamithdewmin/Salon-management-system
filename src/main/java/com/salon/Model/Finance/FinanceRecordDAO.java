package com.salon.Model.Finance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FinanceRecordDAO {
    public final Connection conn;

    public FinanceRecordDAO(Connection conn) {
        this.conn = conn;
    }

    public FinanceRecord getFinanceSummary() throws SQLException {
        double totalIncome = getTotalFromTable("income");
        double totalExpenses = getTotalFromTable("expense");
        return new FinanceRecord(totalIncome, totalExpenses);
    }

    private double getTotalFromTable(String tableName) throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount), 0) AS total FROM " + tableName;
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("total");
            }
        }
        return 0.0;
    }

    public boolean insertIncome(FinanceRecord record) {
        String sql = "INSERT INTO income (date, description, customerName, amount) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, record.getDate());
            stmt.setString(2, record.getDescription());
            stmt.setString(3, record.getCustomerName());
            stmt.setDouble(4, record.getAmount());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertExpense(FinanceRecord record) {
        String sql = "INSERT INTO expense (date, description, amount) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, record.getDate());
            stmt.setString(2, record.getDescription());
            stmt.setDouble(3, record.getAmount());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}