package com.example.deansofficeapp;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

public class Discipline
{
    int id;
    String name_discipline;
    int id_teacher;

    Discipline()
    {

    }

    public Discipline(String name_discipline, int id_teacher)
    {
        this.name_discipline = name_discipline;
        this.id_teacher = id_teacher;
    }

    public Discipline(String name_discipline)
    {
        this.name_discipline = name_discipline;
    }

    // Возвращает ID объекта:
    public int getLocalID()
    {
        return id;
    }

    // Возвращает ID соответствующей записи с БД:
    public int getDbID() throws SQLException
    {
        String sql = "SELECT id_discipline " +
                "FROM Discipline " +
                "WHERE name_discipline = ?";

        PreparedStatement ps = DB.connection.prepareStatement(sql);
        ps.setString(1, name_discipline);
        ResultSet rs = ps.executeQuery();
        if (rs.next())
        {
            return rs.getInt("id_discipline");
        }
        return 0;   // означает что в БД нет такой записи
    }

    public int getDbIdStrict() throws SQLException
    {
        String sql = "SELECT id_discipline " +
                "FROM Discipline " +
                "WHERE name_discipline = ? AND id_teacher = ?";

        PreparedStatement ps = DB.connection.prepareStatement(sql);
        ps.setString(1, name_discipline);
        ps.setInt(2, id_teacher);
        ResultSet rs = ps.executeQuery();
        if (rs.next())
        {
            return rs.getInt("id_discipline");
        }
        return 0;   // означает что в БД нет такой записи
    }

    public void setID(int id)
    {
        this.id = id;
    }

    public String getName_discipline()
    {
        return name_discipline;
    }

    public void setName_discipline(String name_discipline)
    {
        this.name_discipline = name_discipline;
    }

    public int getId_teacher()
    {
        return id_teacher;
    }

    public void setId_teacher(int id_teacher)
    {
        this.id_teacher = id_teacher;
    }

    // CRUD операции:

    // Create:
    boolean create() throws SQLException
    {
        // Если объект не существует в БД, нужно его добавить:
        if (id == 0)
        {
            String sql = "INSERT INTO Discipline(" +
                    "name_discipline," +
                    "id_teacher) " +
                    " VALUES (?,?)";

            PreparedStatement ps = DB.connection.prepareStatement(sql);
            ps.setString(1, name_discipline);
            ps.setInt(2, id_teacher);
            ps.executeUpdate();

            return true;
        }
        else
        {
            return false;
        }
    }

    boolean update() throws SQLException
    {
        // Если объект с заданным id существует
        // Проверка соответствия записи в БД объекту
        Discipline temp = this.read();     // получение записи из БД по id
        if (temp != null)         // если объекты не равны между собой
        {
            // необходимо обновить информацию в БД
            String sql = "UPDATE Teacher SET" +
                    "name_discipline = ?" +
                    "id_teacher = ?" +
                    "WHERE id_discipline = ?";

            PreparedStatement ps = DB.connection.prepareStatement(sql);
            ps.setString(1, name_discipline);
            ps.setInt(2, id_teacher);
            ps.setInt(3, id);
            ps.executeUpdate();

            return true;
        }
        else
        {
            return false;
        }
    }

    // Read (метод возвращает объект типа Teacher):
    Discipline read() throws SQLException
    {
        Discipline temp = null;
        // Объект существует в БД:
        if (id != 0)
        {
            String sql = "SELECT name_discipline," +
                    "id_teacher FROM Discipline" +
                    "WHERE id_discipline = ?";

            PreparedStatement ps = DB.connection.prepareStatement(sql);
            ps.setInt(1, this.id);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
            {
                temp = new Discipline();
                temp.name_discipline = rs.getString("name_discipline");
                temp.id_teacher = rs.getInt("id_teacher");
            }
        }

        return temp;
    }

    // Delete:
    void delete() throws SQLException
    {
        id = this.getDbID();
        // если объект есть в БД:
        if (id != 0)
        {
            String sql = "DELETE FROM Discipline WHERE id_discipline = ?";
            PreparedStatement ps = DB.connection.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            id = 0;
        }
    }

    public static ResultSet getAllDisciplinesAndTeachers() throws SQLException
    {
        String sql = "SELECT name_discipline, CONCAT(teacher_surname, ' ', teacher_name, ' ', teacher_patronymic_name) AS fullName " +
                "FROM Discipline " +
                "JOIN Teacher " +
                "ON Discipline.id_teacher = Teacher.id_teacher";
        PreparedStatement ps = DB.connection.prepareStatement(sql);

        return ps.executeQuery();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Discipline that = (Discipline) o;
        return id == that.id && id_teacher == that.id_teacher && Objects.equals(name_discipline, that.name_discipline);
    }

    @Override
    public String toString()
    {
        return "Discipline{" +
                "id=" + id +
                ", name_discipline='" + name_discipline + '\'' +
                ", id_teacher=" + id_teacher +
                '}';
    }
}
