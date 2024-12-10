package com.example.deansofficeapp;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.RandomAccess;

public class TabRatingController {

    @FXML
    private Button but_clear;

    @FXML
    private Button but_show_rating;

    @FXML
    private TableColumn<RatingTableItem, String> col_1;

    @FXML
    private TableColumn<RatingTableItem, String> col_10;

    @FXML
    private TableColumn<RatingTableItem, String> col_11;

    @FXML
    private TableColumn<RatingTableItem, String> col_12;

    @FXML
    private TableColumn<RatingTableItem, String> col_13;

    @FXML
    private TableColumn<RatingTableItem, String> col_14;

    @FXML
    private TableColumn<RatingTableItem, String> col_15;

    @FXML
    private TableColumn<RatingTableItem, String> col_2;

    @FXML
    private TableColumn<RatingTableItem, String> col_3;

    @FXML
    private TableColumn<RatingTableItem, String> col_4;

    @FXML
    private TableColumn<RatingTableItem, String> col_5;

    @FXML
    private TableColumn<RatingTableItem, String> col_6;

    @FXML
    private TableColumn<RatingTableItem, String> col_7;

    @FXML
    private TableColumn<RatingTableItem, String> col_8;

    @FXML
    private TableColumn<RatingTableItem, String> col_9;

    @FXML
    private ComboBox<String> combo_group;

    @FXML
    private ComboBox<String> combo_student;

    @FXML
    private TableColumn<RatingTableItem, String> discipline_col;

    @FXML
    private TableColumn<RatingTableItem, String> teacher_col;

    @FXML
    private TableColumn<RatingTableItem, String> type_col;

    @FXML
    private TableView<RatingTableItem> rating_table;

    public ComboBox<String> getCombo_group()
    {
        return combo_group;
    }

    public ComboBox<String> getCombo_student()
    {
        return combo_student;
    }

    // массив оценок
    private ArrayList<TableColumn<RatingTableItem, String>> rating_array;

