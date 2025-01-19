package com.example.vaidjavafx.ViewControllers;

import com.example.vaidjavafx.Utility.SessionManager;
import com.example.vaidjavafx.TekstData;
import com.example.vaidjavafx.ViewSwitcher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class DataController {

    public Button btnFetchData;
    @FXML
    private TableView<TekstData> tekstTable;

    @FXML
    private TableColumn<TekstData, Integer> colTekstId;

    @FXML
    private TableColumn<TekstData, String> colTekst;

    @FXML
    private TableColumn<TekstData, String> colTijdstip;

    @FXML
    private TableColumn<TekstData, Void> colActions;

    @FXML
    private ProgressIndicator loadingIndicator;

    @FXML
    private Button btnBack; // Back button

    @FXML
    private TableColumn<TekstData, String> colUser;

    private ObservableList<TekstData> tekstList;

    private static final String SERVER_IP = "192.168.8.10";
    private static final int SERVER_PORT = 5000;

    // Hold the logged-in user's ID
    private int loggedInUserId;

    public DataController() {
        // Retrieve the logged-in user's ID from a global utility or session-like class
        this.loggedInUserId = SessionManager.getLoggedInUserId();
    }

    @FXML
    public void initialize() {
        // Initialize the columns with data bindings
        colTekstId.setCellValueFactory(new PropertyValueFactory<>("tekstId"));
        colTekst.setCellValueFactory(new PropertyValueFactory<>("tekst"));
        colTijdstip.setCellValueFactory(new PropertyValueFactory<>("tijdstip"));

        // Load data from the database
        btnBack.setOnAction(e -> ViewSwitcher.switchTo("dashboard-view.fxml"));
        setupActionColumn();

        loadTekstData();
    }


    private void setupActionColumn() {
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setOnAction(event -> {
                    TekstData data = getTableView().getItems().get(getIndex());
                    deleteData(data);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });
    }

    private void deleteData(TekstData data) {
        if (data == null || data.getTekstId() == -1) {
            showErrorPopup("Invalid data selected for deletion.");
            return;
        }

        // Database connection details
        String dbFile = "vAid.db";
        String deleteSQL = "DELETE FROM Tekst WHERE tekst_id = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbFile);
             PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {

            // Set the ID parameter
            pstmt.setInt(1, data.getTekstId());

            // Execute deletion
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Successfully deleted entry with ID: " + data.getTekstId());
                tekstList.remove(data); // Remove from the list after successful deletion
            } else {
                showErrorPopup("Failed to delete the entry from the database. ID not found.");
            }
        } catch (SQLException e) {
            System.err.println("Error deleting data from the database:");
            e.printStackTrace();
            showErrorPopup("Error deleting the entry. Please try again.");
        }
    }

    @FXML
    private void fetchDataFromSocket() {
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Send the fetchData request
            out.println("fetchData");
            System.out.println("Request sent - fetchDataFromSocket called");

            // Receive the JSON response
            String response = in.readLine();
            System.out.println("Raw JSON response: " + response);

            if (response != null) {
                JSONArray jsonArray = new JSONArray(response);
                System.out.println("Parsed JSON array length: " + jsonArray.length());

                // Clear the current list and add new entries
                tekstList.clear();

                // Database connection details
                String dbFile = "vAid.db";
                String insertSQL = "INSERT INTO Tekst (tekst, tijdstip, user) VALUES (?, ?, ?)";

                // Get the logged-in user ID from SessionManager
                SessionManager.getInstance();
                int loggedInUserId = SessionManager.getLoggedInUserId();

                try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbFile);
                     PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            String tekst = jsonObject.optString("tekst", "Unknown");
                            String tijdstip = jsonObject.optString("tijdstip", "Unknown");

                            System.out.println("Tekst at index " + i + ": " + tekst);

                            // Add the entry to the list
                            tekstList.add(new TekstData(-1, tekst, tijdstip, null)); // Assuming user column is not displayed

                            // Save the entry to the database
                            pstmt.setString(1, tekst);
                            pstmt.setString(2, tijdstip);
                            pstmt.setInt(3, loggedInUserId); // Use logged-in user ID
                            pstmt.executeUpdate();

                            sendClearRequest();
                        } catch (Exception e) {
                            System.err.println("Error parsing or saving JSON object at index " + i);
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error inserting data into the database:");
                    e.printStackTrace();
                }

                // Update TableView
                tekstTable.setItems(tekstList);
            } else {
                showErrorPopup("No data received from the server.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorPopup("Error fetching data from the server. Please check the connection.");
        }
    }

    private void sendClearRequest() {
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Send the clearTexts request
            out.println("clearTexts");
            System.out.println("Clear request sent - sendClearRequest called");

            // Read the response from the server
            String response = in.readLine();
            if ("SUCCESS".equalsIgnoreCase(response)) {
                System.out.println("Texts folder cleared successfully on the server.");
            } else {
                System.err.println("Failed to clear the texts folder on the server.");
                showErrorPopup("Failed to clear the texts folder on the server.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorPopup("Error sending clear request to the server. Please check the connection.");
        }
    }

    private void loadTekstData() {
        tekstList = FXCollections.observableArrayList();

        // SQLite database file
        String dbFile = "vAid.db";
        String query = "SELECT * FROM Tekst WHERE user = " + loggedInUserId;

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbFile);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                tekstList.add(new TekstData(
                        rs.getInt("tekst_id"),
                        rs.getString("tekst"),
                        rs.getString("tijdstip"),
                        rs.getString("user")
                ));
            }

            // Bind data to the TableView
            tekstTable.setItems(tekstList);

            // Check if the list is empty and set a placeholder
            if (tekstList.isEmpty()) {
                tekstTable.setPlaceholder(new Text("No data available for the current user."));
            }

        } catch (Exception e) {
            e.printStackTrace();
            showErrorPopup("Error loading data from the database. Please check the database file and try again.");
        }
    }

    private void showErrorPopup(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Database Error");
        alert.setHeaderText("Data Load Failure");
        alert.setContentText(message);
        alert.showAndWait();
    }
}