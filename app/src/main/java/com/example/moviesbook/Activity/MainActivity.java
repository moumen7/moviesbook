package com.example.moviesbook.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;


import com.example.moviesbook.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";
    private EditText editTextTitle;
    private EditText editTextDescription;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences mPrefs = getSharedPreferences("user",MODE_PRIVATE);
        boolean in = mPrefs.getBoolean("in", false);
        if (in == true) {
            Intent intent = new Intent(this , HomeActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(this , LoginActivity.class);
            startActivity(intent);
            finish();
        }


    }

}