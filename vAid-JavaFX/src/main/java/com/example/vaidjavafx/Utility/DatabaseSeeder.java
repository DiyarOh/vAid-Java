package com.example.vaidjavafx.Utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DatabaseSeeder {
    private static final String DATABASE_URL = "jdbc:sqlite:vAid.db";

    public static void seedDatabase() {
        try (Connection conn = DriverManager.getConnection(DATABASE_URL)) {
            // Seed Users
            seedUsers(conn);

            // Seed vAids
            seedVAids(conn);

            // Seed Teksts
            seedTeksts(conn);

            System.out.println("Database seeding completed!");
        } catch (SQLException e) {
            System.err.println("Error seeding the database:");
            e.printStackTrace();
        }
    }

    private static void seedUsers(Connection conn) throws SQLException {
        String userInsertSQL = "INSERT INTO User (username, password) VALUES (?, ?);";

        try (PreparedStatement pstmt = conn.prepareStatement(userInsertSQL)) {
            String[][] users = {
                    {"john_doe", "password123"},
                    {"jane_smith", "securePass!"},
                    {"robert_brown", "brownie456"},
                    {"emily_white", "wh1teDove"},
                    {"michael_johnson", "m1k3j0hn"},
                    {"sarah_clark", "clarky99"},
                    {"david_lewis", "lewis2025"},
                    {"emma_davis", "davemma76"},
                    {"oliver_wilson", "wilsonOl1"},
                    {"amelia_moore", "mooreMia@1"}
            };

            for (String[] user : users) {
                pstmt.setString(1, user[0]);
                pstmt.setString(2, user[1]);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            System.out.println("Inserted dummy users.");
        }
    }

    private static void seedVAids(Connection conn) throws SQLException {
        String vAidInsertSQL = "INSERT INTO vAid (serienummer, model, software_versie, eigenaar) VALUES (?, ?, ?, ?);";

        try (PreparedStatement pstmt = conn.prepareStatement(vAidInsertSQL)) {
            int startingSerialNumber = 20250121;

            for (int i = 1; i < 11; i++) {
                pstmt.setInt(1, startingSerialNumber + i);
                pstmt.setString(2, "vAidModel2025");
                pstmt.setInt(3, 1);
                pstmt.setInt(4, (i % 10) + 1); // Owner ID cycles from 1–10
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            System.out.println("Inserted dummy vAids.");
        }
    }

    private static void seedTeksts(Connection conn) throws SQLException {
        String tekstInsertSQL = "INSERT INTO Tekst (tekst, tijdstip, user) VALUES (?, ?, ?);";

        try (PreparedStatement pstmt = conn.prepareStatement(tekstInsertSQL)) {
            String[] texts = {
                    "This is a test message.",
                    "Another example text for the user.",
                    "vAid is working perfectly!",
                    "Text generated at random.",
                    "Let's add some variety to this.",
                    "Adding more dummy data for tests.",
                    "This is quite an interesting project!",
                    "Learning SQLite with Java.",
                    "Dummy text data for seeding.",
                    "Everything seems to be functional."
            };

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();

            for (int userId = 1; userId <= 10; userId++) {
                for (int i = 0; i < 3; i++) { // Each user gets 3 texts
                    pstmt.setString(1, texts[(userId + i) % texts.length]); // Cycle through text array
                    pstmt.setString(2, now.minusMinutes(userId * 10L + i).format(formatter)); // Generate different timestamps
                    pstmt.setInt(3, userId);
                    pstmt.addBatch();
                }
            }
            pstmt.executeBatch();
            System.out.println("Inserted dummy texts.");
        }
    }

    public static void main(String[] args) {
        seedDatabase();
    }
}