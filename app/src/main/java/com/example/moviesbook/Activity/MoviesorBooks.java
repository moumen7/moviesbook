package com.example.moviesbook.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.moviesbook.R;

public class MoviesorBooks extends AppCompatActivity {
    ImageView movies;
    ImageView books;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moviesor_books);
        movies = findViewById(R.id.imageView);
        books = findViewById(R.id.imageView2);
    }

    public void Books(View view) {
        if(view.getId() == books.getId())
        {
            Intent intent = new Intent(MoviesorBooks.this, AddActivity.class);
            intent.putExtra("Books",true);
            intent.putExtra("post",true);
            startActivity(intent);
        }
    }

    public void movies(View view)
    {
        if(view.getId() == movies.getId())
        {
            Intent intent = new Intent(MoviesorBooks.this, AddActivity.class);
            intent.putExtra("Movies",true);
            intent.putExtra("post",true);
            startActivity(intent);
        }
    }
}
