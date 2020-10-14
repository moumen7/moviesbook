package com.example.moviesbook.Client;

import com.example.moviesbook.Interfaces.MoviesApiInterface;
import com.example.moviesbook.MovieResults;
import com.example.moviesbook.MovieRsults2;
import com.example.moviesbook.Movies2;

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
    public Call<MovieRsults2> getRecommendations(int ID)
    {

        return moviesInterface.getRecommedations(ID,"c5fa320e469e34c460448dda7edfba79");

    }
    public Call<Movies2> getMovies2(int ID)
    {

        return moviesInterface.getMovies2(ID,"c5fa320e469e34c460448dda7edfba79");

    }
}
