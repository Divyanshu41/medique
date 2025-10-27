package com.example.medique.temp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class ManualTableFix {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/new-hsrole_db";
        String user = "root";
        String password = "@Dibu__413";

        try (Connection conn = DriverManager.getConnection(url, user, password);
                Statement stmt = conn.createStatement()) {

            System.out.println("Connected to database successfully!");

            // First drop tables that reference appointments (foreign keys)
            System.out.println("Dropping dependent tables first...");
            String[] dependentTables = { "payments", "reviews", "medical_records" };
            for (String table : dependentTables) {
                try {
                    stmt.execute("DROP TABLE IF EXISTS " + table);
                    System.out.println("✅ " + table + " table dropped!");
                } catch (Exception e) {
                    System.out.println("⚠️ " + table + " table doesn't exist or already dropped");
                }
            }

            // Drop appointments table
            System.out.println("Dropping appointments table...");
            stmt.execute("DROP TABLE IF EXISTS appointments");
            System.out.println("✅ Appointments table dropped!");

            // Create with correct schema
            System.out.println("Creating appointments table with correct schema...");
            stmt.execute(
                    "CREATE TABLE appointments (" +
                            "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                            "patient_id BIGINT NOT NULL, " +
                            "doctor_id BIGINT NOT NULL, " +
                            "appointment_date DATE NOT NULL, " +
                            "time_slot VARCHAR(255) NOT NULL, " +
                            "status VARCHAR(255) NOT NULL, " +
                            "created_at DATETIME(6) NOT NULL, " +
                            "updated_at DATETIME(6), " +
                            "FOREIGN KEY (patient_id) REFERENCES users(id), " +
                            "FOREIGN KEY (doctor_id) REFERENCES users(id)" +
                            ") ENGINE=InnoDB");
            System.out.println("✅ Table created successfully with time_slot column!");

        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
