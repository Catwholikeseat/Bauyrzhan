package com.example.deansofficeapp;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class CreateOrEditDisciplineController
{
    private boolean is_create;
    private String discipline;

    @FXML
    private Button add_button;

    @FXML
    private Button clear_button;

    @FXML
    private Button delete_button;

    @FXML
    private Button create_or_edit_but;

    @FXML
    private ComboBox<String> discipline_combo;

    @FXML
    private TextField name_discipline_field;

    CreateOrEditDisciplineController(boolean is_create, String discipline)
    {
        this.is_create = is_create;
        this.discipline = discipline;
    }

    @FXML
    void initialize() throws SQLException
    {
        // при инициализации необходимо заполнить начальные данные:
        // если окно открыто для редактирования существующего предмета
        if (!is_create)
        {
            // необходимо отобразить имя дисциплины
            name_discipline_field.setText(discipline);
            create_or_edit_but.setText("Сохранить изменения");
        }
        else
        {
            // если окно открыто для создания предмета
            // блокируем кнопки
            add_button.setDisable(true);
            delete_button.setDisable(true);
            // блокируем ComboBox
            discipline_combo.setDisable(true);
            create_or_edit_but.setText("Создать");
        }

        // необходимо добавить данные преподавателей в ComboBox
        // получение данных о всех преподавателях
        String sql = "SELECT * FROM Teacher";
        PreparedStatement ps = DB.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next())
        {
            discipline_combo.getItems().add(rs.getString("teacher_surname") + " " + rs.getString("teacher_name")
                    + " " + rs.getString("teacher_patronymic_name"));
        }
    }

    // добавление преподавателя к предмету:
    @FXML
    void onAdd(MouseEvent event) throws SQLException
    {
        // в случае добавления преподавателя к предмету
        // сначала необходимо проверить существование такого преподавателя в предмете
        // если преподаватель существует вернуть сообщение об ошибке
        // в обратном случае приписать преподавателя к предмету и обновить таблицу

        // необходимо проверить существование предмета:
        discipline = name_discipline_field.getText();
        Discipline dis = new Discipline(discipline);

        // если такой предмет существует
        if (dis.getDbID() != 0)
        {
            // проверка наличия выбранного преподавателя в предмете:
            String sql = "SELECT * " +
                    "FROM Discipline " +
                    "JOIN Teacher " +
                    "ON Discipline.id_teacher = Teacher.id_teacher " +
                    "WHERE name_discipline = ? AND CONCAT(teacher_surname, ' ', teacher_name, ' ', teacher_patronymic_name) = ?";

            PreparedStatement ps = DB.connection.prepareStatement(sql);
            ps.setString(1, discipline);
            ps.setString(2, discipline_combo.getSelectionModel().getSelectedItem());
            ResultSet rs = ps.executeQuery();
            if (rs.next())
            {
                // преподаватель, который преподаёт по выбранному предмету существует:
                // необходимо вызвать сообщение об ошибке
                Alert alert = new Alert(Alert.AlertType.ERROR); // Тип: Ошибка
                alert.setTitle("Ошибка"); // Заголовок окна
                alert.setHeaderText(null); // Заголовок сообщения (можно задать или оставить пустым)
                alert.setContentText("Выбранный преподаватель уже преподаёт по этому предмету"); // Основной текст сообщения
                alert.showAndWait(); // Отобразить и ждать закрытия
                return;
            }

            // если же преподаватель не преподаёт по выбранному предмету
            // необходимо добавить в таблицу предметов новые данные
            sql = "SELECT id_teacher FROM Teacher WHERE CONCAT(teacher_surname, ' ', teacher_name, ' ', teacher_patronymic_name) = ?";
            ps = DB.connection.prepareStatement(sql);
            ps.setString(1, discipline_combo.getSelectionModel().getSelectedItem());
            rs = ps.executeQuery();
            if (rs.next())
            {
                dis = new Discipline(name_discipline_field.getText(), rs.getInt("id_teacher"));
            }
            dis.create();
            TabDisciplineController.reloadDisciplineTable();
        }
    }

    // очищает содержимое полей и ComboBox
    @FXML
    void onClear(MouseEvent event) {
        name_discipline_field.setText("");
        discipline_combo.getSelectionModel().clearSelection();
    }

    // удаление преподавателя из предмета:
    @FXML
    void onDelete(MouseEvent event) throws SQLException
    {
        // необходимо проверить существование предмета:
        discipline = name_discipline_field.getText();
        Discipline dis = new Discipline(discipline);

        // если такой предмет существует
        if (dis.getDbID() != 0)
        {
            // проверка наличия выбранного преподавателя в предмете:
            String sql = "SELECT * " +
                    "FROM Discipline " +
                    "JOIN Teacher " +
                    "ON Discipline.id_teacher = Teacher.id_teacher " +
                    "WHERE name_discipline = ? AND CONCAT(teacher_surname, ' ', teacher_name, ' ', teacher_patronymic_name) = ?";

            PreparedStatement ps = DB.connection.prepareStatement(sql);
            ps.setString(1, discipline);
            ps.setString(2, discipline_combo.getSelectionModel().getSelectedItem());
            ResultSet rs = ps.executeQuery();
            if (!rs.next())
            {
                // преподаватель, который преподаёт по выбранному предмету не существует:
                // необходимо вызвать сообщение об ошибке
                Alert alert = new Alert(Alert.AlertType.ERROR); // Тип: Ошибка
                alert.setTitle("Ошибка"); // Заголовок окна
                alert.setHeaderText(null); // Заголовок сообщения (можно задать или оставить пустым)
                alert.setContentText("Выбранный преподаватель не преподаёт по этому предмету"); // Основной текст сообщения
                alert.showAndWait(); // Отобразить и ждать закрытия
                return;
            }
            // если же преподаватель не преподаёт по выбранному предмету
            // необходимо добавить в таблицу предметов новые данные
            sql = "SELECT id_teacher FROM Teacher WHERE CONCAT(teacher_surname, ' ', teacher_name, ' ', teacher_patronymic_name) = ?";
            ps = DB.connection.prepareStatement(sql);
            ps.setString(1, discipline_combo.getSelectionModel().getSelectedItem());
            rs = ps.executeQuery();
            if (rs.next())
            {
                dis = new Discipline(name_discipline_field.getText(), rs.getInt("id_teacher"));
            }
            // если же преподаватель преподаёт по выбранному предмету
            // необходимо удалить из таблицы предметов данные
            dis = new Discipline(discipline, rs.getInt("id_teacher"));
            System.out.println(dis.toString());
            dis.delete();
            TabDisciplineController.reloadDisciplineTable();
        }
    }

    @FXML
    void onCreateOrEdit(MouseEvent event) throws SQLException
    {
        // получение имени дисциплины из текстового поля
        if (is_create)       // если окно открыто для создания предмета
        {
            Discipline temp = new Discipline(name_discipline_field.getText());
            // создание дисциплины
            if (!temp.create())
            {
                Alert alert = new Alert(Alert.AlertType.ERROR); // Тип: Ошибка
                alert.setTitle("Ошибка"); // Заголовок окна
                alert.setHeaderText(null); // Заголовок сообщения (можно задать или оставить пустым)
                alert.setContentText("Такой предмет уже существует"); // Основной текст сообщения
                alert.showAndWait(); // Отобразить и ждать закрытия
            }
        }
        else                 // если окно открыто для изменения существующего предмета
        {
            String sql = "UPDATE Discipline SET name_discipline = ? WHERE name_discipline = ?";
            PreparedStatement ps = DB.connection.prepareStatement(sql);
            ps.setString(1, name_discipline_field.getText());
            ps.setString(2, discipline);
            ps.executeUpdate();
        }

        TabDisciplineController.reloadDisciplineTable();            // перезагрузка таблицы
        // закрытие окна
        Stage stage = (Stage) add_button.getScene().getWindow();
        stage.close();
    }
}
