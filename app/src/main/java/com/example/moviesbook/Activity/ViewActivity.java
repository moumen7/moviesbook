package com.example.moviesbook.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ViewActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    FirebaseFirestore fb;
    SharedPreferences sp;
    ArrayList<Movie> usermovies;
    ArrayList<Book> userbooks;
    Mymoviesadapter mymoviesadapter;
    Mybooksadapter mybooksadapter;
    Button button;

    Boolean pass;
    Query q;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences("user",MODE_PRIVATE);
        setContentView(R.layout.activity_view);
        recyclerView = findViewById(R.id.view);
        button = findViewById(R.id.Add);
        fb =  FirebaseFirestore.getInstance();


        if(!getIntent().getStringExtra("id").contains(sp.getString("ID","")))
        button.setVisibility(View.GONE);
        mymoviesadapter = new Mymoviesadapter(ViewActivity.this,new ClickListener() {
            @Override
            public void onPositionClicked(int position) {

            }

            @Override
            public void onLongClicked(int position) {

            }
        }, getIntent().getStringExtra("id"));
        mybooksadapter = new Mybooksadapter(ViewActivity.this,new ClickListener() {
            @Override
            public void onPositionClicked(int position) {

            }

            @Override
            public void onLongClicked(int position) {

            }
        }, getIntent().getStringExtra("id"));
        usermovies = new ArrayList<>();
        userbooks = new ArrayList<>();

        if(getIntent().hasExtra("MutualMovies"))
        {
            String id = getIntent().getStringExtra("id");
            String id2 = getIntent().getStringExtra("id2");

            button.setVisibility(View.GONE);
            setTitle(getIntent().getStringExtra("Name"));
            //MUTUAL MOVIES
            final ArrayList<Movie> movies = new ArrayList<>();
            final Set<String> repeated = new HashSet<String>();

            CollectionReference ref = fb.collection("Movies");
            Task task1 = ref.whereArrayContains("users", id).get();

            Task task2 = ref.whereArrayContains("users", id2).get();

            Task<java.util.List<QuerySnapshot>> alltasks = Tasks.whenAllSuccess(task1, task2);
            alltasks.addOnSuccessListener(new OnSuccessListener<List<QuerySnapshot>>() {
                @Override
                public void onSuccess(java.util.List<QuerySnapshot> querySnapshots)
                {

                    int x = 0;
                    for (QuerySnapshot queryDocumentSnapshots : querySnapshots)
                    {
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                        {

                            Movie movie = snapshot.toObject(Movie.class);
                            if(repeated.contains(snapshot.getId())) {
                                movies.add(movie);
                            }
                            else
                                repeated.add(snapshot.getId());
                        }
                        mymoviesadapter.setList(movies);

                    }
                    recyclerView.setAdapter(mymoviesadapter);
                    recyclerView.setNestedScrollingEnabled(false);
                    recyclerView.setLayoutManager(new LinearLayoutManager(ViewActivity.this));

                }

            });
        }
        if(getIntent().hasExtra("MutualBooks"))
        {
            String id = getIntent().getStringExtra("id");
            String id2 = getIntent().getStringExtra("id2");

            button.setVisibility(View.GONE);
            setTitle(getIntent().getStringExtra("Name"));
            //MUTUAL BOOKS
            final ArrayList<Book> books = new ArrayList<>();
            final Set <String> repeatedbooks = new HashSet<String>();

            CollectionReference ref = fb.collection("Books");
            Task task1 = ref.whereArrayContains("users", id).get();
            Task task2 = ref.whereArrayContains("users", id2).get();

            Task<java.util.List<QuerySnapshot>> alltasks = Tasks.whenAllSuccess(task1, task2);
            alltasks.addOnSuccessListener(new OnSuccessListener<java.util.List<QuerySnapshot>>() {
                @Override
                public void onSuccess(java.util.List<QuerySnapshot> querySnapshots) {
                    for (QuerySnapshot queryDocumentSnapshots : querySnapshots)
                    {

                        for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                        {
                            Book book = snapshot.toObject(Book.class);
                            if(repeatedbooks.contains(snapshot.getId())) {
                                books.add(book);

                            }
                            else
                                repeatedbooks.add(snapshot.getId());

                        }
                        mybooksadapter.setList(books);
                    }
                    recyclerView.setAdapter(mybooksadapter);
                    recyclerView.setNestedScrollingEnabled(false);
                    recyclerView.setLayoutManager(new LinearLayoutManager(ViewActivity.this));
                }

            });
        }

        if(getIntent().hasExtra("Movie"))
        {
            button.setText("Add Movies");
            setTitle(getIntent().getStringExtra("Name"));

                q = fb.collection("Movies").whereArrayContains("users",getIntent().getStringExtra("id"));


            q.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                int x = 0;
                                for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                    Movie ChatUser = snapshot.toObject(Movie.class);
                                    usermovies.add(ChatUser);
                                    usermovies.get(x).setID(snapshot.getId());
                                    x++;
                                }
                                mymoviesadapter.setList(usermovies);
                            }
                        }
                    });
            recyclerView.setAdapter(mymoviesadapter);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setLayoutManager(new LinearLayoutManager(ViewActivity.this));
        }
        else
        {
            button.setText("Add Books");
            setTitle(getIntent().getStringExtra("Name"));

                q = fb.collection("Books").whereArrayContains("users",getIntent().getStringExtra("id"));
            q.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                int x = 0;
                                for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                    Book ChatUser = snapshot.toObject(Book.class);
                                    userbooks.add(ChatUser);
                                    userbooks.get(x).setID(snapshot.getId());
                                    x++;
                                }

                                mybooksadapter.setList(userbooks);
                            }
                        }
                    });
            recyclerView.setAdapter(mybooksadapter);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setLayoutManager(new LinearLayoutManager(ViewActivity.this));
        }

    }
    public void go(View view)
    {
        if(view.getId() == button.getId())
        {
            Intent intent = new Intent(ViewActivity.this,AddActivity.class);
            if(getIntent().hasExtra("Movie"))
            intent.putExtra("Movies",true);
            else
            intent.putExtra("Books",true);
            intent.putExtra("id", getIntent().getStringExtra("id"));
            startActivity(intent);
        }
    }
}
