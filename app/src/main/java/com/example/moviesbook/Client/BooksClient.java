package com.example.moviesbook.Client;

import com.example.moviesbook.Json_Books.BooksResult;
import com.example.moviesbook.Interfaces.BooksApiInterface;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class BooksClient {
    private static final String BASE_URL = "\n" + "https://www.googleapis.com/books/v1/";
    private BooksApiInterface booksApiInterface;
    private static BooksClient INSTANCE;

    public BooksClient()
    {
        Retrofit retrofit =  new Retrofit.Builder().baseUrl(BASE_URL).
                addConverterFactory(GsonConverterFactory.create())
                .build();
        booksApiInterface = retrofit.create(BooksApiInterface.class);
    }

    public static BooksClient getINSTANCE() {
        if(INSTANCE== null)
        {
            INSTANCE = new BooksClient();
        }
        return INSTANCE;
    }
    public Call<BooksResult> getBooks(String SEARCH)
    {

        return booksApiInterface.getBooks(SEARCH,"AIzaSyDI6VSEg1BL0nYHhJyZEBu5RzopgV8AeP4");

    }
}
