package com.salon.Model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExpenseDAO {

    public static void insertExpense(Expences expense) {
        String sql = "INSERT INTO expense (date, description, value, category) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, expense.getDate());
            stmt.setString(2, expense.getDescription());
            stmt.setDouble(3, expense.getValue());
            stmt.setString(4, expense.getCategory());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Expences> getAllExpenses() {
        List<Expences> list = new ArrayList<>();
        String sql = "SELECT * FROM expense";

        try (Connection conn = DatabaseConnection.connect();

             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Expences e = new Expences();
                e.setId(rs.getInt("id"));
                e.setDate(rs.getDate("date"));
                e.setDescription(rs.getString("description"));
                e.setValue(rs.getDouble("value"));
                e.setCategory(rs.getString("category"));
                list.add(e);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void deleteExpense(int id) {
        String sql = "DELETE FROM expense WHERE id = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateExpense(Expences expense) {
        String sql = "UPDATE expense SET date = ?, description = ?, value = ?, category = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, expense.getDate());
            stmt.setString(2, expense.getDescription());
            stmt.setDouble(3, expense.getValue());
            stmt.setString(4, expense.getCategory());
            stmt.setInt(5, expense.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
