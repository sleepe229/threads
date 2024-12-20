package com.example.threads;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/example/threads/hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("Hello!");
        stage.setScene(scene);

        HelloController controller = fxmlLoader.getController();

        stage.setOnCloseRequest(event -> {controller.stopAllThreads();});
        stage.show();
//        initializeSmokers();
    }


    public static void main(String[] args) {
        launch();
    }
}