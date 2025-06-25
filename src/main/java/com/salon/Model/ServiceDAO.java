package com.salon.Model;

import com.salon.Model.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceDAO {

    private final Connection conn;

    public ServiceDAO(Connection conn) {
        this.conn = conn;
    }

    public void addService(Service service) throws SQLException {
        String sql = "INSERT INTO services (name, price) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, service.getName());
            stmt.setDouble(2, service.getPrice());
            stmt.executeUpdate();
        }
    }

    public List<Service> getAllServices() throws SQLException {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT * FROM services ORDER BY id DESC";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Service service = new Service(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price")
                );
                services.add(service);
            }
        }
        return services;
    }

    public void deleteService(int id) throws SQLException {
        String sql = "DELETE FROM services WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