    @FXML
    void initialize()
    {
        // массив столбцов оценок
        rating_array = new ArrayList<>();
        rating_array.add(col_1);
        rating_array.add(col_2);
        rating_array.add(col_3);
        rating_array.add(col_4);
        rating_array.add(col_5);
        rating_array.add(col_6);
        rating_array.add(col_7);
        rating_array.add(col_8);
        rating_array.add(col_9);
        rating_array.add(col_10);
        rating_array.add(col_11);
        rating_array.add(col_12);
        rating_array.add(col_13);
        rating_array.add(col_14);
        rating_array.add(col_15);

        // установка обработчика ячеек таблицы оценок
        discipline_col.setCellValueFactory(new PropertyValueFactory<>("discipline"));
        type_col.setCellValueFactory(new PropertyValueFactory<>("type_of_rating"));
        teacher_col.setCellValueFactory(new PropertyValueFactory<>("teacher_name"));
        for (int i = 1; i <= 15; ++i)
        {
            rating_array.get(i - 1).setCellValueFactory(new Callback<TableColumn.CellDataFeatures<RatingTableItem, String>, ObservableValue<String>>()
            {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<RatingTableItem, String> param)
                {
                    return param.getValue().getSimpleRating(Integer.parseInt(param.getTableColumn().getText()) - 1);
                }
            });
            // значение ячеек должны быть изменяемыми
            rating_array.get(i - 1).setCellFactory(new Callback<TableColumn<RatingTableItem, String>, TableCell<RatingTableItem, String>>()
            {
                @Override
                public TableCell<RatingTableItem, String> call(TableColumn<RatingTableItem, String> param)
                {
                    return new TextFieldTableCell<>();
                }
            });
            rating_array.get(i - 1).setCellFactory(TextFieldTableCell.forTableColumn());
            // установка обработчика изменения оценок
            rating_array.get(i - 1).setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<RatingTableItem, String>>()
            {
                @Override
                public void handle(TableColumn.CellEditEvent<RatingTableItem, String> event)
                {
                    // в случае изменения ячейки с оценкой
                    // необходимо определить для какого предмета и типа занятия выставлена оценка
                    // и выполнить транзакцию в БД с изменением оценки
                    RatingTableItem currentEditingRating = event.getRowValue();  // получение объекта изменяемой оценки
                    int column_index = Integer.parseInt(event.getTableColumn().getText());
                    // получение оценки
                    SimpleStringProperty newRatingProp = new SimpleStringProperty(event.getNewValue());
                    // транзакция в БД с изменением оценки
                    // вычисление оценки по кодам
                    int newRatingNum;
                    if (newRatingProp.getValue().equals("н")) newRatingNum = -1;
                    else if (newRatingProp.getValue().equals("н.п")) newRatingNum = -2;
                    else newRatingNum = Integer.parseInt(newRatingProp.getValue());
                    try
                    {
                        // int id_student, int id_discipline, int rating, int week, String type_of_rating
                        Rating rating = new Rating(
                                Student.getDBidUsingFIO(combo_student.getSelectionModel().getSelectedItem()),
                                new Discipline(
                                        currentEditingRating.getDiscipline(),
                                        Teacher.getDBidUsingFIO(currentEditingRating.getTeacher_name())).getDbIdStrict(),
                                        newRatingNum,
                                        column_index,
                                        currentEditingRating.getType_of_rating()
                        );

                        rating.setID(rating.getDbID());     // получение id из БД
                        if (rating.getLocalID() == 0)
                        {
                            rating.create();
                        }
                        else
                        {
                            rating.update();
                        }
                    } catch (SQLException e)
                    {
                        throw new RuntimeException(e);
                    }
                    // изменение оценки в текстовом поле
                    currentEditingRating.setRating(column_index - 1, newRatingProp);
                }
            });
        }

    }

    // очищает содержимое ComboBox и таблицы
    @FXML
    void onMouseClearClicked(MouseEvent event) {
        combo_group.getSelectionModel().clearSelection();
        combo_student.getSelectionModel().clearSelection();
        rating_table.getItems().clear();
    }

    // получает из БД актуальные данные о группах и выводит их
    @FXML
    void onMouseGroupComboClicked(MouseEvent event) throws SQLException
    {
        ArrayList<String> groups = Group.getAllGroups();
        combo_group.setItems(FXCollections.observableArrayList(groups));
    }

    // получает из БД актуальные данные о студентах и выводит их
    @FXML
    void onMouseStudentComboClicked(MouseEvent event) throws SQLException
    {
        ArrayList<String> students = Student.getAllStudents();
        combo_student.setItems(FXCollections.observableArrayList(students));
    }

    // метод показывает оценки выбранного студента в виде таблицы
    public void show_ratings() throws SQLException
    {
        rating_table.getItems().clear();    // перед показом очищаем таблицу
        // отображение предметов в таблице
        // Или, если используете PreparedStatement
        PreparedStatement pstmt = DB.connection.prepareStatement(
                "SELECT name_discipline, CONCAT(teacher_surname, ' ', teacher_name, ' ', teacher_patronymic_name) AS fullName " +
                        "FROM Discipline " +
                        "JOIN Teacher " +
                        "ON Discipline.id_teacher = Teacher.id_teacher",
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY
        );
        ResultSet rs_disciplines = pstmt.executeQuery();
        String[] types = {      // типы занятий
                "Лекция",
                "Лаб. работа",
                "СРО"
        };
        String name_discipline = new String();
        String teacher_name = new String();
        ArrayList<RatingTableItem> ratings = new ArrayList<>();
        while (rs_disciplines.next())
        {
            if (name_discipline.equals(rs_disciplines.getString("name_discipline")))
            {
                continue;
            }
            name_discipline = rs_disciplines.getString("name_discipline");
            for (String type : types)
            {
                RatingTableItem temp = new RatingTableItem(name_discipline, type, teacher_name);
                // отображаем предметы по типам занятий
                ratings.add(temp);
            }
        }

        rs_disciplines.beforeFirst();
        int i = 0;
        while (rs_disciplines.next())
        {
            ratings.get(i).teacher_name = rs_disciplines.getString("fullName");
            i++;
        }

        rating_table.getItems().addAll(ratings);    // добавление элементов
        int e = 0;
        // добавление оценок по предметам
        for (RatingTableItem item : ratings)    // для каждой оценки
        {
            ArrayList<String> temp = new ArrayList<>(Collections.nCopies(15, ""));
            // получение оценок по неделям
            for (e = 1; e <= 15; ++e)
            {
                String sql = "SELECT rating FROM Rating " +
                        "WHERE id_student = ? AND " +
                        "id_discipline = ? AND " +
                        "week = ? AND type_of_rating = ?";
                Discipline dis = new Discipline(
                        item.getDiscipline(),
                        Teacher.getDBidUsingFIO(item.getTeacher_name())
                );
                PreparedStatement ps = DB.connection.prepareStatement(sql);
                ps.setInt(1, Student.getDBidUsingFIO(combo_student.getSelectionModel().getSelectedItem()));
                ps.setInt(2, dis.getDbIdStrict());
                ps.setInt(3, e);
                ps.setString(4, item.getType_of_rating());
                ResultSet rs = ps.executeQuery();
                if (rs.next())
                {
                    // неявка
                    if (rs.getInt("rating") == -1)
                    {
                        temp.set(e - 1, "н");
                    }
                    else if (rs.getInt("rating") == -2)
                    {
                        temp.set(e - 1, "н.п");
                    }
                    else
                    {
                        temp.set(e - 1, String.valueOf("rating"));
                    }
                }
            }
            item.setRatings(temp);
        }
        rating_table.getItems().clear();
        rating_table.getItems().addAll(ratings);    // добавление элементов
    }


    @FXML
    void onMouseShowClicked(MouseEvent event) throws SQLException
    {
        rating_table.getItems().clear();    // перед показом очищаем таблицу
        // отображение предметов в таблице
        // Или, если используете PreparedStatement
        PreparedStatement pstmt = DB.connection.prepareStatement(
                "SELECT name_discipline, CONCAT(teacher_surname, ' ', teacher_name, ' ', teacher_patronymic_name) AS fullName " +
                        "FROM Discipline " +
                        "JOIN Teacher " +
                        "ON Discipline.id_teacher = Teacher.id_teacher",
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY
        );
        ResultSet rs_disciplines = pstmt.executeQuery();
        String[] types = {      // типы занятий
                "Лекция",
                "Лаб. работа",
                "СРО"
        };
        String name_discipline = new String();
        String teacher_name = new String();
        ArrayList<RatingTableItem> ratings = new ArrayList<>();
        while (rs_disciplines.next())
        {
            if (name_discipline.equals(rs_disciplines.getString("name_discipline")))
            {
                continue;
            }
            name_discipline = rs_disciplines.getString("name_discipline");
            for (String type : types)
            {
                RatingTableItem temp = new RatingTableItem(name_discipline, type, teacher_name);
                // отображаем предметы по типам занятий
                ratings.add(temp);
            }
        }

        rs_disciplines.beforeFirst();
        int i = 0;
        while (rs_disciplines.next())
        {
            ratings.get(i).teacher_name = rs_disciplines.getString("fullName");
            i++;
        }

        rating_table.getItems().addAll(ratings);    // добавление элементов
        int e = 0;
        // добавление оценок по предметам
        for (RatingTableItem item : ratings)    // для каждой оценки
        {
            ArrayList<String> temp = new ArrayList<>(Collections.nCopies(15, ""));
            // получение оценок по неделям
            for (e = 1; e <= 15; ++e)
            {
                String sql = "SELECT rating FROM Rating " +
                        "WHERE id_student = ? AND " +
                        "id_discipline = ? AND " +
                        "week = ? AND type_of_rating = ?";
                Discipline dis = new Discipline(
                        item.getDiscipline(),
                        Teacher.getDBidUsingFIO(item.getTeacher_name())
                );
                PreparedStatement ps = DB.connection.prepareStatement(sql);
                ps.setInt(1, Student.getDBidUsingFIO(combo_student.getSelectionModel().getSelectedItem()));
                ps.setInt(2, dis.getDbIdStrict());
                ps.setInt(3, e);
                ps.setString(4, item.getType_of_rating());
                ResultSet rs = ps.executeQuery();
                if (rs.next())
                {
                    // неявка
                    if (rs.getInt("rating") == -1)
                    {
                        temp.set(e - 1, "н");
                    }
                    else if (rs.getInt("rating") == -2)
                    {
                        temp.set(e - 1, "н.п");
                    }
                    else
                    {
                        temp.set(e - 1, String.valueOf("rating"));
                    }
                }
            }
            item.setRatings(temp);
        }
        rating_table.getItems().clear();
        rating_table.getItems().addAll(ratings);    // добавление элементов
    }
}
