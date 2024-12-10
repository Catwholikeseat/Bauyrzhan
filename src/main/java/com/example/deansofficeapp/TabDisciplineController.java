package com.example.deansofficeapp;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

public class TabDisciplineController {

    @FXML
    private ContextMenu context_discipline_table;

    @FXML
    private TreeTableColumn<String, String> name_col;

    @FXML
    private TreeTableView<String> discipline_table;

    static TreeItem<String> root;     // корневой элемент дерева

    @FXML
    void initialize() throws SQLException
    {
        name_col.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<String, String>, ObservableValue<String>>()
        {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<String, String> param)
            {
                return new SimpleStringProperty(param.getValue().getValue());
            }
        });
        // корневой элемент дерева:
        root = new TreeItem<String>();
        discipline_table.setRoot(root);
        discipline_table.setShowRoot(false);

        reloadDisciplineTable();          // обновление таблицы
    }

    public static void reloadDisciplineTable() throws SQLException
    {
        root.getChildren().clear();         // очистка таблицы
        // Получение всех записей о предметах из БД:
        String sql = "SELECT name_discipline " +
                     "FROM Discipline " +
                     "GROUP BY name_discipline";
        PreparedStatement statement = DB.connection.prepareStatement(sql);
        ResultSet rs = statement.executeQuery();

        // отображение данных в таблице:
        while (rs.next())
        {
            // отображение данных о дисциплине
            String name_discipline = rs.getString("name_discipline");
            ArrayList<TreeItem<String>> elements = new ArrayList<>();

            // отображение данных о преподавателях данной дисциплины
            String sql1 = "SELECT CONCAT(teacher_surname, ' ', teacher_name, ' ', teacher_patronymic_name) " +
                    "FROM Discipline " +
                    "JOIN Teacher " +
                    "ON Discipline.id_teacher = Teacher.id_teacher " +
                    "WHERE name_discipline = ?";
            PreparedStatement statement1 = DB.connection.prepareStatement(sql1);
            statement1.setString(1, name_discipline);
            ResultSet rs1 = statement1.executeQuery();

            while (rs1.next())
            {
                elements.add(new TreeItem<String>(rs1.getString(1)));
            }

            root.getChildren().add(new TreeItem<String>(name_discipline));
            root.getChildren().getLast().getChildren().addAll(elements);
            root.getChildren().getLast().setExpanded(true);
            root.setExpanded(true);
        }
    }

    @FXML
    void onMouseClickedDisciplineTeacher(MouseEvent e)
    {
        // если была нажата первая кнопка мыши
        if (e.getButton() == MouseButton.PRIMARY)
        {
            // если было произведено нажатие на пустой элемент таблиы:
            // Проверяем, был ли клик совершен на строке
            boolean isRowClicked = discipline_table.lookup(".tree-table-row:hover") != null;

            if (!isRowClicked) {
                discipline_table.getSelectionModel().clearSelection(); // Снимаем выделение только если клик на пустой области
            }
        }
        // если была нажата правая кнопка мыши:
        else if (e.getButton() == MouseButton.SECONDARY)
        {
            // создание элементов MenuItem в ContextMenu:
            MenuItem create_discipline = new MenuItem("Создать предмет");
            MenuItem delete_discipline = new MenuItem("Удалить предмет");
            MenuItem edit_discipline = new MenuItem("Изменить предмет");

            // связывание элементов MenuItem в ContextMenu с их методами обработчиками:
            create_discipline.setOnAction(event ->
            {
                create_or_edit_discipline(true);
            });
            delete_discipline.setOnAction(event -> delete_discipline());
            edit_discipline.setOnAction(event ->
            {
                create_or_edit_discipline(false);
            });

            TreeItem<String> selectedItem = discipline_table.getSelectionModel().getSelectedItem();  // получение выбранного объекта
            // если был выбран объект и этот объект это дисциплина
            if (selectedItem != null && selectedItem.getParent() == root)
            {
                String tempTeacher = selectedItem.getValue();
                context_discipline_table = new ContextMenu();
                context_discipline_table.getItems().addAll(edit_discipline, delete_discipline);
                discipline_table.setContextMenu(context_discipline_table);
            }
            else
            {
                // если была выбрана пустая область:
                context_discipline_table = new ContextMenu();
                context_discipline_table.getItems().addAll(create_discipline);
                discipline_table.setContextMenu(context_discipline_table);
            }
        }
    }

    // удаление дисциплины
    private void delete_discipline()
    {
        // необходимо удалить все записи с соответствующей записью в БД
        TreeItem<String> selectedItem = discipline_table.getSelectionModel().getSelectedItem();  // получение выбранного объекта
        if (selectedItem != null)
        {
            String temp = selectedItem.getValue();      // получение объекта Student из TreeItem
            try
            {
                String sql = "DELETE FROM Discipline WHERE name_discipline = ?";
                PreparedStatement ps = DB.connection.prepareStatement(sql);
                ps.setString(1, temp);
                ps.executeUpdate();
                reloadDisciplineTable();                             // обновление таблицы
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }

    // создание новой группы или изменение названия предмета:
    private void create_or_edit_discipline(boolean is_create)
    {
        String discipline = null;
        // получение выбранного объекта
        TreeItem<String> selected_item = discipline_table.getSelectionModel().getSelectedItem();
        if (selected_item != null)
        {
            discipline = selected_item.getValue();
        }

        openCreateOrEditTeacher(is_create, discipline);
    }

    // открывает второе окно с созданием дисциплины
    private void openCreateOrEditTeacher(boolean is_create, String discipline)
    {
        try
        {
            // Создаём экземпляр второго контроллера с параметрами
            CreateOrEditDisciplineController controller = new CreateOrEditDisciplineController(is_create, discipline);

            // Загружаем FXML с этим контроллером
            FXMLLoader loader = new FXMLLoader(getClass().getResource("createOredit_discipline_view.fxml"));
            loader.setController(controller); // Устанавливаем контроллер вручную

            Parent root = loader.load(); // Загружаем интерфейс

            // Открываем второе окно
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Создание или изменение предмета");
            stage.show();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
