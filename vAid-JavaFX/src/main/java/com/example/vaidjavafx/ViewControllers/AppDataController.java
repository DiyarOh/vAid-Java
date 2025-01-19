package com.example.vaidjavafx.ViewControllers;

import com.example.vaidjavafx.TekstData;
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

    @FXML
    private void initialize() {
        loadVAids();
        loadTexts();
    }

    private void loadVAids() {
        ObservableList<VaidData> vAidList = FXCollections.observableArrayList();

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DB_FILE);
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM vAid")) {
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

        // Set up columns
        colSerialNumber.setCellValueFactory(new PropertyValueFactory<>("serienummer"));
        colModel.setCellValueFactory(new PropertyValueFactory<>("model"));
        colSoftwareVersion.setCellValueFactory(new PropertyValueFactory<>("softwareVersion"));

        // Add delete button to each row
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
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });

        vAidTable.setItems(vAidList);
    }

    private void loadTexts() {
        ObservableList<TekstData> textList = FXCollections.observableArrayList();

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DB_FILE);
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM Tekst")) {
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int textId = rs.getInt("tekst_id");
                String textContent = rs.getString("tekst");
                String tijdstip = rs.getString("tijdstip"); // Match property in TekstData
                String user = rs.getString("user");

                textList.add(new TekstData(textId, textContent, tijdstip, user));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Set up columns
        colTextId.setCellValueFactory(new PropertyValueFactory<>("tekstId")); // Match getter in TekstData
        colTextContent.setCellValueFactory(new PropertyValueFactory<>("tekst")); // Match getter in TekstData
        colTextTijdstip.setCellValueFactory(new PropertyValueFactory<>("tijdstip")); // Match getter in TekstData

        // Add delete button to each row
        colTextActions.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setOnAction(event -> {
                    TekstData text = getTableView().getItems().get(getIndex());
                    deleteText(text.getTekstId()); // Use TekstId for deletion
                    textTable.getItems().remove(text);
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

        textTable.setItems(textList);
    }

    private void deleteVAid(int serienummer) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DB_FILE);
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM vAid WHERE serienummer = ?")) {
            pstmt.setInt(1, serienummer);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteText(int textId) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DB_FILE);
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM Tekst WHERE tekst_id = ?")) {
            pstmt.setInt(1, textId);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBackButton() {
        ViewSwitcher.switchTo("dashboard-view.fxml");
    }
}