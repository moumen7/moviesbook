package com.example.moviesbook.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.moviesbook.R;

public class chooseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        if(getIntent().getStringExtra("type").equals("books"))
        {
            setTitle("Choose book to post");
        }
        else
        {
            setTitle("Choose movie to post");
        }

    }
}
