package com.example.vaidjavafx;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

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
        btnConnect.setOnAction(e -> handleConnectButton());
    }

    private void showNotConnectedPopup() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Connection Status");
        alert.setHeaderText("No Device Connected");
        alert.setContentText("Please ensure the Raspberry Pi is connected to the PC.");
        alert.showAndWait();
    }

    private void handleConnectButton() {
        String ipAddress = "192.168.8.10";

        if (isRaspberryPiConnected()) {
            // Switch to bril_view.fxml using the ViewSwitcher
            ViewSwitcher.switchTo("bril_view.fxml");
        } else {
            showErrorPopup("Raspberry Pi not detected at " + ipAddress + ". Please check the connection.");
        }
    }

    private boolean isRaspberryPiConnected() {
        // Implement Raspberry Pi detection logic here
        return USBDetection.isRaspberryPiConnected();
    }

    private void showErrorPopup(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Connection Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
}