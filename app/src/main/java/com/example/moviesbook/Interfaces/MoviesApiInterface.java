package com.example.moviesbook.Interfaces;

import android.app.DownloadManager;

import com.example.moviesbook.MovieResults;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MoviesApiInterface {
   @GET("/3/search/movie")
   Call<MovieResults> getMovies(
        @Query("query") String query,
        @Query("api_key") String api_key
    );
}
