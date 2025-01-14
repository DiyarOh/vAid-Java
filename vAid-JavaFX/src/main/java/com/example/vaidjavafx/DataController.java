package com.example.vaidjavafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DataController {

    @FXML
    private TableView<TekstData> tekstTable;

    @FXML
    private TableColumn<TekstData, Integer> colTekstId;

    @FXML
    private TableColumn<TekstData, String> colTekst;

    @FXML
    private TableColumn<TekstData, String> colTijdstip;

    @FXML
    private TableColumn<TekstData, String> colUser;

    private ObservableList<TekstData> tekstList;

    @FXML
    public void initialize() {
        // Initialize the columns with data bindings
        colTekstId.setCellValueFactory(new PropertyValueFactory<>("tekstId"));
        colTekst.setCellValueFactory(new PropertyValueFactory<>("tekst"));
        colTijdstip.setCellValueFactory(new PropertyValueFactory<>("tijdstip"));
        colUser.setCellValueFactory(new PropertyValueFactory<>("user"));

        // Load data from the database
        loadTekstData();
    }

    private void loadTekstData() {
        tekstList = FXCollections.observableArrayList();

        // Database connection details
        String url = "jdbc:mysql://localhost:3306/vAid"; // Replace `vAid` with your actual database name
        String user = "root";
        String password = "your_password"; // Replace with your actual password

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String query = "SELECT * FROM Tekst";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                tekstList.add(new TekstData(
                        rs.getInt("tekst_id"),
                        rs.getString("tekst"),
                        rs.getString("tijdstip"),
                        rs.getString("user")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Set data in TableView
        tekstTable.setItems(tekstList);
    }
}