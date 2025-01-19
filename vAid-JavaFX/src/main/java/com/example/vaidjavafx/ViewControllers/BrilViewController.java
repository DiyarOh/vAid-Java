package com.example.vaidjavafx.ViewControllers;

import com.example.vaidjavafx.ViewSwitcher;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;

/**
 * Controller for the BrilView, handling the display of vAid information
 * and interactions with the server.
 */
public class BrilViewController {

    @FXML
    private Button btnBack;

    @FXML
    private Button btnData;

    @FXML
    private Text serialNumberText;

    @FXML
    private Text versionText;

    @FXML
    private Text typeText;

    @FXML
    private Text titleText;

    private static final String SERVER_IP = "192.168.8.10";
    private static final int SERVER_PORT = 5000;

    /**
     * Initializes the BrilView by setting the title, fetching server data,
     * and configuring button actions.
     */
    @FXML
    private void initialize() {
        titleText.setText("vAid Info");
        fetchDataFromServer();
        btnBack.setOnAction(e -> ViewSwitcher.switchTo("dashboard-view.fxml"));
        btnData.setOnAction(e -> ViewSwitcher.switchTo("data-view.fxml"));
    }

    /**
     * Fetches vAid data from the server and updates the UI fields.
     */
    private void fetchDataFromServer() {
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("connect");

            String response = in.readLine();
            if (response != null) {
                JSONObject jsonResponse = new JSONObject(response);
                String serialNumber = jsonResponse.getString("serienummer");
                int version = jsonResponse.getInt("software_versie");
                String type = jsonResponse.getString("model");

                serialNumberText.setText("Serial Number: " + serialNumber);
                versionText.setText("Version: " + version);
                typeText.setText("Type: " + type);
            }
        } catch (IOException e) {
            showErrorPopup("Failed to fetch data from the server. Please check the connection.");
        }
    }

    /**
     * Displays an error popup with the specified message.
     *
     * @param message The error message to display.
     */
    private void showErrorPopup(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Connection Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
}