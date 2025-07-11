package com.salon.Model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SalesDAO {
    private final Connection conn;

    public SalesDAO(Connection conn) {
        this.conn = conn;
    }

    public boolean insertSale(String customerName, String phone, String services, String staff,
                              double amount, String description, String date) {
        try {
            String sql = "INSERT INTO sales (customer_name, phone_number, services, staff, amount, description, date) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, customerName);
            stmt.setString(2, phone);
            stmt.setString(3, services);
            stmt.setString(4, staff);
            stmt.setDouble(5, amount);
            stmt.setString(6, description);
            stmt.setString(7, date);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
