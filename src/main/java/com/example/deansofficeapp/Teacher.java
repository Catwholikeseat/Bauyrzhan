package com.example.deansofficeapp;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

public class Teacher
{
    int id;
    String surname;
    String name;
    String patronymic;
    String date_of_birth;
    String place_of_residence;
    String number_of_phone;
    int work_experience;

    Teacher()
    {

    }


    // Конструктор где, полное имя задаётся в виде отельных строк:
    public Teacher(String surname, String name, String patronymic, String date_of_birth, String place_of_residence, String number_of_phone, int work_experience) throws SQLException
    {
        this.id = getDbID();
        this.surname = surname;
        this.name = name;
        this.patronymic = patronymic;
        this.date_of_birth = date_of_birth;
        this.place_of_residence = place_of_residence;
        this.number_of_phone = number_of_phone;
        this.work_experience = work_experience;
    }

    // Конструктор где, полное имя задаётся в виде отдной строки:
    public Teacher(String full_name, String date_of_birth, String place_of_residence, String number_of_phone, int work_experience) throws SQLException
    {
        this.date_of_birth = date_of_birth;
        this.place_of_residence = place_of_residence;
        this.number_of_phone = number_of_phone;
        this.work_experience = work_experience;

        // Выделение из ФИО имени, фамилии, отчества:
        full_name = full_name.trim();
        String[] FIO = full_name.split(" ");
        this.surname = FIO[0];
        this.name = FIO[1];
        this.patronymic = FIO[2];
    }

    // Возвращает ID объекта:
    public int getLocalID()
    {
        return id;
    }

    // Возвращает ID соответствующей записи с БД:
    public int getDbID() throws SQLException
    {
        String sql = "SELECT id_teacher " +
                "FROM Teacher " +
                "WHERE teacher_surname = ? AND " +
                "teacher_name = ? AND " +
                "teacher_patronymic_name = ? AND " +
                "date_of_birth = ? AND " +
                "place_of_residence = ? AND " +
                "number_of_phone = ? AND " +
                "work_experience = ?";

        PreparedStatement ps = DB.connection.prepareStatement(sql);
        ps.setString(1, surname);
        ps.setString(2, name);
        ps.setString(3, patronymic);
        ps.setString(4, date_of_birth);
        ps.setString(5, place_of_residence);
        ps.setString(6, number_of_phone);
        ps.setInt(7, work_experience);
        ResultSet rs = ps.executeQuery();
        if (rs.next())
        {
            return rs.getInt("id_teacher");
        }
        return 0;   // означает что в БД нет такой записи
    }

    public static int getDBidUsingFIO(String fio) throws SQLException
    {
        String sql = "SELECT id_teacher FROM Teacher WHERE " +
                "CONCAT(teacher_surname, ' ', teacher_name, ' ', teacher_patronymic_name) = ?";
        PreparedStatement ps = DB.connection.prepareStatement(sql);
        ps.setString(1, fio);
        ResultSet rs = ps.executeQuery();
        if (rs.next())
        {
            return rs.getInt("id_teacher");
        }
        return 0;
    }

    public void setID(int id)
    {
        this.id = id;
    }


    public String getSurname()
    {
        return surname;
    }

