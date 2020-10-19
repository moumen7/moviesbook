package com.example.moviesbook.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.moviesbook.Adapter.BooksAdapter;
import com.example.moviesbook.Adapter.MovieAdapter;
import com.example.moviesbook.Client.MoviesClient;
import com.example.moviesbook.Interfaces.ClickListener;
import com.example.moviesbook.Interfaces.MoviesApiInterface;
import com.example.moviesbook.Json_Books.BooksResult;
import com.example.moviesbook.Json_Books.Item;
import com.example.moviesbook.MovieResults;
import com.example.moviesbook.R;
import com.example.moviesbook.Result;
import com.example.moviesbook.Userdata;
import com.example.moviesbook.ViewModel.BooksViewModel;
import com.example.moviesbook.ViewModel.MoviesViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    SharedPreferences sp;
    SharedPreferences sp2;
    boolean orig = true;
    SearchView searchView;
    Userdata userdata;
    Intent mIntent;
    RecyclerView recyclerView;
    MoviesViewModel moviesViewModel;
    BooksViewModel booksViewModel;
    MovieAdapter adapter;
    FirebaseFirestore db;
    BooksAdapter adapter2;
    Boolean [] check;
    int ind;
    private Activity mActivity;
    String where = "Movie";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        userdata= new Userdata();
        mIntent = getIntent();
        TextView tv = findViewById(R.id.textView5);
        db = FirebaseFirestore.getInstance();

        if(!mIntent.hasExtra("addpic"))
        {
            where = "Book";
            tv.setText("Click here when done");
        }
        else
        {
            where = "Movie";

        }
        if(mIntent.hasExtra("Movies"))
        {
            where = "Movie";
        }
        else if(mIntent.hasExtra("Books"))
        {
            where="Book";
        }

        sp = getSharedPreferences("prev", Context.MODE_PRIVATE);
        sp2 = getSharedPreferences("user",Context.MODE_PRIVATE);
        mActivity = AddActivity.this;
        if(mIntent.hasExtra("post")) {
            orig = false;
            tv.setVisibility(View.GONE);
            if (where.equals("Movie")) {
                setTitle("Choose Movie to post");
            } else {
                setTitle("Choose book to post");
            }
        }
        else
        {

            if (where.equals("Movie")) {
                setTitle("Add Movies");
            } else {
                setTitle("Add Books");
            }
        }
        moviesViewModel = ViewModelProviders.of(this).get(MoviesViewModel.class);
        booksViewModel = ViewModelProviders.of(this).get(BooksViewModel.class);
        recyclerView = findViewById(R.id.recycler);
        adapter2 = new BooksAdapter(AddActivity.this, new ClickListener() {
            @Override
            public void onPositionClicked(int position) {

            }

            @Override
            public void onLongClicked(int position) {

            }
        },orig,getIntent().getStringExtra("id"));
        adapter= new MovieAdapter(AddActivity.this, new ClickListener() {
            @Override public void onPositionClicked(int position) {

            }

            @Override public void onLongClicked(int position) {
                Toast.makeText(AddActivity.this,"heree4",Toast.LENGTH_LONG).show();
            }
        },orig,getIntent().getStringExtra("id"));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(AddActivity.this);

        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if(!query.isEmpty())
        {
            if(where.equals("Book")) {
                recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
                recyclerView.setAdapter(adapter2);
                booksViewModel.getBooks(query);
                booksViewModel.BooksMutable.observe(AddActivity.this, new Observer<BooksResult>() {

                    @Override
                    public void onChanged(BooksResult postModels) {
                        adapter2.setList(postModels.getItems());
                    }
                });
            }
            else
            {
                recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
                recyclerView.setAdapter(adapter);
                moviesViewModel.getMovies(query);
                moviesViewModel.MoviesMutable.observe(AddActivity.this, new Observer<MovieResults>() {

                    @Override
                    public void onChanged(MovieResults postModels) {
                        adapter.setList(postModels.getResults());
                    }
                });
            }
        }
        else
        {
            List<Item> res = new ArrayList<>();
            adapter2.setList(res);
        }
        return false;
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void Next(View view) {
        if(!(mIntent.hasExtra("Movies") || mIntent.hasExtra("Books"))) {
            if (mIntent.hasExtra("addpic")) {
                getIntent().removeExtra("addpic");
                restartActivity(mActivity);
            } else {
                Intent intent = new Intent(AddActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        }
        else
        {
            Intent intent = new Intent(AddActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }
    public static void restartActivity(Activity activity) {
        if (Build.VERSION.SDK_INT >= 11) {
            activity.recreate();
        } else {
            activity.startActivity(activity.getIntent());
        }
    }
}
