package com.example.vaidjavafx.Utility;

import java.sql.*;

public class DatabaseUtils {
    private static final String DATABASE_URL = "jdbc:sqlite:vAid.db";

    /**
     * Validates a user's login credentials.
     *
     * @param username The username of the user.
     * @param password The password of the user.
     * @return true if the credentials are valid, false otherwise.
     */
    public static int validateUser(String username, String password) {
        String validateLoginSQL = "SELECT user_id FROM User WHERE username = ? AND password = ?;";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement(validateLoginSQL)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_id");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error validating login credentials:");
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * Inserts a new user into the User table.
     *
     * @param username The username of the new user.
     * @param password The password of the new user.
     * @return true if the user was inserted successfully, false otherwise.
     */
    public static boolean insertUser(String username, String password) {
        String insertUserSQL = "INSERT INTO User (username, password) VALUES (?, ?);";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement(insertUserSQL)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error inserting user into the database:");
            e.printStackTrace();
            return false;
        }
    }
}