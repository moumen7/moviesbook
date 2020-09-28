package com.example.moviesbook.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.Toast;

import com.example.moviesbook.Adapter.MovieAdapter;
import com.example.moviesbook.Adapter.Mybooksadapter;
import com.example.moviesbook.Adapter.Mymoviesadapter;
import com.example.moviesbook.Book;
import com.example.moviesbook.Interfaces.ClickListener;
import com.example.moviesbook.Movie;
import com.example.moviesbook.R;
import com.example.moviesbook.Userdata;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ViewActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    FirebaseFirestore fb;
    SharedPreferences sp;
    ArrayList<Movie> usermovies;
    ArrayList<Book> userbooks;
    Mymoviesadapter mymoviesadapter;
    Mybooksadapter mybooksadapter;
    Boolean pass;
    Query q;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        recyclerView = findViewById(R.id.view);
        if(getIntent().hasExtra("ID"))
            pass = false;
        else
            pass = true;
        fb =  FirebaseFirestore.getInstance();
        mymoviesadapter = new Mymoviesadapter(ViewActivity.this,new ClickListener() {
            @Override
            public void onPositionClicked(int position) {

            }

            @Override
            public void onLongClicked(int position) {

            }
        },pass);
        mybooksadapter = new Mybooksadapter(ViewActivity.this,new ClickListener() {
            @Override
            public void onPositionClicked(int position) {

            }

            @Override
            public void onLongClicked(int position) {

            }
        },pass);
        usermovies = new ArrayList<>();
        userbooks = new ArrayList<>();
        sp = getSharedPreferences("user",MODE_PRIVATE);
        if(getIntent().hasExtra("Movies"))
        {
            setTitle("fav movies");
            if(getIntent().hasExtra("ID"))
            {
                q = fb.collection("Users").document(getIntent().getStringExtra("id"))
                        .collection("MoviesList");
            }
            else {
                q = fb.collection("Users").document(sp.getString("ID", ""))
                        .collection("MoviesList");
            }

            q.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                    int x = 0;
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        Movie ChatUser = snapshot.toObject(Movie.class);
                        if(!getIntent().hasExtra("id"))
                        Userdata.Usermovies.put(snapshot.getId(),true);
                        usermovies.add(ChatUser);
                        usermovies.get(x).setID(snapshot.getId());
                        x++;
                    }

                    mymoviesadapter.setList(usermovies);
                }
            });
            recyclerView.setAdapter(mymoviesadapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(ViewActivity.this));
        }
        else
        {
            setTitle("fav books");
            if(getIntent().hasExtra("ID"))
            {
                q = fb.collection("Users").document(getIntent().getStringExtra("id"))
                        .collection("BooksList");
            }
            else {
                q = fb.collection("Users").document(sp.getString("ID", ""))
                        .collection("BooksList");
            }
            q.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                    int x = 0;
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        Book ChatUser = snapshot.toObject(Book.class);
                        userbooks.add(ChatUser);
                        userbooks.get(x).setID(snapshot.getId());
                        x++;
                    }

                    mybooksadapter.setList(userbooks);
                }
            });
            recyclerView.setAdapter(mybooksadapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(ViewActivity.this));
        }
    }
}
