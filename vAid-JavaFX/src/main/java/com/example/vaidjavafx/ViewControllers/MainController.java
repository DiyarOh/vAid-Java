package com.example.vaidjavafx.ViewControllers;

import com.example.vaidjavafx.Utility.USBDetection;
import com.example.vaidjavafx.ViewSwitcher;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import com.example.vaidjavafx.ViewControllers.DataCreateController;

public class MainController {

    @FXML
    private Button btnToevoegen;
    @FXML
    private Button btnConnect;
//    @FXML
//    private Button btnReset;
    @FXML
    private Button btnAppData;

    @FXML
    private void initialize() {
        // Set actions for buttons
        btnToevoegen.setOnAction(e -> handleToevoegenButton());
//        btnReset.setOnAction(e -> showNotConnectedPopup());
        btnAppData.setOnAction(e -> handleAppDataButton());
        btnConnect.setOnAction(e -> handleConnectButton());
    }

    private void  handleAppDataButton(){
        ViewSwitcher.switchTo("app-data-view.fxml");
    }
    private void showNotConnectedPopup() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Connection Status");
        alert.setHeaderText("No Device Connected");
        alert.setContentText("Please ensure the vAid is connected to the PC.");
        alert.showAndWait();
    }

    private void handleConnectButton() {
        String ipAddress = "192.168.8.10";

        if (isRaspberryPiConnected()) {
            // Switch to bril_view.fxml using the ViewSwitcher
            ViewSwitcher.switchTo("bril_view.fxml");
        } else {
            showErrorPopup("vAid not detected, please check the connection.");
        }
    }

    @FXML
    private void handleToevoegenButton() {
        if (isRaspberryPiConnected()) {
            try {
                DataCreateController controller = new DataCreateController();
                controller.fetchDeviceDetailsFromSocket();
            } catch (Exception e) {
                e.printStackTrace();
                showErrorPopup("Failed to add device. Please try again.");
            }        } else {
            showErrorPopup("vAid not detected. Please check the connection.");
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


    private void showInfoPopup(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}