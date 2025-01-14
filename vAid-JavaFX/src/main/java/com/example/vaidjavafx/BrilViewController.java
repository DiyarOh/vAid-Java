package com.example.vaidjavafx;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.util.Objects;

public class BrilViewController {

    @FXML
    private Button btnBack; // Back button

    @FXML
    private Button btnData; // Data button

    @FXML
    private Text serialNumberText; // Serial Number Text

    @FXML
    private Text versionText; // Version Text

    @FXML
    private Text typeText; // Type Text

    @FXML
    private Text titleText; // Title Text

    private static final String SERVER_IP = "192.168.8.10"; // Change to your Raspberry Pi IP
    private static final int SERVER_PORT = 5000;

    @FXML
    private void initialize() {
        titleText.setText("vAid Info"); // Update the title

        // Fetch data from the socket server and update the fields
        fetchDataFromServer();

        // Set actions for buttons
        btnBack.setOnAction(e -> goBackToDashboard());
        btnData.setOnAction(e -> handleDataButton());
    }

    @FXML
    private void handleDataButton() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("data-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            // Apply stylesheet
            String stylesheetPath = Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm();
            scene.getStylesheets().add(stylesheetPath);

            Stage stage = (Stage) btnData.getScene().getWindow(); // Get the current stage
            stage.setScene(scene); // Switch to Data View
        } catch (IOException ex) {
            ex.printStackTrace();
            showErrorPopup("Failed to load data-view.fxml. Please try again.");
        }
    }

    private void fetchDataFromServer() {
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Send the "connect" command to the server
            out.println("connect");

            // Read the response from the server
            String response = in.readLine();
            if (response != null) {
                JSONObject jsonResponse = new JSONObject(response);
                String serialNumber = jsonResponse.getString("serial_number");
                String version = jsonResponse.getString("version");
                String type = jsonResponse.getString("name");

                // Update the UI
                serialNumberText.setText("Serial Number: " + serialNumber);
                versionText.setText("Version: " + version);
                typeText.setText("Type: " + type);
            }
        } catch (IOException e) {
            e.printStackTrace();
            showErrorPopup("Failed to fetch data from the server. Please check the connection.");
        }
    }

    @FXML
    private void goBackToDashboard() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("dashboard-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            // Reapply the style sheet
            String stylesheetPath = Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm();
            scene.getStylesheets().add(stylesheetPath);

            Stage stage = (Stage) btnBack.getScene().getWindow(); // Get the current stage
            stage.setScene(scene); // Switch to dashboard-view
        } catch (IOException ex) {
            ex.printStackTrace();
            showErrorPopup("Failed to load dashboard-view.fxml. Please try again.");
        }
    }

    private void showInfoPopup(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorPopup(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Connection Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
}