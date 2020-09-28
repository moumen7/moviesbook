package com.example.moviesbook.ViewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.example.moviesbook.Client.MoviesClient;
import com.example.moviesbook.MovieResults;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.gson.reflect.TypeToken.get;

public class MoviesViewModel extends ViewModel {

    public MutableLiveData<MovieResults> MoviesMutable= new MutableLiveData();
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





    }

