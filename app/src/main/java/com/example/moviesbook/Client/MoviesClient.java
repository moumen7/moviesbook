package com.example.moviesbook.Client;

import com.example.moviesbook.Interfaces.MoviesApiInterface;
import com.example.moviesbook.MovieResults;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class MoviesClient {
    private static final String BASE_URL = "\n" + "https://api.themoviedb.org/";
    private MoviesApiInterface moviesInterface;
    private static MoviesClient INSTANCE;

    public MoviesClient()
    {
        Retrofit retrofit =  new Retrofit.Builder().baseUrl(BASE_URL).
                addConverterFactory(GsonConverterFactory.create())
                .build();
        moviesInterface = retrofit.create(MoviesApiInterface.class);
    }

    public static MoviesClient getINSTANCE() {
        if(INSTANCE== null)
        {
            INSTANCE = new MoviesClient();
        }
        return INSTANCE;
    }
    public Call<MovieResults> getMovies(String SEARCH)
    {

        return moviesInterface.getMovies(SEARCH,"c5fa320e469e34c460448dda7edfba79");

    }
}
