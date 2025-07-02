package com.salon.Model.Customers;

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

    public void updateCustomer(Customer customer) throws SQLException {
        String sql = "UPDATE customers SET full_name = ?, phone = ?, email = ?, gender = ?, join_date = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customer.getFullName());
            stmt.setString(2, customer.getPhone());
            stmt.setString(3, customer.getEmail());
            stmt.setString(4, customer.getGender());
            stmt.setDate(5, customer.getJoinDate());
            stmt.setInt(6, customer.getId());
            stmt.executeUpdate();
        }
    }

    public void deleteCustomer(int customerId) throws SQLException {
        String sql = "DELETE FROM customers WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
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

    public List<Customer> searchCustomersByName(String keyword) throws SQLException {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customers WHERE LOWER(full_name) LIKE ? ORDER BY id DESC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + keyword.toLowerCase() + "%");
            try (ResultSet rs = stmt.executeQuery()) {
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
        }
        return list;
    }
}
