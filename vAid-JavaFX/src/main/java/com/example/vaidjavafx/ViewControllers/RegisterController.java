package com.example.vaidjavafx.ViewControllers;

import com.example.vaidjavafx.Utility.DatabaseUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class RegisterController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleRegisterButton() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showErrorPopup("Username and Password cannot be empty.");
            return;
        }

        boolean isUserAdded = DatabaseUtils.insertUser(username, password);
        if (isUserAdded) {
            showInfoPopup("User registered successfully!");
            clearFields();
        } else {
            showErrorPopup("Failed to register user. Please try again.");
        }
    }

    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
    }

    private void showErrorPopup(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Registration Error");
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

    @FXML
    private void switchToLogin() {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login-view.fxml"));
            stage.getScene().setRoot(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}