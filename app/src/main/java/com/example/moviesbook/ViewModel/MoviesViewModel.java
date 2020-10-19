package com.example.moviesbook.ViewModel;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.example.moviesbook.Client.MoviesClient;
import com.example.moviesbook.MovieResults;
import com.example.moviesbook.MovieRsults2;
import com.example.moviesbook.Movies2;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.gson.reflect.TypeToken.get;

public class MoviesViewModel extends ViewModel {

    public MutableLiveData<MovieResults> MoviesMutable= new MutableLiveData();
    public MutableLiveData<MovieRsults2> MoviesMutable3 = new MutableLiveData();
    public MutableLiveData<Movies2> MoviesMutable2= new MutableLiveData();
    String x;
    public void getMovies(String page)
    {

        x=page;


          MoviesClient.getINSTANCE().getMovies(x).enqueue(new Callback<MovieResults>() {
            @Override
            public void onResponse(Call<MovieResults> call, Response<MovieResults> response) {
                       MoviesMutable.setValue(response.body());

                    }

                    @Override
                    public void onFailure(Call<MovieResults> call, Throwable t) {

                    }
                });
            }
    public void getRecommendations(int ID)
    {

        MoviesClient.getINSTANCE().getRecommendations(ID).enqueue(new Callback<MovieRsults2>() {
            @Override
            public void onResponse(Call<MovieRsults2> call, Response<MovieRsults2> response) {
                MoviesMutable3.setValue(response.body());
            }

            @Override
            public void onFailure(Call<MovieRsults2> call, Throwable t) {

            }
        });
    }
    public void getMovies2(int ID)
    {


        MoviesClient.getINSTANCE().getMovies2(ID).enqueue(new Callback<Movies2>() {
            @Override
            public void onResponse(Call<Movies2> call, Response<Movies2> response) {
                MoviesMutable2.setValue(response.body());
            }

            @Override
            public void onFailure(Call<Movies2> call, Throwable t) {

            }
        });
    }
    }

