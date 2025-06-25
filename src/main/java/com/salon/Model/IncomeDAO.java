package com.salon.Model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IncomeDAO {
    private final Connection conn;

    public IncomeDAO(Connection conn) {
        this.conn = conn;
    }

    public boolean insertIncome(Income income) throws SQLException {
        String sql = "INSERT INTO income (date, description, value) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, income.getDate());
            stmt.setString(2, income.getDescription());
            stmt.setDouble(3, income.getValue());
            return stmt.executeUpdate() > 0;
        }
    }

    public List<Income> getAllIncome() {
        List<Income> incomeList = new ArrayList<>();
        String sql = "SELECT * FROM income";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Income income = new Income();
                income.setId(rs.getInt("id"));
                income.setDate(rs.getString("date"));
                income.setDescription(rs.getString("description"));
                income.setValue(rs.getDouble("value"));
                incomeList.add(income);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return incomeList;
    }

    public boolean deleteIncome(int id) {
        String sql = "DELETE FROM income WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}