package com.example.vaidjavafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.IOException;
import java.util.Objects;

public class vAidApplication extends Application {
    private static final String VIEW_FILE = "dashboard-view.fxml";
    private static final String STYLESHEET_FILE = "style.css";
    private static final String APP_TITLE = "vAid";
    private static final int SCENE_WIDTH = 600;
    private static final int SCENE_HEIGHT = 400;
    private static final int MAX_WIDTH = 800;
    private static final int MAX_HEIGHT = 600;

    private static final String DATABASE_FILE = "vAid.db";


    @Override
    public void start(Stage stage) throws IOException {
        initializeDatabase();

        // Load FXML file and create the scene
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(VIEW_FILE));
        Scene scene = new Scene(fxmlLoader.load(), SCENE_WIDTH, SCENE_HEIGHT);

        // Add CSS stylesheet
        String stylesheetPath = Objects.requireNonNull(getClass().getResource(STYLESHEET_FILE)).toExternalForm();
        scene.getStylesheets().add(stylesheetPath);

        // Configure stage properties
        stage.setTitle(APP_TITLE);
        stage.setScene(scene);
        stage.setMaxWidth(MAX_WIDTH);
        stage.setMaxHeight(MAX_HEIGHT);

        // Show the stage
        stage.show();
    }

    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_FILE)) {
            if (conn != null) {
                System.out.println("Connected to SQLite database: " + DATABASE_FILE);

                Statement stmt = conn.createStatement();

                // Create User table
                String createUserTableSQL = """
                        CREATE TABLE IF NOT EXISTS User (
                            user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                            username VARCHAR(35) NOT NULL,
                            password VARCHAR(35) NOT NULL
                        );
                        """;
                stmt.executeUpdate(createUserTableSQL);

                // Create vAid table
                String createVAidTableSQL = """
                        CREATE TABLE IF NOT EXISTS vAid (
                            serienummer INTEGER PRIMARY KEY,
                            model CHAR(10) NOT NULL,
                            software_versie INTEGER NOT NULL,
                            eigenaar INTEGER NOT NULL,
                            FOREIGN KEY (eigenaar) REFERENCES User (user_id)
                                ON DELETE CASCADE
                        );
                        """;
                stmt.executeUpdate(createVAidTableSQL);

                // Create Tekst table
                String createTekstTableSQL = """
                        CREATE TABLE IF NOT EXISTS Tekst (
                            tekst_id INTEGER PRIMARY KEY AUTOINCREMENT,
                            tekst TEXT NOT NULL,
                            tijdstip TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                            user INTEGER NOT NULL,
                            FOREIGN KEY (user) REFERENCES User (user_id)
                                ON DELETE CASCADE
                        );
                        """;
                stmt.executeUpdate(createTekstTableSQL);

                System.out.println("Database and tables initialized successfully.");
            }
        } catch (SQLException e) {
            System.err.println("Error initializing SQLite database:");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}