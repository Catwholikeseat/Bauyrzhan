package com.example.deansofficeapp;

import javafx.beans.property.SimpleStringProperty;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

public class Student implements Serializable
{
    int id;
    String surname;
    String name;
    String patronymic;
    String gender;
    int id_group;
    String date_of_birth;
    String place_of_residence;
    String number_of_phone;

    Student()
    {

    }

    // Конструктор где, полное имя задаётся в виде отельных строк:
    public Student(String surname, String name, String patronymic, String gender, int id_group, String date_of_birth, String place_of_residence, String number_of_phone) throws SQLException
    {
        this.id = getDbID();
        this.surname = surname;
        this.name = name;
        this.patronymic = patronymic;
        this.gender = gender;
        this.id_group = id_group;
        this.date_of_birth = date_of_birth;
        this.place_of_residence = place_of_residence;
        this.number_of_phone = number_of_phone;
    }

    // Конструктор где, полное имя задаётся в виде одной строки:
    public Student(String full_name, String gender, int id_group, String date_of_birth, String place_of_residence, String number_of_phone) throws SQLException
    {
        // Выделение из ФИО имени, фамилии, отчества:
        full_name = full_name.trim();
        String[] FIO = full_name.split(" ");
        this.surname = FIO[0];
        this.name = FIO[1];
        this.patronymic = FIO[2];

        this.gender = gender;
        this.id_group = id_group;
        this.date_of_birth = date_of_birth;
        this.place_of_residence = place_of_residence;
        this.number_of_phone = number_of_phone;
        if (this.name == null) this.name = "";
        if (this.surname == null) this.surname = "";
        if (this.patronymic == null) this.patronymic = "";
    }

    public Student(String name)
    {
        this.name = name;
    }

    // Возвращает ID объекта:
    public int getLocalID()
    {
        return id;
    }

    // Возвращает ID соответствующей записи с БД:
    public int getDbID() throws SQLException
    {
        String sql = "SELECT id_student " +
                "FROM Student " +
                "WHERE surname_student = ? AND " +
                "name_student = ? AND " +
                "patronymic_name = ? AND " +
                "gender = ? AND " +
                "id_group = ? AND " +
                "date_of_birth = ? AND " +
                "place_of_residence = ? AND " +
                "number_of_phone = ?";

        PreparedStatement ps = DB.connection.prepareStatement(sql);
        ps.setString(1, surname);
        ps.setString(2, name);
        ps.setString(3, patronymic);
        ps.setString(4, gender);
        ps.setInt(5, id_group);
        ps.setString(6, date_of_birth);
        ps.setString(7, place_of_residence);
        ps.setString(8, number_of_phone);
        ResultSet rs = ps.executeQuery();
        if (rs.next())
        {
            return rs.getInt("id_student");
        }
        return 0;   // означает что в БД нет такой записи
    }

