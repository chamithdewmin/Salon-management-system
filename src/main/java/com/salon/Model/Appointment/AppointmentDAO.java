package com.salon.Model.Appointment;

import com.salon.Model.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {

    // Insert a new appointment
    public static void insertAppointment(Appointment appointment) throws Exception {
        String sql = "INSERT INTO appointments (client_name, client_contact, appointment_date, appointment_time, service_type, staff_name, appointment_type, reminder_sent) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, appointment.getClientName());
            stmt.setString(2, appointment.getClientContact());
            stmt.setDate(3, appointment.getDate());
            stmt.setTime(4, appointment.getTime());
            stmt.setString(5, appointment.getServiceType());
            stmt.setString(6, appointment.getStaffName());
            stmt.setString(7, appointment.getAppointmentType());  // ✅
            stmt.setBoolean(8, appointment.isReminderSent());
            stmt.executeUpdate();
        }
    }

    // Automatically delete past appointments
    public static void deleteExpiredAppointments() throws Exception {
        String sql = "DELETE FROM appointments WHERE CONCAT(appointment_date, ' ', appointment_time) < NOW()";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        }
    }

    // Fetch all appointments
    public static List<Appointment> getAllAppointments() throws Exception {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT * FROM appointments ORDER BY appointment_date, appointment_time";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Appointment a = new Appointment();
                a.setId(rs.getInt("id"));
                a.setClientName(rs.getString("client_name"));
                a.setClientContact(rs.getString("client_contact"));
                a.setDate(rs.getDate("appointment_date"));
                a.setTime(rs.getTime("appointment_time"));
                a.setServiceType(rs.getString("service_type"));
                a.setStaffName(rs.getString("staff_name"));
                a.setAppointmentType(rs.getString("appointment_type")); // ✅
                a.setReminderSent(rs.getBoolean("reminder_sent"));
                list.add(a);
            }
        }

        return list;
    }

    // Delete by ID
    public static void deleteAppointmentById(int id) throws Exception {
        String sql = "DELETE FROM appointments WHERE id = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    // Update appointment
    public static void updateAppointment(Appointment appointment) throws Exception {
        String sql = "UPDATE appointments SET client_name=?, client_contact=?, appointment_date=?, appointment_time=?, service_type=?, staff_name=?, appointment_type=?, reminder_sent=? WHERE id=?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, appointment.getClientName());
            stmt.setString(2, appointment.getClientContact());
            stmt.setDate(3, appointment.getDate());
            stmt.setTime(4, appointment.getTime());
            stmt.setString(5, appointment.getServiceType());
            stmt.setString(6, appointment.getStaffName());
            stmt.setString(7, appointment.getAppointmentType()); // ✅
            stmt.setBoolean(8, appointment.isReminderSent());
            stmt.setInt(9, appointment.getId());

            stmt.executeUpdate();
        }
    }

    // Mark reminder as sent
    public static void markReminderSent(int id) throws Exception {
        String sql = "UPDATE appointments SET reminder_sent = 1 WHERE id = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
