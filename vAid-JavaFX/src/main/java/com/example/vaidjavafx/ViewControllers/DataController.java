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

/**
 * Controller for managing data display and server communication for the Tekst table.
 */
public class DataController {

    @FXML
    private Button btnFetchData;

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
    private Button btnBack;

    @FXML
    private TableColumn<TekstData, String> colUser;

    private ObservableList<TekstData> tekstList;
    private static final String SERVER_IP = "192.168.8.10";
    private static final int SERVER_PORT = 5000;

    private final int loggedInUserId;

    /**
     * Constructor initializes the logged-in user ID from SessionManager.
     */
    public DataController() {
        this.loggedInUserId = SessionManager.getLoggedInUserId();
    }

    /**
     * Initializes the controller, sets up columns, and loads data.
     */
    @FXML
    public void initialize() {
        colTekstId.setCellValueFactory(new PropertyValueFactory<>("tekstId"));
        colTekst.setCellValueFactory(new PropertyValueFactory<>("tekst"));
        colTijdstip.setCellValueFactory(new PropertyValueFactory<>("tijdstip"));

        btnBack.setOnAction(e -> ViewSwitcher.switchTo("dashboard-view.fxml"));

        setupActionColumn();
        loadTekstData();
    }

    /**
     * Sets up the actions column with delete functionality.
     */
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
                setGraphic(empty ? null : deleteButton);
            }
        });
    }

    /**
     * Deletes a selected entry from the database and updates the table.
     *
     * @param data The entry to delete.
     */
    private void deleteData(TekstData data) {
        if (data == null || data.getTekstId() == -1) {
            showErrorPopup("Invalid data selected for deletion.");
            return;
        }

        String dbFile = "vAid.db";
        String deleteSQL = "DELETE FROM Tekst WHERE tekst_id = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbFile);
             PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {

            pstmt.setInt(1, data.getTekstId());
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                tekstList.remove(data);
            } else {
                showErrorPopup("Failed to delete the entry from the database.");
            }
        } catch (SQLException e) {
            showErrorPopup("Error deleting the entry. Please try again.");
        }
    }

    /**
     * Fetches data from the server and updates the table and database.
     */
    @FXML
    private void fetchDataFromSocket() {
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("fetchData");
            String response = in.readLine();

            if (response != null) {
                JSONArray jsonArray = new JSONArray(response);
                tekstList.clear();

                String dbFile = "vAid.db";
                String insertSQL = "INSERT INTO Tekst (tekst, tijdstip, user) VALUES (?, ?, ?)";

                try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbFile);
                     PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String tekst = jsonObject.optString("tekst", "Unknown");
                        String tijdstip = jsonObject.optString("tijdstip", "Unknown");

                        tekstList.add(new TekstData(-1, tekst, tijdstip, null));

                        pstmt.setString(1, tekst);
                        pstmt.setString(2, tijdstip);
                        pstmt.setInt(3, loggedInUserId);
                        pstmt.executeUpdate();
                    }
                }

                tekstTable.setItems(tekstList);
            } else {
                showErrorPopup("No data received from the server.");
            }
        } catch (Exception e) {
            showErrorPopup("Error fetching data from the server.");
        }
    }

    /**
     * Sends a request to the server to clear texts.
     */
    private void sendClearRequest() {
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true)) {

            out.println("clearTexts");
        } catch (Exception e) {
            showErrorPopup("Error sending clear request to the server.");
        }
    }

    /**
     * Loads data from the database into the table.
     */
    private void loadTekstData() {
        tekstList = FXCollections.observableArrayList();

        String dbFile = "vAid.db";
        String query = "SELECT * FROM Tekst WHERE user = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbFile);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            // Set the user ID in the prepared statement
            pstmt.setInt(1, loggedInUserId);

            // Execute the query
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tekstList.add(new TekstData(
                            rs.getInt("tekst_id"),
                            rs.getString("tekst"),
                            rs.getString("tijdstip"),
                            rs.getString("user")
                    ));
                }
            }

            // Set the data to the TableView
            tekstTable.setItems(tekstList);

            // Set a placeholder if the list is empty
            if (tekstList.isEmpty()) {
                tekstTable.setPlaceholder(new Text("No data available for the current user."));
            }

        } catch (Exception e) {
            showErrorPopup("Error loading data from the database.");
        }
    }

    /**
     * Displays an error popup with the specified message.
     *
     * @param message The error message to display.
     */
    private void showErrorPopup(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Database Error");
        alert.setHeaderText("Data Load Failure");
        alert.setContentText(message);
        alert.showAndWait();
    }
}