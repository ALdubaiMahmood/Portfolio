package com.coffee;

import com.coffee.controller.LoginController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        LoginController login = new LoginController();
        Scene scene = login.buildScene(stage);
        stage.setTitle("KAFFE Management System");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}