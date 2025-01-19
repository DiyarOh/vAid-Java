package com.example.vaidjavafx.ViewControllers;

import com.example.vaidjavafx.TekstData;
import com.example.vaidjavafx.Utility.SessionManager;
import com.example.vaidjavafx.VaidData;
import com.example.vaidjavafx.ViewSwitcher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Controller for the AppData view, handling the display and management
 * of vAids and texts in the database.
 */
public class AppDataController {

    @FXML
    private TableView<VaidData> vAidTable;
    @FXML
    private TableView<TekstData> textTable;

    @FXML
    private TableColumn<VaidData, Integer> colSerialNumber;
    @FXML
    private TableColumn<VaidData, String> colModel;
    @FXML
    private TableColumn<VaidData, Integer> colSoftwareVersion;
    @FXML
    private TableColumn<VaidData, Void> colVAidActions;

    @FXML
    private TableColumn<TekstData, Integer> colTextId;
    @FXML
    private TableColumn<TekstData, String> colTextContent;
    @FXML
    private TableColumn<TekstData, String> colTextTijdstip;
    @FXML
    private TableColumn<TekstData, Void> colTextActions;

    private static final String DB_FILE = "vAid.db";

    /**
     * Initializes the controller by loading vAids and texts from the database.
     */
    @FXML
    private void initialize() {
        loadVAids();
        loadTexts();
    }

    /**
     * Loads all vAids from the database and populates the vAidTable.
     */
    private void loadVAids() {
        ObservableList<VaidData> vAidList = FXCollections.observableArrayList();

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DB_FILE);
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM vAid WHERE eigenaar = ?")) {

            // Bind the logged-in user ID
            pstmt.setInt(1, SessionManager.getLoggedInUserId());

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int serienummer = rs.getInt("serienummer");
                String model = rs.getString("model");
                int softwareVersion = rs.getInt("software_versie");
                vAidList.add(new VaidData(serienummer, model, softwareVersion));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        colSerialNumber.setCellValueFactory(new PropertyValueFactory<>("serienummer"));
        colModel.setCellValueFactory(new PropertyValueFactory<>("model"));
        colSoftwareVersion.setCellValueFactory(new PropertyValueFactory<>("softwareVersion"));

        colVAidActions.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setOnAction(event -> {
                    VaidData vAid = getTableView().getItems().get(getIndex());
                    deleteVAid(vAid.getSerienummer());
                    vAidTable.getItems().remove(vAid);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteButton);
            }
        });

        vAidTable.setItems(vAidList);
    }

    /**
     * Loads all texts from the database and populates the textTable.
     */
    private void loadTexts() {
        ObservableList<TekstData> textList = FXCollections.observableArrayList();

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DB_FILE);
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM Tekst WHERE user = ?")) {

            // Bind the logged-in user ID
            pstmt.setInt(1, SessionManager.getLoggedInUserId());

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int textId = rs.getInt("tekst_id");
                String textContent = rs.getString("tekst");
                String tijdstip = rs.getString("tijdstip");
                String user = rs.getString("user");
                textList.add(new TekstData(textId, textContent, tijdstip, user));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        colTextId.setCellValueFactory(new PropertyValueFactory<>("tekstId"));
        colTextContent.setCellValueFactory(new PropertyValueFactory<>("tekst"));
        colTextTijdstip.setCellValueFactory(new PropertyValueFactory<>("tijdstip"));

        colTextActions.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setOnAction(event -> {
                    TekstData text = getTableView().getItems().get(getIndex());
                    deleteText(text.getTekstId());
                    textTable.getItems().remove(text);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteButton);
            }
        });

        textTable.setItems(textList);
    }

    /**
     * Deletes a vAid from the database based on its serial number.
     *
     * @param serienummer The serial number of the vAid to delete.
     */
    private void deleteVAid(int serienummer) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DB_FILE);
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM vAid WHERE serienummer = ?")) {
            pstmt.setInt(1, serienummer);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes a text from the database based on its ID.
     *
     * @param textId The ID of the text to delete.
     */
    private void deleteText(int textId) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DB_FILE);
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM Tekst WHERE tekst_id = ?")) {
            pstmt.setInt(1, textId);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the back button action by navigating to the dashboard view.
     */
    @FXML
    private void handleBackButton() {
        ViewSwitcher.switchTo("dashboard-view.fxml");
    }
}