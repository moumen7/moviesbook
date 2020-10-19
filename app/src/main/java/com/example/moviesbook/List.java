package com.example.moviesbook;

public class List {
    String ID;
    String Image;
    String Name;

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    long number;

    public List()
    {

    }
    public List(String ID, String image, String name) {
        this.ID = ID;
        Image = image;
        Name = name;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
