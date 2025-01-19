package com.example.vaidjavafx.ViewControllers;

import com.example.vaidjavafx.Utility.SessionManager;
import com.example.vaidjavafx.ViewSwitcher;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.text.Text;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Optional;

public class DataCreateController {
    public Text loadingText;
    public ProgressIndicator progressIndicator;

    @FXML
    private void initialize() {
        try {
            fetchDeviceDetailsFromSocket();
        } catch (Exception e) {
            e.printStackTrace();
            showErrorPopup("Error communicating with the server. Returning to dashboard.");
            ViewSwitcher.switchTo("dashboard-view.fxml");
        }
    }

    public void fetchDeviceDetailsFromSocket() throws Exception {
        String SERVER_IP = "192.168.8.10";
        int SERVER_PORT = 5000;

        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("create");

            String response = in.readLine();
            if (response != null) {
                JSONObject jsonResponse = new JSONObject(response);

                int serienummer = jsonResponse.getInt("serienummer");
                String model = jsonResponse.getString("model");
                int softwareVersie =  jsonResponse.getInt("software_versie");

                showConfirmationDialog(serienummer, model, softwareVersie);
            } else {
                throw new Exception("No response received from the server.");
            }
        } catch (Exception e) {
            // Propagate the exception to ensure proper handling in `initialize`
            throw new Exception("Failed to fetch device details.", e);
        }
    }

    private void showConfirmationDialog(int serienummer, String model, int softwareVersie) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Device Addition");
        confirmationAlert.setHeaderText("Device Addition Confirmation");
        confirmationAlert.setContentText(
                "U probeert de vAid met serienummer: " + serienummer +
                        ", model: " + model +
                        ", toe te voegen. Klopt dit?"
        );

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // User confirmed, save to the database
            saveVAidToDatabase(serienummer, model, softwareVersie, SessionManager.getInstance().getLoggedInUserId());
            ViewSwitcher.switchTo("data-view.fxml");
        } else {
            // User canceled
            System.out.println("Device addition canceled by the user.");
            ViewSwitcher.switchTo("dashboard-view.fxml"); // Return to dashboard if user cancels
        }
    }

    private void saveVAidToDatabase(int serienummer, String model, int softwareVersie, int eigenaar) {
        String dbFile = "vAid.db";
        String insertSQL = "INSERT INTO vAid (serienummer, model, software_versie, eigenaar) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbFile);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            pstmt.setInt(1, serienummer);
            pstmt.setString(2, model);
            pstmt.setInt(3, softwareVersie);
            pstmt.setInt(4, eigenaar);

            pstmt.executeUpdate();
            showInfoPopup("Device with Serial Number " + serienummer + " added successfully.");
        } catch (Exception e) {
            if (e.getMessage().contains("SQLITE_CONSTRAINT")) {
                // Handle unique constraint violation
                showErrorPopup("A device with Serial Number " + serienummer + " already exists in the database.");
            } else {
                // Handle other exceptions
                e.printStackTrace();
                showErrorPopup("Error saving device to the database.");
                ViewSwitcher.switchTo("dashboard-view.fxml");
            }
        }
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