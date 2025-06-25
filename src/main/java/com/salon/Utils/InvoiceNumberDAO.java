package com.salon.Utils;

import com.salon.Model.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InvoiceNumberDAO {

    public static String getNextInvoiceNumber() {
        String nextNumber = "00001";

        String selectQuery = "SELECT last_number FROM invoice_sequence WHERE id = 1";
        String updateQuery = "UPDATE invoice_sequence SET last_number = ? WHERE id = 1";
        String insertQuery = "INSERT INTO invoice_sequence (id, last_number) VALUES (1, 1)";

        try (Connection conn = DatabaseConnection.connect()) {
            if (conn == null) return nextNumber;

            try (PreparedStatement selectStmt = conn.prepareStatement(selectQuery);
                 ResultSet rs = selectStmt.executeQuery()) {

                if (rs.next()) {
                    int lastNumber = rs.getInt("last_number") + 1;
                    nextNumber = String.format("%05d", lastNumber);

                    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                        updateStmt.setInt(1, lastNumber);
                        updateStmt.executeUpdate();
                    }

                } else {
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                        insertStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("\u274C Invoice Number Generation Failed: " + e.getMessage());
        }

        return nextNumber;
    }
}
