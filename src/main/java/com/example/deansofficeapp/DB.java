package com.example.deansofficeapp;

import java.sql.*;
import java.util.ArrayList;

// Класс отвечает за работу с БД:
public class DB
{
    static Connection connection;

    // метод для открытия соединения:
    static void startConnection() throws SQLException
    {
        String url = "jdbc:sqlserver://desktop-lus0keh;encrypt = false;databaseName=DeansOffice";
        String user = "Bauyrzhan";
        String password = "123456";
        connection = DriverManager.getConnection(url, user, password);
    }

    // метод для закрытия соединения:
    static void closeConnection() throws SQLException
    {
        connection.close();
    }
}
