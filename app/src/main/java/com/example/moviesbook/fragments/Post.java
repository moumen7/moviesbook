package com.example.moviesbook.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Post {
    String userimage;
    String username;
    String userid;
    String usedid;
    String usedtitle;
    String postdesc;
    String Image;
    String Date;
    String Postid;
    int comments;
    Map<String,Boolean> Likers = new HashMap<>();

    public ArrayList<HashMap<String, String>> getCommenters() {
        return Commenters;
    }

    public void setCommenters(ArrayList<HashMap<String, String>> commenters) {
        Commenters = commenters;
    }

    ArrayList<HashMap<String,String>> Commenters = new ArrayList<>();
    int likes;

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }


    Post()
    {

    }
    public String getPostid() {
        return Postid;
    }

    public void setPostid(String postid) {
        this.Postid = postid;
    }



    public String getUserimage() {
        return userimage;
    }

    public void setUserimage(String userimage) {
        this.userimage = userimage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsedid() {
        return usedid;
    }

    public void setUsedid(String usedid) {
        this.usedid = usedid;
    }

    public String getUsedtitle() {
        return usedtitle;
    }

    public void setUsedtitle(String usedtitle) {
        this.usedtitle = usedtitle;
    }

    public String getPostdesc() {
        return postdesc;
    }

    public void setPostdesc(String postdesc) {
        this.postdesc = postdesc;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public Map<String, Boolean> getLikers() {
        return Likers;
    }

    public void setLikers(Map<String, Boolean> Likers) {
        this.Likers = Likers;
    }


}
