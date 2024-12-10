package com.example.deansofficeapp;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class Application extends javafx.application.Application
{
    @Override
    public void start(Stage stage) throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("MainView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setTitle("Деканат");
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) throws SQLException
    {
        DB.startConnection();   // начало соединения
        launch();
        DB.closeConnection();   // конец соединения
    }
}