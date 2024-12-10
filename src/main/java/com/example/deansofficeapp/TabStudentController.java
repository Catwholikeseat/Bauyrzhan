package com.example.deansofficeapp;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

public class TabStudentController
{

    @FXML
    private TreeTableColumn<Student, String> birth_stud_col;

    @FXML
    private TreeTableColumn<Student, String> fio_stud_col;

    @FXML
    private TreeTableColumn<Student, String> number_stud_col;

    @FXML
    private TreeTableColumn<Student, String> place_stud_col;

    @FXML
    private TreeTableColumn<Student, String> gender_col;

    @FXML
    private TreeTableView<Student> stud_table;

    @FXML
    private ContextMenu context_stud_table;

    // Касательно групп:
    // создание группы
    // удаление группы
    // изменение группы:
    //  - добавление или удаление студентов из одной группы в другой
    @FXML
    MenuItem create_group;
    @FXML
    MenuItem delete_group;

    // Касательно студентов:
    // создание студента
    // изменение студента
    // удаление студента
    @FXML
    MenuItem create_student;
    @FXML
    MenuItem edit_student;
    @FXML
    MenuItem delete_student;

    MainController main_controller;

    TabRatingController rating_controller;

    static TreeItem<Student> root;     // корневой элемент дерева

    TabStudentController()
    {

    }

    TabStudentController(MainController main_controller, TabRatingController rating_controller)
    {
        this.main_controller = main_controller;
        this.rating_controller = rating_controller;
    }


