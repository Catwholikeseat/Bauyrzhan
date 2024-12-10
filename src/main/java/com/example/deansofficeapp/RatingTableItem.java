package com.example.deansofficeapp;

import javafx.beans.property.SimpleStringProperty;

import java.util.ArrayList;
import java.util.Arrays;

// класс содержит структуру одной строки таблицы оценок
public class RatingTableItem
{
    String discipline;
    String type_of_rating;
    String teacher_name;

    SimpleStringProperty[] ratings;

    RatingTableItem()
    {

    }

    public RatingTableItem(String type_of_rating, String teacher_name)
    {
        ratings = new SimpleStringProperty[15];
        this.type_of_rating = type_of_rating;
        this.teacher_name = teacher_name;
    }

    public RatingTableItem(String discipline, String type_of_rating, String teacher_name)
    {
        ratings = new SimpleStringProperty[15];
        this.discipline = discipline;
        this.type_of_rating = type_of_rating;
        this.teacher_name = teacher_name;
    }


    public SimpleStringProperty getSimpleRating(int index)
    {
        return ratings[index];
    }

    public void setRating(int index, SimpleStringProperty rating)
    {
        ratings[index] = rating;
    }

    public void setRatings(ArrayList<String> ratings)
    {
        for (int i = 1; i <= 15; ++i)
        {
            this.ratings[i - 1] = new SimpleStringProperty(ratings.get(i - 1));
        }
    }

    public String getDiscipline()
    {
        return discipline;
    }

    public String getType_of_rating()
    {
        return type_of_rating;
    }

    public String getTeacher_name()
    {
        return teacher_name;
    }

    @Override
    public String toString()
    {
        String result = "";
        for (int i = 1; i <= 15; ++i)
        {
            result += ratings[i - 1].getValue();
        }

        return result;
    }
}
