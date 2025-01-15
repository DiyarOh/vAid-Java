package com.example.vaidjavafx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewSwitcher {
    private static Stage primaryStage;

    /**
     * Sets the primary stage reference.
     *
     * @param stage The main application stage.
     */
    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    /**
     * Switches to the given FXML view.
     *
     * @param fxmlFile The FXML file to load.
     */
    public static void switchTo(String fxmlFile) {
        if (primaryStage == null) {
            throw new IllegalStateException("Primary stage has not been set. Call setPrimaryStage() first.");
        }

        try {
            FXMLLoader loader = new FXMLLoader(ViewSwitcher.class.getResource(fxmlFile));
            Parent root = loader.load();

            // Create the scene and set the stylesheet
            Scene scene = new Scene(root);
            String stylesheetPath = ViewSwitcher.class.getResource("style.css").toExternalForm();
            scene.getStylesheets().add(stylesheetPath);

            // Set the scene and show the stage
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}