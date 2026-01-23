package com.gestionpharmacie;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        Globals.stage = primaryStage;
        Globals.controllers.login.show();
        primaryStage.show();
    }

    public static void main(String[] args) {
        Globals.init();
        Globals.config.databaseUrl = "jdbc:mysql://localhost:3306/pharmacy";
        Globals.config.databaseUser = "pharma";
        Globals.config.databasePassword = "StrongPassword123";
        Globals.database.connect();

        Globals.config.resourcePath = "/com/gestionpharmacie/";
        Globals.scenes.loadStyles();

        Globals.controllers.init();
        Globals.managers.init();

        launch(args);
    }
}

