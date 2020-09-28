package com.example.moviesbook;

public class Friend {
    private String username;
    private String image;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }




    public Friend(String username, String image,String id) {
        this.username = username;
        this.image = image;
        this.id = id;

    }

    public Friend() {
        /// Don't Delete
    }

    public String getUsername() {
        return username;
    }

    public String getImage() {
        return image;
    }


}
