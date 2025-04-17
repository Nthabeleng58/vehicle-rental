package com.example.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    @Override

    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/Login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Vehicle Rental System - Login");
        stage.setScene(scene);
        stage.show();
    }

}