    public static int getDBidUsingFIO(String fio) throws SQLException
    {
        String sql = "SELECT id_student FROM Student WHERE " +
                "CONCAT(surname_student, ' ', name_student, ' ', patronymic_name) = ?";
        PreparedStatement ps = DB.connection.prepareStatement(sql);
        ps.setString(1, fio);
        ResultSet rs = ps.executeQuery();
        if (rs.next())
        {
            return rs.getInt("id_student");
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

    public String getNameStudent()
    {
        return name;
    }

    public SimpleStringProperty getSimpleFullName()
    {
        if (surname == null)
        {
            return new SimpleStringProperty(name);
        }
        return new SimpleStringProperty(surname + " " + name + " " + patronymic);
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

    public String getGender()
    {
        return gender;
    }

    public SimpleStringProperty getSimpleGender()
    {
        return new SimpleStringProperty(gender);
    }

    public void setGender(String gender)
    {
        this.gender = gender;
    }

    public int getId_group()
    {
        return id_group;
    }

    public String getGroupName() throws SQLException
    {
        String sql = "SELECT name_group FROM [Group] WHERE id_group = ?";
        PreparedStatement ps = DB.connection.prepareStatement(sql);
        ps.setInt(1, id_group);
        ResultSet rs = ps.executeQuery();
        if (rs.next())
        {
            return rs.getString("name_group");
        }

        return null;
    }

    public String[] getArrayDateBirth()
    {
        String[] array = date_of_birth.split("-");
        return array;
    }

    public void setId_group(int id_group)
    {
        this.id_group = id_group;
    }

    public String getDate_of_birth()
    {
        return date_of_birth;
    }

    public SimpleStringProperty getSimpleBirthDate()
    {
        return new SimpleStringProperty(date_of_birth);
    }
    public void setDate_of_birth(String date_of_birth)
    {
        this.date_of_birth = date_of_birth;
    }

    public String getPlace_of_residence()
    {
        return place_of_residence;
    }

    public SimpleStringProperty getSimplePlace()
    {
        return new SimpleStringProperty(place_of_residence);
    }

    public void setPlace_of_residence(String place_of_residence)
    {
        this.place_of_residence = place_of_residence;
    }

    public String getNumber_of_phone()
    {
        return number_of_phone;
    }

    public SimpleStringProperty getSimpleNumber()
    {
        return new SimpleStringProperty(number_of_phone);
    }

    public void setNumber_of_phone(String number_of_phone)
    {
        this.number_of_phone = number_of_phone;
    }


    // CRUD операции:

    // Create:
    boolean create() throws SQLException
    {
        id = this.getDbID();
        // Если объект не существует в БД, нужно его добавить:
        if (id == 0)
        {
            String sql = "INSERT INTO Student(" +
                    "surname_student," +
                    "name_student," +
                    "patronymic_name," +
                    "gender," +
                    "id_group," +
                    "date_of_birth," +
                    "place_of_residence," +
                    "number_of_phone" +
                    ") VALUES (?,?,?,?,?,?,?,?)";

            PreparedStatement ps = DB.connection.prepareStatement(sql);
            ps.setString(1, surname);
            ps.setString(2, name);
            ps.setString(3, patronymic);
            ps.setString(4, gender);
            ps.setInt(5, id_group);
            ps.setString(6, date_of_birth);
            ps.setString(7, place_of_residence);
            ps.setString(8, number_of_phone);
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
        // если объект с заданным id существует в БД:
        Student temp = this.read();     // получение записи из БД по id
        if (temp != null)         // если объекты не равны между собой
        {
            // необходимо обновить информацию в БД
            String sql = "UPDATE Student SET " +
                    "surname_student = ?," +
                    "name_student = ?," +
                    "patronymic_name = ?," +
                    "gender = ?," +
                    "id_group = ?," +
                    "date_of_birth = ?," +
                    "place_of_residence = ?," +
                    "number_of_phone = ?" +
                    "WHERE id_student = ?";

            PreparedStatement ps = DB.connection.prepareStatement(sql);
            ps.setString(1, surname);
            ps.setString(2, name);
            ps.setString(3, patronymic);
            ps.setString(4, gender);
            ps.setInt(5, id_group);
            ps.setString(6, date_of_birth);
            ps.setString(7, place_of_residence);
            ps.setString(8, number_of_phone);
            ps.setInt(9, id);
            ps.executeUpdate();

            return true;
        }
        else
        {
            return false;
        }
    }

    // Read (метод возвращает объект типа Teacher):
    Student read() throws SQLException
    {
        Student temp = null;
        // Объект существует в БД:
        if (id != 0)
        {
            String sql = "SELECT * FROM Student WHERE id_student = ?";

            PreparedStatement ps = DB.connection.prepareStatement(sql);
            ps.setInt(1, this.id);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
            {
                temp = new Student();
                temp.id = id;
                temp.surname = rs.getString("surname_student");
                temp.name = rs.getString("name_student");
                temp.patronymic = rs.getString("patronymic_name");
                temp.gender = rs.getString("gender");
                temp.id_group = rs.getInt("id_group");
                temp.date_of_birth = rs.getString("date_of_birth");
                temp.place_of_residence = rs.getString("place_of_residence");
                temp.number_of_phone = rs.getString("number_of_phone");
            }
        }

        return temp;
    }

    // Delete:
    void delete() throws SQLException
    {
        this.id = getDbID();
        // если объект есть в БД:
        if (id != 0)
        {
            String sql = "DELETE FROM Student WHERE id_student = ?";
            PreparedStatement ps = DB.connection.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            id = 0;
        }
    }

    // метод получает список имен студентов
    static ArrayList<String> getAllStudents() throws SQLException
    {
        ArrayList<String> list = null;
        String sql = "SELECT * FROM Student";
        PreparedStatement ps = DB.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        if (rs == null)
        {
            return list;
        }
        list = new ArrayList<>();
        while (rs.next())
        {
            list.add(rs.getString("surname_student") + " " +
                    rs.getString("name_student") + " " +
                    rs.getString("patronymic_name"));
        }

        return list;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return id == student.id && id_group == student.id_group && Objects.equals(surname, student.surname) && Objects.equals(name, student.name) && Objects.equals(patronymic, student.patronymic) && Objects.equals(gender, student.gender) && Objects.equals(place_of_residence, student.place_of_residence) && Objects.equals(number_of_phone, student.number_of_phone);
    }

    @Override
    public String toString()
    {
        return "Student{" +
                "id=" + id +
                ", surname='" + surname + '\'' +
                ", name='" + name + '\'' +
                ", patronymic='" + patronymic + '\'' +
                ", gender='" + gender + '\'' +
                ", id_group=" + id_group +
                ", date_of_birth='" + date_of_birth + '\'' +
                ", place_of_residence='" + place_of_residence + '\'' +
                ", number_of_phone='" + number_of_phone + '\'' +
                '}';
    }
}
