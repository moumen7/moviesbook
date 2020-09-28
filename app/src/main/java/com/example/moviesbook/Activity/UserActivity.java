package com.example.moviesbook.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.example.moviesbook.R;

public class UserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
    }

    public void Logout(View view) {
        Intent intent = new Intent(UserActivity.this,LoginActivity.class);
        startActivity(intent);
        SharedPreferences mPrefs = getSharedPreferences("user",MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.clear();
        editor.apply();
        finish();
    }
}
