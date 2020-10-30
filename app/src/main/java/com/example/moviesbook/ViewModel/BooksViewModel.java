package com.example.moviesbook.ViewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.moviesbook.Example;
import com.example.moviesbook.Json_Books.BooksResult;
import com.example.moviesbook.Client.BooksClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BooksViewModel extends ViewModel {
    public MutableLiveData<BooksResult> BooksMutable= new MutableLiveData();
    public MutableLiveData<Example> BooksMutable2= new MutableLiveData();
    String x;
    public void getBooks(String page)
    {
        x=page;
        BooksClient.getINSTANCE().getBooks(x).enqueue(new Callback<BooksResult>() {
            @Override
            public void onResponse(Call<BooksResult> call, Response<BooksResult> response) {
                BooksMutable.setValue(response.body());
            }
            @Override
            public void onFailure(Call<BooksResult> call, Throwable t) {
            }
        });
    }
    public void getBook(String page)
    {
        x=page;
        BooksClient.getINSTANCE().getBook(x).enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Call<Example> call, Response<Example> response) {
                BooksMutable2.setValue(response.body());
            }
            @Override
            public void onFailure(Call<Example> call, Throwable t) {
            }
        });
    }
}
