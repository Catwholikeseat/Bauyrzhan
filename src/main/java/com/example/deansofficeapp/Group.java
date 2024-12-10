package com.example.deansofficeapp;

import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

public class Group
{
    int id;
    String name_group;

    Group()
    {

    }

    Group(String name_group)
    {
        this.name_group = name_group;
    }

    // Возвращает ID объекта:
    public int getLocalID()
    {
        return id;
    }

    // Возвращает ID соответствующей записи с БД:
    public int getDbID() throws SQLException
    {
        String sql = "SELECT id_group " +
                "FROM [Group] " +
                "WHERE name_group = ?";

        PreparedStatement ps = DB.connection.prepareStatement(sql);
        ps.setString(1, name_group);
        ResultSet rs = ps.executeQuery();
        if (rs.next())
        {
            return rs.getInt("id_group");
        }
        return 0;   // означает что в БД нет такой записи
    }

    public void setID(int id)
    {
        this.id = id;
    };

    public String getName_group()
    {
        return name_group;
    }

    public void setName_group(String name_group)
    {
        this.name_group = name_group;
    }

    // CRUD операции:

    // Create:
    boolean create() throws SQLException
    {
        id = this.getDbID();
        // Если объект не существует в БД, нужно его добавить:
        if (id == 0)
        {
            String sql = "INSERT INTO [Group](" +
                    "name_group) " +
                    " VALUES (?)";

            PreparedStatement ps = DB.connection.prepareStatement(sql);
            ps.setString(1, name_group);
            ps.executeUpdate();
        }
        else
        {
            // Если объект с заданным id существует
            // Проверка соответствия записи в БД объекту
            Group temp = this.read();     // получение записи из БД по id
            if (!this.equals(temp))         // если объекты не равны между собой
            {
                // необходимо обновить информацию в БД
                String sql = "UPDATE [Group] SET " +
                        "name_group = ?" +
                        "WHERE id_group = ?";

                PreparedStatement ps = DB.connection.prepareStatement(sql);
                ps.setString(1, name_group);
                ps.setInt(2, id);
                ps.executeUpdate();
            }
            else
            {
                return false;
            }
        }

        return true;
    }

    // Read (метод возвращает объект типа Teacher):
    Group read() throws SQLException
    {
        Group temp = null;
        // Объект существует в БД:
        if (id != 0)
        {
            String sql = "SELECT name_group FROM [Group]" +
                    "WHERE id_group = ?";

            PreparedStatement ps = DB.connection.prepareStatement(sql);
            ps.setInt(1, this.id);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
            {
                temp = new Group();
                temp.id = id;
                temp.name_group = rs.getString("name_group");
            }
        }

        return temp;
    }

    // Delete (удаление группы должно сопровождать удаление всех студентов этой группы):
    void delete() throws SQLException
    {
        id = this.getDbID();
        // если объект есть в БД:
        if (id != 0)
        {
            String sql = "DELETE FROM [Group] WHERE id_group = ?";
            PreparedStatement ps = DB.connection.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            id = 0;
            // для удаления студентов этой группы было настроено каскадное удаление
        }
    }

    // Методы необходимые для реализации приложения:

    // метод из объекта записи БД записывает данные в объект класса Group:
    static Group getDataFromResultSet(ResultSet rs) throws SQLException
    {
        Group temp = new Group();
        temp.setID(rs.getInt("id"));
        temp.setName_group(rs.getString("name_group"));

        return temp;
    }

    // получение соединенной таблицы содержащую информацию о группах и студентах:
    static ResultSet getAllGroupAndStudent() throws SQLException
    {
        ArrayList<Group> groups = new ArrayList<Group>();

        String sql = "SELECT * FROM [Group] " +
                "LEFT OUTER JOIN Student " +
                "ON [Group].id_group = Student.id_group";
        PreparedStatement ps = DB.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        return rs;
    }

    // возвращает список имен всех групп
    static ArrayList<String> getAllGroups() throws SQLException
    {
        ArrayList<String> list = null;
        String sql = "SELECT name_group FROM [Group]";
        PreparedStatement ps = DB.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        if (rs == null)
        {
            return list;
        }
        list = new ArrayList<>();
        while (rs.next())
        {
            list.add(rs.getString("name_group"));
        }

        return list;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return id == group.id && Objects.equals(name_group, group.name_group);
    }

    @Override
    public String toString()
    {
        return "Group{" +
                "id=" + id +
                ", name_group='" + name_group + '\'' +
                '}';
    }
}
