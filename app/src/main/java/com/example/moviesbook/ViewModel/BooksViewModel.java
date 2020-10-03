package com.example.moviesbook.ViewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.moviesbook.Json_Books.BooksResult;
import com.example.moviesbook.Client.BooksClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BooksViewModel extends ViewModel {
    public MutableLiveData<BooksResult> BooksMutable= new MutableLiveData();
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
}