    public void setSurname(String surname)
    {
        this.surname = surname;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getPatronymic()
    {
        return patronymic;
    }

    public void setPatronymic(String patronymic)
    {
        this.patronymic = patronymic;
    }

    public String getDate_of_birth()
    {
        return date_of_birth;
    }

    public void setDate_of_birth(String date_of_birth)
    {
        this.date_of_birth = date_of_birth;
    }

    public String getPlace_of_residence()
    {
        return place_of_residence;
    }

    public void setPlace_of_residence(String place_of_residence)
    {
        this.place_of_residence = place_of_residence;
    }

    public String getNumber_of_phone()
    {
        return number_of_phone;
    }

    public void setNumber_of_phone(String number_of_phone)
    {
        this.number_of_phone = number_of_phone;
    }

    public int getWork_experience()
    {
        return work_experience;
    }

    public void setWork_experience(int work_experience)
    {
        this.work_experience = work_experience;
    }

    // CRUD операции:

    // Create:
    public boolean create() throws SQLException
    {
        id = this.getDbID();
        // Если объект не существует в БД, нужно его добавить:
        if (id == 0)
        {
            String sql = "INSERT INTO Teacher(teacher_surname," +
                    "teacher_name, " +
                    "teacher_patronymic_name, " +
                    "date_of_birth, " +
                    "place_of_residence, " +
                    "number_of_phone, " +
                    "work_experience) VALUES (?,?,?,?,?,?,?)";

            PreparedStatement ps = DB.connection.prepareStatement(sql);
            ps.setString(1, surname);
            ps.setString(2, name);
            ps.setString(3, patronymic);
            ps.setString(4, date_of_birth);
            ps.setString(5, place_of_residence);
            ps.setString(6, number_of_phone);
            ps.setInt(7, work_experience);
            ps.executeUpdate();

            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean update() throws SQLException
    {
        // Если объект с заданным id существует
        // Проверка соответствия записи в БД объекту
        Teacher temp = this.read();     // получение записи из БД по id
        if (temp != null)         // если объекты не равны между собой
        {
            // необходимо обновить информацию в БД
            String sql = "UPDATE Teacher SET " +
                    "teacher_surname = ?," +
                    "teacher_name = ?," +
                    "teacher_patronymic_name = ?," +
                    "date_of_birth = ?," +
                    "place_of_residence = ?," +
                    "number_of_phone = ?," +
                    "work_experience = ?" +
                    "WHERE id_teacher = ?";

            PreparedStatement ps = DB.connection.prepareStatement(sql);
            ps.setString(1, surname);
            ps.setString(2, name);
            ps.setString(3, patronymic);
            ps.setString(4, date_of_birth);
            ps.setString(5, place_of_residence);
            ps.setString(6, number_of_phone);
            ps.setInt(7, work_experience);
            ps.setInt(8, id);
            ps.executeUpdate();

            return true;
        }
        else
        {
            return false;
        }
    }

    // Read (метод возвращает объект типа Teacher):
    Teacher read() throws SQLException
    {
        Teacher temp = null;
        // Объект существует в БД:
        if (id != 0)
        {
            String sql = "SELECT * FROM Teacher WHERE id_teacher = ?";

            PreparedStatement ps = DB.connection.prepareStatement(sql);
            ps.setInt(1, this.id);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
            {
                temp = new Teacher();
                temp.surname = rs.getString("teacher_surname");
                temp.name = rs.getString("teacher_name");
                temp.patronymic = rs.getString("teacher_patronymic_name");
                temp.date_of_birth = rs.getString("date_of_birth");
                temp.place_of_residence = rs.getString("place_of_residence");
                temp.number_of_phone = rs.getString("number_of_phone");
                temp.work_experience = rs.getInt("work_experience");
            }
        }

        return temp;
    }

    // Delete:
    void delete() throws SQLException
    {
        // если объект есть в БД:
        if (id != 0)
        {
            String sql = "DELETE FROM Teacher WHERE id_teacher = ?";
            PreparedStatement ps = DB.connection.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            id = 0;
        }
    }

    // метод извлекает объект из записи БД:
    public static Teacher getObjFromRS(ResultSet rs) throws SQLException
    {
        Teacher teacher = null;
        if (rs != null)
        {
            teacher = new Teacher();
            teacher.name = rs.getString("teacher_name");
            teacher.surname = rs.getString("teacher_surname");
            teacher.patronymic = rs.getString("teacher_patronymic_name");
            teacher.date_of_birth = rs.getString("date_of_birth");
            teacher.place_of_residence = rs.getString("place_of_residence");
            teacher.number_of_phone = rs.getString("number_of_phone");
            teacher.work_experience = rs.getInt("work_experience");
        }

        return teacher;
    }


    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Teacher teacher = (Teacher) o;
        return id == teacher.id && work_experience == teacher.work_experience && Objects.equals(surname, teacher.surname) && Objects.equals(name, teacher.name) && Objects.equals(patronymic, teacher.patronymic) && Objects.equals(date_of_birth, teacher.date_of_birth) && Objects.equals(place_of_residence, teacher.place_of_residence) && Objects.equals(number_of_phone, teacher.number_of_phone);
    }

    public SimpleStringProperty getSimpleTeacherFIO()
    {
        return new SimpleStringProperty(name + " " + surname + " " + patronymic);
    }

    public SimpleStringProperty getSimpleTeacherBirth()
    {
        return new SimpleStringProperty(date_of_birth);
    }

    public SimpleStringProperty  getSimpleTeacherExperience()
    {
        return new SimpleStringProperty(String.valueOf(work_experience));
    }

    public SimpleStringProperty  getSimpleTeacherNumber()
    {
        return new SimpleStringProperty(number_of_phone);
    }

    public SimpleStringProperty getSimpleTeacherPlace()
    {
        return new SimpleStringProperty(place_of_residence);
    }
}
