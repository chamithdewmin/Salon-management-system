package com.salon.Model.Staff;

import com.salon.Model.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class StaffDAO {

    private final Connection connection;

    public StaffDAO() {
        connection = DatabaseConnection.connect();
    }

    public ObservableList<Staff> getAllStaff() {
        ObservableList<Staff> list = FXCollections.observableArrayList();
        String query = "SELECT * FROM staff"; // Ensure this table exists

        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Staff staff = new Staff(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("nic"),
                        rs.getString("address")
                );
                list.add(staff);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean addStaff(Staff staff) {
        String sql = "INSERT INTO staff (name, phone, nic, address) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, staff.getName());
            ps.setString(2, staff.getPhone());
            ps.setString(3, staff.getNic());
            ps.setString(4, staff.getAddress());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateStaff(Staff staff) {
        String sql = "UPDATE staff SET name = ?, phone = ?, nic = ?, address = ? WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, staff.getName());
            ps.setString(2, staff.getPhone());
            ps.setString(3, staff.getNic());
            ps.setString(4, staff.getAddress());
            ps.setInt(5, staff.getId());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteStaff(int id) {
        String sql = "DELETE FROM staff WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
