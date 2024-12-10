package com.example.deansofficeapp;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javafx.scene.input.MouseEvent;

public class TabTeacherController {

    @FXML
    private TreeTableColumn<Teacher, String> birth_teacher_col;

    @FXML
    private TreeTableColumn<Teacher, String> experience_teacher_col;

    @FXML
    private TreeTableColumn<Teacher, String> fio_teacher_col;

    @FXML
    private TreeTableColumn<Teacher, String> number_teacher_col;

    @FXML
    private TreeTableColumn<Teacher, String> place_teacher_col;

    @FXML
    private TreeTableView<Teacher> teacher_table;

    @FXML
    private ContextMenu context_teacher_table;

    static TreeItem<Teacher> root;     // корневой элемент дерева

    @FXML
    void initialize() throws SQLException
    {
        // Формирование таблицы преподавателей:
        // Установка методов возвращающих значение для объектов класса Teacher в столбцы таблицы:

        fio_teacher_col.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Teacher, String>, ObservableValue<String>>()
        {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Teacher, String> param)
            {
                return param.getValue().getValue().getSimpleTeacherFIO();
            }
        });
        birth_teacher_col.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Teacher, String>, ObservableValue<String>>()
        {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Teacher, String> param)
            {
                return param.getValue().getValue().getSimpleTeacherBirth();
            }
        });
        experience_teacher_col.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Teacher, String>, ObservableValue<String>>()
        {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Teacher, String> param)
            {
                return param.getValue().getValue().getSimpleTeacherExperience();
            }
        });
        number_teacher_col.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Teacher, String>, ObservableValue<String>>()
        {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Teacher, String> param)
            {
                return param.getValue().getValue().getSimpleTeacherNumber();
            }
        });
        place_teacher_col.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Teacher, String>, ObservableValue<String>>()
        {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Teacher, String> param)
            {
                return param.getValue().getValue().getSimpleTeacherPlace();
            }
        });

        // корневой элемент дерева:
        root = new TreeItem<Teacher>();
        teacher_table.setRoot(root);
        teacher_table.setShowRoot(false);

        reloadTeacherTable();          // обновление таблицы
    }

    // метод обновляет содержимое таблицы учителей в соответствии с данными в БД:
    public static void reloadTeacherTable() throws SQLException
    {
        root.getChildren().clear();         // очистка таблицы
        // Получение всех записей о преподавателях из БД:
        String sql = "SELECT * FROM Teacher";
        PreparedStatement statement = DB.connection.prepareStatement(sql);
        ResultSet rs = statement.executeQuery();

        // отображение данных в таблице:
        while (rs.next())
        {
            root.getChildren().add(new TreeItem<Teacher>(Teacher.getObjFromRS(rs)));
            root.setExpanded(true);
        }
    }


    @FXML
    void onMouseClickedTeacherTable(MouseEvent e)
    {
        // если была нажата первая кнопка мыши
        if (e.getButton() == MouseButton.PRIMARY)
        {
            // если было произведено нажатие на пустой элемент таблиы:
            // Проверяем, был ли клик совершен на строке
            boolean isRowClicked = teacher_table.lookup(".tree-table-row:hover") != null;

            if (!isRowClicked) {
                teacher_table.getSelectionModel().clearSelection(); // Снимаем выделение только если клик на пустой области
            }
        }
        // если была нажата правая кнопка мыши:
        else if (e.getButton() == MouseButton.SECONDARY)
        {
            // создание элементов MenuItem в ContextMenu:
            MenuItem create_teacher = new MenuItem("Создать преподавателя");
            MenuItem delete_teacher = new MenuItem("Удалить преподавателя");
            MenuItem edit_teacher = new MenuItem("Изменить преподавателя");

            // связывание элементов MenuItem в ContextMenu с их методами обработчиками:
            create_teacher.setOnAction(event ->
            {
                try
                {
                    createOrEdit_teacher(true);
                } catch (SQLException ex)
                {
                    throw new RuntimeException(ex);
                }
            });
            delete_teacher.setOnAction(event -> delete_teacher());
            edit_teacher.setOnAction(event ->
            {
                try
                {
                    createOrEdit_teacher(false);
                } catch (SQLException ex)
                {
                    throw new RuntimeException(ex);
                }
            });

            TreeItem<Teacher> selectedItem = teacher_table.getSelectionModel().getSelectedItem();  // получение выбранного объекта
            // если был выбран объект
            if (selectedItem != null)
            {
                Teacher tempTeacher = selectedItem.getValue();
                context_teacher_table = new ContextMenu();
                context_teacher_table.getItems().addAll(edit_teacher, delete_teacher);
                teacher_table.setContextMenu(context_teacher_table);
            }
            else
            {
                context_teacher_table = new ContextMenu();
                context_teacher_table.getItems().addAll(create_teacher);
                teacher_table.setContextMenu(context_teacher_table);
            }
        }
    }

    // метод удаляет преподавателя из таблицы и БД:
    private void delete_teacher()
    {
        TreeItem<Teacher> selectedItem = teacher_table.getSelectionModel().getSelectedItem();  // получение выбранного объекта
        if (selectedItem != null)
        {
            Teacher tempTeacher = selectedItem.getValue();      // получение объекта Student из TreeItem
            try
            {
                tempTeacher.setID(tempTeacher.getDbID());           // получение id для соответствующей записи в БД
                tempTeacher.delete();                               // удаление студента из БД
                reloadTeacherTable();                                  // обновление таблицы
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }

    // метод создаёт или изменяет информацию о преподавателе:
    private void createOrEdit_teacher(boolean is_create) throws SQLException
    {
        Teacher teacher = null;
        // получение выбранного объекта
        TreeItem<Teacher> selected_item = teacher_table.getSelectionModel().getSelectedItem();
        if (selected_item != null)
        {
            teacher = selected_item.getValue();
            teacher.setID(teacher.getDbID());
        }

        openCreateOrEditTeacher(is_create, teacher);
    }

    // метод открывает второе окно создания или изменения данных о преподавателе:
    private void openCreateOrEditTeacher(boolean is_create, Teacher teacher)
    {
        try
        {
            // Создаём экземпляр второго контроллера с параметрами
            CreateOrEditTeacherController controller = new CreateOrEditTeacherController(is_create, teacher);

            // Загружаем FXML с этим контроллером
            FXMLLoader loader = new FXMLLoader(getClass().getResource("createOredit_teacher_view.fxml"));
            loader.setController(controller); // Устанавливаем контроллер вручную

            Parent root = loader.load(); // Загружаем интерфейс

            // Открываем второе окно
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Создание или изменение преподавателя");
            stage.show();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
