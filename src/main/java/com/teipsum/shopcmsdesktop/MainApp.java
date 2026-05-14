package com.teipsum.shopcmsdesktop;

import com.teipsum.shopcmsdesktop.config.AppConfig;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(
                Objects.requireNonNull(getClass().getResource("/com/teipsum/shopcmsdesktop/MainWindow.fxml"))
        );
        primaryStage.setTitle("Shop CMS");
        primaryStage.setScene(new Scene(root, 1200, 700));
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);
        primaryStage.show();

        System.out.println("API URL: " + AppConfig.getApiBaseUrl());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
