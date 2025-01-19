package com.example.vaidjavafx.Utility;

public class SessionManager {
    private static SessionManager instance;
    private String loggedInUsername; // To store the username
    private static int loggedInUserId;      // To store the user ID

    // Private constructor to enforce singleton
    private SessionManager() {}

    // Public method to get the single instance of SessionManager
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    // Getters and Setters
    public String getLoggedInUsername() {
        return loggedInUsername;
    }

    public void setLoggedInUsername(String username) {
        this.loggedInUsername = username;
    }

    public static int getLoggedInUserId() {
        return loggedInUserId;
    }

    public void setLoggedInUserId(int userId) {
        loggedInUserId = userId;
    }

    // Check if a user is logged in
    public boolean isLoggedIn() {
        return loggedInUsername != null;
    }

    // Log the user out
    public void logout() {
        loggedInUsername = null;
        loggedInUserId = -1;
    }
}