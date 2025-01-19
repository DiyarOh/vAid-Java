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

            // Load the new FXML file
            FXMLLoader loader = new FXMLLoader(ViewSwitcher.class.getResource("/com/example/vaidjavafx/" + fxmlFile));
            if (loader.getLocation() == null) {
                throw new IOException("FXML file not found: " + fxmlFile);
            }

            Parent newRoot = loader.load();

            // Get the current scene and update its root
            Scene currentScene = primaryStage.getScene();
            if (currentScene == null) {
                // If no scene exists, create a new one
                currentScene = new Scene(newRoot);
                primaryStage.setScene(currentScene);
            } else {
                // Update the root without recreating the scene
                currentScene.setRoot(newRoot);
            }

            // Add the stylesheet (if not already added)
            String stylesheetPath = ViewSwitcher.class.getResource("/com/example/vaidjavafx/style.css").toExternalForm();
            if (!currentScene.getStylesheets().contains(stylesheetPath)) {
                currentScene.getStylesheets().add(stylesheetPath);
            }

            System.out.println("Successfully switched to view: " + fxmlFile);
        } catch (IOException e) {
            System.err.println("Error loading FXML file: " + fxmlFile);
            e.printStackTrace();
        }
    }
}