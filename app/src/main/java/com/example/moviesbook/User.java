package com.example.moviesbook;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    String Email;
    String id;
    String Username;

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    String Image;
    public User(String email, String id, String username) {
        this.id = id;
        Email = email;
        Username = username;
    }


    public User() {
    }




    public void setEmail(String email) {
        Email = email;
    }

    public void setId(String id) {
        this.id = id;
    }



    public void setUsername(String username) {
        Username = username;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return Username;
    }

    public String getEmail() {
        return Email;
    }

}
