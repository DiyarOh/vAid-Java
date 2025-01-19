package com.example.vaidjavafx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewSwitcher {
    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static void switchTo(String fxmlFile) {
        if (primaryStage == null) {
            throw new IllegalStateException("Primary stage has not been set. Call setPrimaryStage() first.");
        }

        try {
            System.out.println("Attempting to load FXML file: " + fxmlFile);

            // Adjust path resolution logic
            FXMLLoader loader = new FXMLLoader(ViewSwitcher.class.getResource("/com/example/vaidjavafx/" + fxmlFile));
            if (loader.getLocation() == null) {
                throw new IOException("FXML file not found: " + fxmlFile);
            }

            Parent root = loader.load();
            Scene scene = new Scene(root);

            // Add the stylesheet
            String stylesheetPath = ViewSwitcher.class.getResource("/com/example/vaidjavafx/style.css").toExternalForm();
            scene.getStylesheets().add(stylesheetPath);

            primaryStage.setScene(scene);
            primaryStage.show();
            System.out.println("Successfully switched to view: " + fxmlFile);
        } catch (IOException e) {
            System.err.println("Error loading FXML file: " + fxmlFile);
            e.printStackTrace();
        }
    }
}