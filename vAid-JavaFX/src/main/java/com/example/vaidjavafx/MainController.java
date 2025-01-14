package com.example.vaidjavafx;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Objects;

public class MainController {

    @FXML
    private Button btnToevoegen;
    @FXML
    private Button btnConnect;
    @FXML
    private Button btnReset;
    @FXML
    private Button btnData;

    @FXML
    private void initialize() {
        // Set actions for buttons
        btnToevoegen.setOnAction(e -> showNotConnectedPopup());
        btnReset.setOnAction(e -> showNotConnectedPopup());
        btnData.setOnAction(e -> showNotConnectedPopup());
        btnConnect.setOnAction(e -> goToBrilView());
    }

    private void showNotConnectedPopup() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Connection Status");
        alert.setHeaderText("No Device Connected");
        alert.setContentText("Please ensure the Raspberry Pi is connected to the PC.");
        alert.showAndWait();
    }

    private void goToBrilView() {
        String ipAddress = "192.168.8.10";
        if (isRaspberryPiConnected()) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("bril_view.fxml"));
                Scene scene = new Scene(fxmlLoader.load());
                // Reapply the style sheet
                String stylesheetPath = Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm();
                scene.getStylesheets().add(stylesheetPath);

                Stage stage = (Stage) btnConnect.getScene().getWindow(); // Get the current stage
                stage.setScene(scene); // Switch to bril_view scene
            } catch (IOException ex) {
                ex.printStackTrace();
                showErrorPopup("Failed to load bril_view.fxml. Please try again.");
            }
        } else {
            showErrorPopup("Raspberry Pi not detected at " + ipAddress + ". Please check the connection.");
        }
    }

    private boolean isRaspberryPiConnected() {
        return USBDetection.isRaspberryPiConnected();
    }

    private void showErrorPopup(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Navigation Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
}