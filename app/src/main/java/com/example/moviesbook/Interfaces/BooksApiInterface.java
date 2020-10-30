package com.example.moviesbook.Interfaces;

import com.example.moviesbook.Example;
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

    @GET("books/v1/volumes/{volumeId}")
    Call<Example> getBook(
            @Path("volumeId") String id,
            @Query("key") String yourAPIKey
    );
}
