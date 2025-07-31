package com.meztli.alufx;

import java.net.URL;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        System.out.println("Working dir: " + System.getProperty("user.dir"));
        URL fxml = HelloApplication.class.getResource("main-view.fxml");
        if (fxml == null) {
            System.err.println("❌ FXML no encontrado");
            throw new RuntimeException("FXML no encontrado");
        } else {
            System.out.println("✅ FXML encontrado en: " + fxml);
        }
        FXMLLoader fxmlLoader = new FXMLLoader(fxml);
        Scene scene = new Scene(fxmlLoader.load(), 500, 600);
        stage.setTitle("Alu-Helper");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        MariaDBLauncher.launch();
        launch();
    }
}