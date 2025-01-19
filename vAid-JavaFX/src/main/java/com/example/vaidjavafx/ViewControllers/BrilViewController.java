package com.example.vaidjavafx.ViewControllers;

import com.example.vaidjavafx.ViewSwitcher;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;

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

    private static final String SERVER_IP = "192.168.8.10"; // Raspberry Pi IP
    private static final int SERVER_PORT = 5000;

    @FXML
    private void initialize() {
        titleText.setText("vAid Info"); // Set the title

        // Fetch data from the server and populate UI fields
        fetchDataFromServer();

        // Set button actions
        btnBack.setOnAction(e -> ViewSwitcher.switchTo("dashboard-view.fxml"));
        btnData.setOnAction(e -> ViewSwitcher.switchTo("data-view.fxml"));
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
                String serialNumber = jsonResponse.getString("serienummer");
                int
                        version = jsonResponse.getInt("software_versie");
                String type = jsonResponse.getString("model");

                // Update the UI fields
                serialNumberText.setText("Serial Number: " + serialNumber);
                versionText.setText("Version: " + version);
                typeText.setText("Type: " + type);
            }
        } catch (IOException e) {
            showErrorPopup("Failed to fetch data from the server. Please check the connection.");
        }
    }

    private void showErrorPopup(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Connection Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
}