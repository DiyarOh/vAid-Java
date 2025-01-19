package com.example.vaidjavafx.ViewControllers;

import com.example.vaidjavafx.Utility.DatabaseUtils;
import com.example.vaidjavafx.Utility.SessionManager;
import com.example.vaidjavafx.ViewSwitcher;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleLoginButton() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        int userId = DatabaseUtils.validateUser(username, password); // Validate user credentials
        if (userId != -1) {
            // Store user details in the session
            SessionManager.getInstance().setLoggedInUsername(username);
            SessionManager.getInstance().setLoggedInUserId(userId);

            // Navigate to the dashboard
            ViewSwitcher.switchTo("dashboard-view.fxml");
        } else {
            showErrorPopup("Invalid username or password.");
        }
    }

    @FXML
    private void switchToRegister() {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("register-view.fxml"));
            stage.getScene().setRoot(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void goToDashboard() {
        // Logic to switch to the dashboard view
        // Example:
        ViewSwitcher.switchTo("dashboard-view.fxml");
    }

    private void showErrorPopup(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Login Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfoPopup(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}