package com.salon.Model.Customers;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoyaltyCustomerDAO {
    private final Connection conn;

    public LoyaltyCustomerDAO(Connection conn) {
        this.conn = conn;
    }

    public List<LoyaltyCustomer> getAllCustomers() throws SQLException {
        List<LoyaltyCustomer> list = new ArrayList<>();
        String sql = "SELECT * FROM loyalty_customers";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            list.add(new LoyaltyCustomer(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("phone"),
                    rs.getDouble("total_spent"),
                    rs.getBoolean("has_discount"),
                    rs.getString("discount_category"),
                    rs.getString("service_type")
            ));
        }
        return list;
    }

    public List<LoyaltyCustomer> getAllLoyaltyCustomers() throws SQLException {
        return getAllCustomers(); // Alias for same method
    }

    public List<LoyaltyCustomer> searchCustomersByName(String keyword) throws SQLException {
        List<LoyaltyCustomer> result = new ArrayList<>();
        String sql = "SELECT * FROM loyalty_customers WHERE LOWER(name) LIKE ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, "%" + keyword.toLowerCase() + "%");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            result.add(new LoyaltyCustomer(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("phone"),
                    rs.getDouble("total_spent"),
                    rs.getBoolean("has_discount"),
                    rs.getString("discount_category"),
                    rs.getString("service_type")
            ));
        }
        return result;
    }

    public void addOrUpdateCustomerIfNeeded(String name, String phone, double newAmount, String serviceType) throws SQLException {
        String selectSQL = "SELECT * FROM loyalty_customers WHERE phone = ?";
        PreparedStatement selectStmt = conn.prepareStatement(selectSQL);
        selectStmt.setString(1, phone);
        ResultSet rs = selectStmt.executeQuery();

        if (rs.next()) {
            double currentTotal = rs.getDouble("total_spent") + newAmount;
            boolean eligible = currentTotal >= 10000;
            String category = eligible ? "Gold" : "None";

            String updateSQL = "UPDATE loyalty_customers SET total_spent = ?, has_discount = ?, discount_category = ?, service_type = ? WHERE phone = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateSQL);
            updateStmt.setDouble(1, currentTotal);
            updateStmt.setBoolean(2, eligible);
            updateStmt.setString(3, category);
            updateStmt.setString(4, serviceType);
            updateStmt.setString(5, phone);
            updateStmt.executeUpdate();
        } else {
            boolean eligible = newAmount >= 10000;
            String category = eligible ? "Gold" : "None";

            String insertSQL = "INSERT INTO loyalty_customers (name, phone, total_spent, has_discount, discount_category, service_type) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertSQL);
            insertStmt.setString(1, name);
            insertStmt.setString(2, phone);
            insertStmt.setDouble(3, newAmount);
            insertStmt.setBoolean(4, eligible);
            insertStmt.setString(5, category);
            insertStmt.setString(6, serviceType);
            insertStmt.executeUpdate();
        }
    }

    public void updateLoyaltyCustomer(LoyaltyCustomer customer) throws SQLException {
        String sql = "UPDATE loyalty_customers SET name=?, phone=?, total_spent=?, has_discount=?, discount_category=?, service_type=? WHERE id=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, customer.getName());
        ps.setString(2, customer.getPhone());
        ps.setDouble(3, customer.getTotalSpent());
        ps.setBoolean(4, customer.isHasDiscount());
        ps.setString(5, customer.getDiscountCategory());
        ps.setString(6, customer.getServiceType());
        ps.setInt(7, customer.getId());
        ps.executeUpdate();
    }
}
