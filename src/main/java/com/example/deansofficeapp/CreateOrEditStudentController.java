package com.example.deansofficeapp;

import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CreateOrEditStudentController
{
    @FXML
    private Button clear_button;

    @FXML
    private Button create_or_edit_button;

    @FXML
    private ComboBox<String> day_box;

    @FXML
    private TextField fio_field;

    @FXML
    private ToggleGroup gender_toggle_group;

    @FXML
    private ComboBox<String> group_box;

    @FXML
    private ComboBox<String> month_box;

    @FXML
    private TextField number_box;

    @FXML
    private TextField place_box;

    @FXML
    private ComboBox<String> year_box;

    @FXML
    private RadioButton female_gender;

    @FXML
    private RadioButton male_gender;

    Student student;

    boolean is_create;

    CreateOrEditStudentController(Student student, boolean is_create)
    {
        this.student = student;
        this.is_create = is_create;
    }

    @FXML
    void initialize() throws SQLException
    {
        // перед выполнением прочих действий необходимо добавить данные в поля групп, день, год, месяц:
        ObservableList<String> days = FXCollections.observableArrayList();
        ObservableList<String> months = FXCollections.observableArrayList();
        ObservableList<String> years = FXCollections.observableArrayList();
        ObservableList<String> groups = FXCollections.observableArrayList();

        // дни:
        for (int i = 1; i <= 31; ++i)
        {
            days.add(String.valueOf(i));
        }

        // месяцы:
        for (int i = 1; i <= 12; ++i)
        {
            months.add(String.valueOf(i));
        }

        // года:
        for (int i = 1924; i <= 2024; ++i)
        {
            years.add(String.valueOf(i));
        }

        // группы:
        String sql = "SELECT * FROM [Group]";
        PreparedStatement ps = DB.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next())
        {
            groups.add(rs.getString("name_group"));
        }

        day_box.setItems(days);
        month_box.setItems(months);
        year_box.setItems(years);
        group_box.setItems(groups);

        if (!is_create && student != null)
        {
            fio_field.setText(student.surname + " " + student.name + " " + student.patronymic);
            if ((student.gender.equals("М")))
            {
                male_gender.setSelected(true);
            } else
            {
                female_gender.setSelected(true);
            }
            group_box.getSelectionModel().select(student.getGroupName());
            year_box.getSelectionModel().select(student.getArrayDateBirth()[0]);
            month_box.getSelectionModel().select(student.getArrayDateBirth()[1]);
            day_box.getSelectionModel().select(student.getArrayDateBirth()[2]);
            place_box.setText(student.place_of_residence);
            number_box.setText(student.number_of_phone);
            //year_box.getSelectionModel().select(student);

            create_or_edit_button.setText("Сохранить изменения");
            // в таком случае нужно отобразить данные студента на соответствующих полях
        }
        else    // если необходимо создать студента
        {
            create_or_edit_button.setText("Создать");
            // Создание объекта студента с заполненными данными
        }
    }

    // получение данных
    @FXML
    void clear(MouseEvent event) {
        fio_field.clear();
        group_box.getSelectionModel().clearSelection();
        year_box.getSelectionModel().clearSelection();
        month_box.getSelectionModel().clearSelection();
        day_box.getSelectionModel().clearSelection();
        place_box.clear();
        number_box.clear();
    }


    // создание нового или сохранение данных об уже существующем студенте:
    @FXML
    void create_or_edite(MouseEvent event) throws SQLException
    {
        String fio = fio_field.getText().trim();
        String gender = ((RadioButton)gender_toggle_group.getSelectedToggle()).getText();
        int id_group = (new Group(group_box.getSelectionModel().getSelectedItem())).getDbID();
        String date_of_birth = year_box.getSelectionModel().getSelectedItem() + "-" + month_box.getSelectionModel().getSelectedItem() + "-" + day_box.getSelectionModel().getSelectedItem();
        String place = place_box.getText().trim();
        String number = number_box.getText().trim();
        Student student1 = new Student(fio, gender, id_group, date_of_birth, place, number);
        // если такая запись уже существует в БД:
        if (is_create)
        {
            if (!student1.create())
            {
                Alert alert = new Alert(Alert.AlertType.ERROR); // Тип: Ошибка
                alert.setTitle("Ошибка"); // Заголовок окна
                alert.setHeaderText(null); // Заголовок сообщения (можно задать или оставить пустым)
                alert.setContentText("Такой студент уже существует"); // Основной текст сообщения
                alert.showAndWait(); // Отобразить и ждать закрытия
            }
        }
        else
        {
            student1.setID(student.getDbID());
            if (!student1.update())
            {
                Alert alert = new Alert(Alert.AlertType.ERROR); // Тип: Ошибка
                alert.setTitle("Ошибка"); // Заголовок окна
                alert.setHeaderText(null); // Заголовок сообщения (можно задать или оставить пустым)
                alert.setContentText("Такой студент уже существует"); // Основной текст сообщения
                alert.showAndWait(); // Отобразить и ждать закрытия
            }
        }

        // необходима обновление страницы:
        TabStudentController.reloadStudTable();
    }
}
