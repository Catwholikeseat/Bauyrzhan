package com.example.deansofficeapp;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class Rating
{
    int id;
    int id_student;
    int id_discipline;
    int rating;
    int week;
    String type_of_rating;

    Rating()
    {

    }

    public Rating(int id_student, int id_discipline, int rating, int week, String type_of_rating)
    {
        this.id_student = id_student;
        this.id_discipline = id_discipline;
        this.rating = rating;
        this.week = week;
        this.type_of_rating = type_of_rating;
    }

    // Возвращает ID объекта:
    public int getLocalID()
    {
        return id;
    }

    // Возвращает ID соответствующей записи с БД:
    public int getDbID() throws SQLException
    {
        String sql = "SELECT id_rating " +
                "FROM Rating " +
                "WHERE id_student = ? AND " +
                "id_discipline = ? AND " +
                "week = ? " +
                "AND type_of_rating = ?";

        PreparedStatement ps = DB.connection.prepareStatement(sql);
        ps.setInt(1, id_student);
        ps.setInt(2, id_discipline);
        ps.setInt(3, week);
        ps.setString(4, type_of_rating);
        ResultSet rs = ps.executeQuery();
        if (rs.next())
        {
            return rs.getInt("id_rating");
        }
        return 0;   // означает что в БД нет такой записи
    }

    public void setID(int id)
    {
        this.id = id;
    }

    public int getId_student()
    {
        return id_student;
    }

    public void setId_student(int id_student)
    {
        this.id_student = id_student;
    }

    public int getId_discipline()
    {
        return id_discipline;
    }

    public void setId_discipline(int id_discipline)
    {
        this.id_discipline = id_discipline;
    }

    public int getRating()
    {
        return rating;
    }

    public void setRating(int rating)
    {
        this.rating = rating;
    }

    public int getWeek()
    {
        return week;
    }

    public void setWeek(int week)
    {
        this.week = week;
    }

    public String getType_of_rating()
    {
        return type_of_rating;
    }

    public void setType_of_rating(String type_of_rating)
    {
        this.type_of_rating = type_of_rating;
    }

    // CRUD операции:

    // Create:
    boolean create() throws SQLException
    {
        // Если объект не существует в БД, нужно его добавить:
        if (id == 0)
        {
            String sql = "INSERT INTO Rating(" +
                    "id_student," +
                    "id_discipline," +
                    "rating," +
                    "week, type_of_rating) " +
                    " VALUES (?,?,?,?, ?)";

            PreparedStatement ps = DB.connection.prepareStatement(sql);
            ps.setInt(1, id_student);
            ps.setInt(2, id_discipline);
            ps.setInt(3, rating);
            ps.setInt(4, week);
            ps.setString(5, type_of_rating);
            ps.executeUpdate();

            return true;
        }
        else
        {
            return false;
        }
    }

    public void update() throws SQLException
    {
        // Если объект с заданным id существует
        // Проверка соответствия записи в БД объекту
        Rating temp = this.read();     // получение записи из БД по id
        if (temp != null)         // если объекты не равны между собой
        {
            // необходимо обновить информацию в БД
            String sql = "UPDATE Rating SET " +
                    "id_student = ?, " +
                    "id_discipline = ?, " +
                    "rating = ?, " +
                    "week = ?, " +
                    "type_of_rating = ? " +
                    "WHERE id_rating = ?";

            PreparedStatement ps = DB.connection.prepareStatement(sql);
            ps.setInt(1, id_student);
            ps.setInt(2, id_discipline);
            ps.setInt(3, rating);
            ps.setInt(4, week);
            ps.setString(5, type_of_rating);
            ps.setInt(6, id);
            ps.executeUpdate();
        }
    }

    // Read (метод возвращает объект типа Teacher):
    Rating read() throws SQLException
    {
        Rating temp = null;
        // Объект существует в БД:
        if (id != 0)
        {
            String sql = "SELECT * FROM Rating WHERE id_rating = ?";
            PreparedStatement ps = DB.connection.prepareStatement(sql);
            ps.setInt(1, this.id);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
            {
                temp = new Rating();
                temp.id_student = rs.getInt("id_student");
                temp.id_discipline = rs.getInt("id_discipline");
                temp.rating = rs.getInt("rating");
                temp.week = rs.getInt("week");
                temp.type_of_rating = rs.getString("type_of_rating");
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
            String sql = "DELETE FROM Rating WHERE id_rating = ?";
            PreparedStatement ps = DB.connection.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            id = 0;
        }
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rating rating1 = (Rating) o;
        return id == rating1.id && id_student == rating1.id_student && id_discipline == rating1.id_discipline && rating == rating1.rating && week == rating1.week && Objects.equals(type_of_rating, rating1.type_of_rating);
    }

    @Override
    public String toString()
    {
        return "Rating{" +
                "id=" + id +
                ", id_student=" + id_student +
                ", id_discipline=" + id_discipline +
                ", rating=" + rating +
                ", week=" + week +
                ", type_of_rating='" + type_of_rating + '\'' +
                '}';
    }
}
