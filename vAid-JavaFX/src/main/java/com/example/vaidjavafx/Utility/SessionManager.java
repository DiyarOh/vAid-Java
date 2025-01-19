package com.example.vaidjavafx.Utility;

/**
 * SessionManager is a singleton class to manage user session data.
 * It keeps track of the logged-in user's ID and username.
 */
public class SessionManager {
    private static SessionManager instance;
    private String loggedInUsername;
    private static int loggedInUserId;

    /**
     * Private constructor to enforce singleton pattern.
     */
    private SessionManager() {}

    /**
     * Retrieves the single instance of SessionManager.
     *
     * @return The instance of SessionManager.
     */
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    /**
     * Gets the logged-in user's username.
     *
     * @return The username of the logged-in user.
     */
    public String getLoggedInUsername() {
        return loggedInUsername;
    }

    /**
     * Sets the logged-in user's username.
     *
     * @param username The username to set.
     */
    public void setLoggedInUsername(String username) {
        this.loggedInUsername = username;
    }

    /**
     * Gets the logged-in user's ID.
     *
     * @return The user ID of the logged-in user.
     */
    public static int getLoggedInUserId() {
        return loggedInUserId;
    }

    /**
     * Sets the logged-in user's ID.
     *
     * @param userId The user ID to set.
     */
    public void setLoggedInUserId(int userId) {
        loggedInUserId = userId;
    }

    /**
     * Checks if a user is currently logged in.
     *
     * @return True if a user is logged in, false otherwise.
     */
    public boolean isLoggedIn() {
        return loggedInUsername != null;
    }

    /**
     * Logs out the current user and clears session data.
     */
    public void logout() {
        loggedInUsername = null;
        loggedInUserId = -1;
    }
}