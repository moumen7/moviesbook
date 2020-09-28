package com.example.moviesbook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Userdata {
    public static Map<String,Boolean> Usermovies = new HashMap<>();
    public static Map<String,Boolean> Userbooks = new HashMap<>();
    public static Map<String,Boolean> followers = new HashMap<>();
    public static Map<String,Boolean> following = new HashMap<>();
    public static String Username = new String();
    public static String Image= new String();
    public Userdata()
    {
        Usermovies = new HashMap<>();
        Userbooks = new HashMap<>();
        followers = new HashMap<>();

    }

    public static Map<String,Boolean> getUserbooks() {
        return Userbooks;
    }

    public static Map<String,Boolean> getUsermovies() {
        return Usermovies;
    }

    public static void setUserbooks(Map<String,Boolean> userbooks) {
        Userbooks = userbooks;
    }

    public static void setUsermovies(Map<String,Boolean> usermovies) {
        Usermovies = usermovies;
    }
}
