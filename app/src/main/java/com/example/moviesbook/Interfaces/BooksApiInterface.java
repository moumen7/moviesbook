package com.example.moviesbook.Interfaces;

import com.example.moviesbook.Json_Books.BooksResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BooksApiInterface {
    @GET("books/v1/volumes")
    Call<BooksResult> getBooks(
            @Query("q") String query,
            @Query("key") String yourAPIKey
    );

    @GET("ebooks")
    Call<BooksResult> getBook(
            @Query("q") String id,
            @Query("key") String yourAPIKey
    );
}
