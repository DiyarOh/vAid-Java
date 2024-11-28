package com.example.vaidjavafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class vAidApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(vAidApplication.class.getResource("settings.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);

        // Add CSS stylesheet
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setMaxWidth(800);
        stage.setMaxHeight(600);
        // Set application title to vAid
        stage.setTitle("vAid");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}