package com.example.moviesbook;

public class comment {
    private String username;
    private String image;
    private String text;
    private String id;

    public comment(String username, String text, String id) {
        this.username = username;
        this.text = text;
        this.id = id;
    }
    public comment() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
