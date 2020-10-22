package com.example.moviesbook;

public class Book {
    String Desc;
    String Image;
    String Title;
    String Year;
    String ID;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public Book()
    {

    }

    public Book(String desc, String image, String title, String year) {
        Desc = desc;
        Image = image;
        Title = title;
        Year = year;
    }
    public Book(String ID, String image, String title) {
        this.ID = ID;
        this.Image = image;
        this.Title = title;
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
