package com.example.deansofficeapp;

import javafx.fxml.FXML;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    @FXML
    public ScrollPane teacher;
    @FXML
    public ScrollPane student;
    @FXML
    public ScrollPane rating;

    @FXML
    private ScrollPane discipline;

    @FXML
    private TabPane tab_pane;

    public TabPane getTabPane()
    {
        return tab_pane;
    }

    @FXML
    private void initialize() {
        loadTabContent(discipline, "TabDisciplineView.fxml");
        loadTabContent(teacher, "TabTeacherView.fxml");
        FXMLLoader loader_rating = loadTabContent(rating, "TabRatingView.fxml");

        try
        {
            // Создаём экземпляр второго контроллера с параметрами
            TabStudentController controller = new TabStudentController(this, loader_rating.getController());
            // Загружаем FXML с этим контроллером
            FXMLLoader loader = new FXMLLoader(getClass().getResource("TabStudentView.fxml"));
            loader.setController(controller); // Устанавливаем контроллер вручную

            // Открываем второе окно
            student.setContent(loader.load());
            // Дополнительные настройки (по желанию)
            student.setFitToWidth(true);  // Масштабировать содержимое по ширине
            student.setFitToHeight(false); // Прокрутка только по вертикали
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private FXMLLoader loadTabContent(ScrollPane container, String fxmlFile) {
        FXMLLoader loader = null;
        try {
            loader = new FXMLLoader(getClass().getResource(fxmlFile));

            // Устанавливаем содержимое ScrollPane
            container.setContent(loader.load());

            // Дополнительные настройки (по желанию)
            container.setFitToWidth(true);  // Масштабировать содержимое по ширине
            container.setFitToHeight(false); // Прокрутка только по вертикали


        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return loader;
    }
}