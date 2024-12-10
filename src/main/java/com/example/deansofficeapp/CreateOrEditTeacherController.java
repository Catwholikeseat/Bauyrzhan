package com.example.deansofficeapp;

import javafx.fxml.FXML;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.sql.SQLException;

public class CreateOrEditTeacherController
{
    @FXML
    private TextField birth_field;

    @FXML
    private Button clear_button;

    @FXML
    private Button create_or_edit_button;

    @FXML
    private TextField experience_field;

    @FXML
    private TextField fio_field;

    @FXML
    private TextField number_field;

    @FXML
    private TextField place_field;

    private boolean is_create;
    private Teacher teacher;

    CreateOrEditTeacherController(boolean is_create, Teacher teacher)
    {
        this.teacher = teacher;
        this.is_create = is_create;
    }

    @FXML
    void initialize()
    {
        // если второе окно было открыто для редактирования:
        if (!is_create)
        {
            // в таком случае необходимо отобразить данные преподавателя:
            birth_field.setText(teacher.getDate_of_birth());
            experience_field.setText(String.valueOf(teacher.getWork_experience()));
            fio_field.setText(teacher.name + " " + teacher.surname + " " + teacher.patronymic);
            number_field.setText(teacher.getNumber_of_phone());
            place_field.setText(teacher.getPlace_of_residence());

            create_or_edit_button.setText("Сохранить изменения");
        }
        else    // для создания:
        {
            create_or_edit_button.setText("Создать");
        }
    }

    // метод очищает содержимое текстовых полей:
    @FXML
    void clear(MouseEvent event)
    {
        birth_field.clear();
        experience_field.clear();
        fio_field.clear();
        number_field.clear();
        place_field.clear();
    }

    // метод создает или изменяет преподавателя:
    @FXML
    void create_or_edite(MouseEvent event) throws SQLException
    {
        // String full_name, String date_of_birth, String place_of_residence, String number_of_phone, int work_experience
        Teacher teacher1 = new Teacher(
                fio_field.getText().trim(),
                birth_field.getText(),
                place_field.getText(),
                number_field.getText(),
                Integer.parseInt(experience_field.getText())
        );

        if (is_create)
        {
            if (!teacher1.create())
            {
                Alert alert = new Alert(Alert.AlertType.ERROR); // Тип: Ошибка
                alert.setTitle("Ошибка"); // Заголовок окна
                alert.setHeaderText(null); // Заголовок сообщения (можно задать или оставить пустым)
                alert.setContentText("Такой преподаватель уже существует"); // Основной текст сообщения
                alert.showAndWait(); // Отобразить и ждать закрытия
            }
        }
        else
        {
            teacher1.setID(teacher.getDbID());
            if (!teacher1.update())
            {
                Alert alert = new Alert(Alert.AlertType.ERROR); // Тип: Ошибка
                alert.setTitle("Ошибка"); // Заголовок окна
                alert.setHeaderText(null); // Заголовок сообщения (можно задать или оставить пустым)
                alert.setContentText("Такой преподаватель уже существует"); // Основной текст сообщения
                alert.showAndWait(); // Отобразить и ждать закрытия
            }
        }

        // необходима обновление страницы:
        TabTeacherController.reloadTeacherTable();
    }
}