    @FXML
    void initialize() throws SQLException
    {
        // Формирование таблицы студентов и групп:

        // Установка методов возвращающих значение для объектов класса Student в столбцы таблицы:
        fio_stud_col.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Student, String>, ObservableValue<String>>()
        {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Student, String> studentStringCellDataFeatures)
            {
                return studentStringCellDataFeatures.getValue().getValue().getSimpleFullName();
            }
        });
        number_stud_col.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Student, String>, ObservableValue<String>>()
        {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Student, String> studentStringCellDataFeatures)
            {
                return studentStringCellDataFeatures.getValue().getValue().getSimpleNumber();
            }
        });
        place_stud_col.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Student, String>, ObservableValue<String>>()
        {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Student, String> studentStringCellDataFeatures)
            {
                return studentStringCellDataFeatures.getValue().getValue().getSimplePlace();
            }
        });
        gender_col.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Student, String>, ObservableValue<String>>()
        {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Student, String> studentStringCellDataFeatures)
            {
                return studentStringCellDataFeatures.getValue().getValue().getSimpleGender();
            }
        });
        birth_stud_col.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Student, String>, ObservableValue<String>>()
        {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Student, String> studentStringCellDataFeatures)
            {
                return studentStringCellDataFeatures.getValue().getValue().getSimpleBirthDate();
            }
        });

        // Отображение информации о группах:
        // корневой элемент дерева:
        root = new TreeItem<Student>(new Student("Группы:"));
        stud_table.setRoot(root);
        stud_table.setShowRoot(false);

        reloadStudTable();          // обновление таблицы
        configureDragAndDrop();     // метод выполняет установки Drag and Drop
    }

    // обработчик события нажатия мыши на таблицу Студентов и Групп:
    @FXML
    void onMouseClickedStudTable(MouseEvent e) {
        // если была нажата первая кнопка мыши
        if (e.getButton() == MouseButton.PRIMARY)
        {
            // если было произведено нажатие на пустой элемент таблиы:
            // Проверяем, был ли клик совершен на строке
            boolean isRowClicked = stud_table.lookup(".tree-table-row:hover") != null;

            if (!isRowClicked) {
                stud_table.getSelectionModel().clearSelection(); // Снимаем выделение только если клик на пустой области
            }
        }
        // если была нажата правая кнопка мыши:
        else if (e.getButton() == MouseButton.SECONDARY)
        {
            // создание элементов MenuItem в ContextMenu:
            MenuItem create_group = new MenuItem("Создать группу");
            MenuItem delete_group = new MenuItem("Удалить группу");
            MenuItem create_student = new MenuItem("Создать студента");
            MenuItem edit_student = new MenuItem("Изменить студента");
            MenuItem delete_student = new MenuItem("Удалить студента");
            MenuItem show_ratings = new MenuItem("Посмотреть оценки студента");

            // связывание элементов MenuItem в ContextMenu с их методами обработчиками:
            create_group.setOnAction(event -> create_group_function());
            delete_group.setOnAction(event -> delete_group_function());
            create_student.setOnAction(event ->
            {
                try
                {
                    createOredit_student_function(true);
                } catch (SQLException ex)
                {
                    throw new RuntimeException(ex);
                }
            });
            edit_student.setOnAction(event ->
            {
                try
                {
                    createOredit_student_function(false);
                } catch (SQLException ex)
                {
                    throw new RuntimeException(ex);
                }
            });
            delete_student.setOnAction(event -> delete_student_function());
            show_ratings.setOnAction(event ->
            {
                try
                {
                    show_ratings();
                } catch (SQLException ex)
                {
                    throw new RuntimeException(ex);
                }
            });

            TreeItem<Student> selectedItem = stud_table.getSelectionModel().getSelectedItem();  // получение выбранного объекта
            // если был выбран объект
            if (selectedItem != null)
            {
                Student tempStudent = selectedItem.getValue();
                if (tempStudent.getGender() == null)  // при создании группы как объекта Student все поля кроме имени не задавались
                {
                    context_stud_table = new ContextMenu();
                    context_stud_table.getItems().addAll(delete_group);
                    stud_table.setContextMenu(context_stud_table);
                }
                else
                {
                    context_stud_table = new ContextMenu();
                    context_stud_table.getItems().addAll(edit_student, delete_student, show_ratings);
                    stud_table.setContextMenu(context_stud_table);
                }
            }
            else
            {
                context_stud_table = new ContextMenu();
                context_stud_table.getItems().addAll(create_student, create_group);
                stud_table.setContextMenu(context_stud_table);
            }
        }
    }

    // функция показывает оценки выбранного студента в разделе оценок
    private void show_ratings() throws SQLException
    {
        main_controller.getTabPane().getSelectionModel().select(2);  // переход в раздел оценок
        // необходимо выбрать нужного студента в нужной группе
        // обновление списков в ComboBox
        TreeItem<Student> item = stud_table.getSelectionModel().getSelectedItem();  // выбранный студент
        ArrayList<String> groups = Group.getAllGroups();
        rating_controller.getCombo_group().setItems(FXCollections.observableArrayList(groups));
        ArrayList<String> students = Student.getAllStudents();
        rating_controller.getCombo_student().setItems(FXCollections.observableArrayList(students));
        // выбор студента и группы в ComboBox
        rating_controller.getCombo_student().getSelectionModel().select(item.getValue().getSimpleFullName().getValue());
        rating_controller.getCombo_group().getSelectionModel().select(item.getValue().getNameStudent());
        // после выбора необходимо отобразить оценки студента
        rating_controller.show_ratings();
    }


    // функция создаёт пустую группу без студентов:
    private void create_group_function()
    {
        // Создаем диалоговое окно для ввода текста
        TextInputDialog textInputDialog = new TextInputDialog("Default Value");
        textInputDialog.setTitle("Создание группы");
        textInputDialog.setHeaderText(null);
        textInputDialog.setGraphic(null);
        textInputDialog.setContentText("Название группы:");
        textInputDialog.getEditor().setText("");

        // Отображаем диалоговое окно и ждем результата
        Optional<String> result = textInputDialog.showAndWait();

        // Создаем группу:
        result.ifPresent(input -> {
            Group group = new Group(input);
            try
            {
                // если создаваемая группа существует в БД:
                if (!group.create())
                {
                    Alert alert = new Alert(Alert.AlertType.ERROR); // Тип: Ошибка
                    alert.setTitle("Ошибка"); // Заголовок окна
                    alert.setHeaderText(null); // Заголовок сообщения (можно задать или оставить пустым)
                    alert.setContentText("Такая группа уже существует"); // Основной текст сообщения
                    alert.showAndWait(); // Отобразить и ждать закрытия
                    create_group_function();    // повторная попытка создания группы
                }
                reloadStudTable();
            } catch (SQLException e)
            {
                throw new RuntimeException(e);
            }
        });
    }

    // функция очищает и перезагружает таблицу студентов и групп:
    public static void reloadStudTable() throws SQLException
    {
        root.getChildren().clear();
        ResultSet groups = Group.getAllGroupAndStudent();  // объекты всех групп и студентов в БД
        // отображение данных в таблице:
        int current_id = 0;                                             // id рассматриваемой группы
        int temp_id = 1;                                                // текущий id записи
        // имена групп в столбцах:
        ArrayList<TreeItem<Student>> groupNames = new ArrayList<TreeItem<Student>>();
        Student tempStudent;
        while (groups.next())
        {
            temp_id = groups.getInt("id_group");
            // если рассматриваемая группа это текущая группа
            if (current_id != temp_id)
            {
                current_id = temp_id;
                groupNames.add(new TreeItem<Student>(new Student(groups.getString("name_group"))));
                root.getChildren().add(groupNames.getLast());
            }

                if (groups.getString("name_student") != null)
                {
                    // отображаем студента внутри группы
                    tempStudent = new Student(
                            groups.getString("surname_student") + " " +
                                    groups.getString("name_student") + " " + groups.getString("patronymic_name"),
                            groups.getString("gender"),
                            groups.getInt("id_group"),
                            groups.getString("date_of_birth"),
                            groups.getString("place_of_residence"),
                            groups.getString("number_of_phone")
                    );
                    groupNames.getLast().getChildren().add(
                            new TreeItem<Student>(tempStudent)
                    );
                    groupNames.getLast().setExpanded(true);
                }
        }
    }

    // функция открывает окно для создания или изменения данных о студентах:
    @FXML
    private void openCreateOrEditStudent(boolean is_create, Student student)
    {
        try
        {
            // Создаём экземпляр второго контроллера с параметрами
            CreateOrEditStudentController controller = new CreateOrEditStudentController(student, is_create);

            // Загружаем FXML с этим контроллером
            FXMLLoader loader = new FXMLLoader(getClass().getResource("createOredit_student_view.fxml"));
            loader.setController(controller); // Устанавливаем контроллер вручную

            Parent root = loader.load(); // Загружаем интерфейс

            // Открываем второе окно
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Создание или изменение студента");
            stage.show();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    // удаление группы:
    private void delete_group_function()
    {
        TreeItem<Student> selectedItem = stud_table.getSelectionModel().getSelectedItem();  // получение выбранного объекта
        if (selectedItem != null)
        {
            Student tempStudent = selectedItem.getValue();      // получение объекта Student из TreeItem
            // если выбранный элемент не группа
            if (tempStudent.getSurname() != null)
            {
                return;
            }
            try
            {
                Group group = new Group(tempStudent.getNameStudent());
                group.delete();                                     // удаление группы со всеми его студентами
                reloadStudTable();                                  // обновление таблицы
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }

    // создание или изменение студента:
    private void createOredit_student_function(boolean is_create) throws SQLException
    {
        Student student = null;
        // получение выбранного объекта
        TreeItem<Student> selected_item = stud_table.getSelectionModel().getSelectedItem();
        if (selected_item != null)
        {
            student = selected_item.getValue();
            student.setID(student.getDbID());
        }

        openCreateOrEditStudent(is_create, student);
    }

    // удаление студента:
    private void delete_student_function()
    {
        TreeItem<Student> selectedItem = stud_table.getSelectionModel().getSelectedItem();  // получение выбранного объекта
        if (selectedItem != null)
        {
            Student tempStudent = selectedItem.getValue();      // получение объекта Student из TreeItem
            try
            {
                tempStudent.setID(tempStudent.getDbID());           // получение id для соответствующей записи в БД
                tempStudent.delete();                               // удаление студента из БД
                reloadStudTable();                                  // обновление таблицы
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void configureDragAndDrop() {
        // Создаем DataFormat для передачи объекта Person
        DataFormat personDataFormat = new DataFormat("application/student");
        stud_table.setRowFactory(tv -> {
                    TreeTableRow<Student> row = new TreeTableRow<>();

                    // Начало Drag
                    row.setOnDragDetected(event -> {
                        if (!row.isEmpty() && isStudent(row.getTreeItem())) { // Перетаскивание только студентов
                            TreeItem<Student> draggedItem = row.getTreeItem();
                            Dragboard db = row.startDragAndDrop(TransferMode.MOVE);

                            ClipboardContent content = new ClipboardContent();

                            // Передаем имя объекта
                            content.putString(draggedItem.getValue().getNameStudent());
                            content.put(personDataFormat, draggedItem.getValue()); // Передаем сам объект Person
                            db.setContent(content);

                            row.setUserData(draggedItem);
                            event.consume();
                        }
                    });

                    // Drag over
                    row.setOnDragOver(event -> {
                        if (event.getGestureSource() != row && event.getDragboard().hasString()) {
                            TreeItem<Student> targetItem = row.getTreeItem();

                            // Разрешить перенос только в корневые элементы (группы)
                            if (isGroup(targetItem)) {
                                event.acceptTransferModes(TransferMode.MOVE);
                            }
                        }
                        event.consume();
                    });

            row.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                boolean success = false;

                // Получаем объект Person из Dragboard
                Student draggedPerson = (Student) db.getContent(personDataFormat);

                if (draggedPerson != null && row.getTreeItem() != null) {
                    TreeItem<Student> targetGroup = row.getTreeItem();

                    if (draggedPerson != null && isGroup(targetGroup)) {
                        // Находим старый родительский элемент
                        TreeItem<Student> oldParent = (TreeItem<Student>) row.getUserData();

                        // Удаляем старый элемент из его родительского элемента
                        if (oldParent != null) {
                            oldParent.getChildren().remove(draggedPerson);
                        }

                        // Добавляем новый элемент в целевую группу
                        TreeItem<Student> newItem = new TreeItem<>(draggedPerson);
                        targetGroup.getChildren().add(newItem);

                        // После перемещения необходимо произвести транзакцию в БД
                        // и перезагрузить таблицу
                        Student tempStudent = newItem.getValue();
                        Group tempGroup = new Group(targetGroup.getValue().getNameStudent());
                        try
                        {
                            // удаление старого студента:
                            tempStudent.setID(tempStudent.getDbID());
                            tempStudent.delete();
                            // изменение группы студента:
                            tempStudent.setId_group(tempGroup.getDbID());
                            tempStudent.create();
                            // обновление таблицы:
                            reloadStudTable();
                        } catch (SQLException e)
                        {
                            throw new RuntimeException(e);
                        }

                        success = true;
                    }
                }
                event.setDropCompleted(success);
                event.consume();
            });

                    return row;
        });
    }

    // Проверяет, является ли элемент группой (корневой элемент)
    private boolean isGroup(TreeItem<Student> item) {
        Student student = item.getValue();
        return student.surname == null;
    }

    // Проверяет, является ли элемент студентом (дочерний элемент)
    private boolean isStudent(TreeItem<Student> item) {
        Student student = item.getValue();
        return student.surname != null;
    }
}

