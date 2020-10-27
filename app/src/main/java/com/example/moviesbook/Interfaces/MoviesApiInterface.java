package com.example.moviesbook.Interfaces;

import android.app.DownloadManager;

import com.example.moviesbook.MovieResults;
import com.example.moviesbook.MovieRsults2;
import com.example.moviesbook.Movies2;

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
    @GET("/3/movie/{movie_id}&append_to_response=videos")
    Call<Movies2> getMovies2(
            @Path("movie_id") int movie_id,
            @Query("api_key") String api_key
    );

    @GET("/3/movie/{movie_id}/recommendations")
    Call<MovieRsults2> getRecommedations
    (
            @Path("movie_id") int movie_id,
            @Query("api_key") String api_key
    );

}
