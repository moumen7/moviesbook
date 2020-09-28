package com.example.moviesbook;

public class Movie {
    String Desc;
    String Image;
    String Rating;
    String Title;
    String Year;
    String ID;
    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }


    Movie()
    {

    }

    public Movie(String desc, String image, String rating, String title, String year) {
        Desc = desc;
        Image = image;
        Rating = rating;
        Title = title;
        Year = year;
    }

    public String getDesc() {
        return Desc;
    }

    public void setDesc(String desc) {
        Desc = desc;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getRating() {
        return Rating;
    }

    public void setRating(String rating) {
        Rating = rating;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getYear() {
        return Year;
    }

    public void setYear(String year) {
        Year = year;
    }



}
