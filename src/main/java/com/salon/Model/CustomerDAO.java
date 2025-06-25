package com.salon.Model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {
    private final Connection conn;

    public CustomerDAO(Connection conn) {
        this.conn = conn;
    }

    public void addCustomer(Customer customer) throws SQLException {
        String sql = "INSERT INTO customers (full_name, phone, email, gender, join_date) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customer.getFullName());
            stmt.setString(2, customer.getPhone());
            stmt.setString(3, customer.getEmail());
            stmt.setString(4, customer.getGender());
            stmt.setDate(5, customer.getJoinDate());
            stmt.executeUpdate();
        }
    }

    public List<Customer> getAllCustomers() throws SQLException {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customers ORDER BY id DESC";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Customer(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("gender"),
                        rs.getDate("join_date")
                ));
            }
        }
        return list;
    }
}
