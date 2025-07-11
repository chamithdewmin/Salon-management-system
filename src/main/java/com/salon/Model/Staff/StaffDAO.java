package com.salon.Model.Staff;

import com.salon.Model.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class StaffDAO {

    private final Connection connection;

    public StaffDAO(Connection conn) {
        connection = DatabaseConnection.connect();
    }

    // 🔹 Fetch all staff (full object list)
    public ObservableList<Staff> getAllStaff() {
        ObservableList<Staff> list = FXCollections.observableArrayList();
        String query = "SELECT * FROM staff";

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

    // ✅ New: Fetch only staff names (for ComboBox)
    public ObservableList<String> getAllStaffNames() {
        ObservableList<String> names = FXCollections.observableArrayList();
        String sql = "SELECT name FROM staff ORDER BY name ASC";

        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                names.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return names;
    }

    // 🔹 Add new staff member
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

    // 🔹 Update staff details
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

    // 🔹 Delete staff by ID
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
