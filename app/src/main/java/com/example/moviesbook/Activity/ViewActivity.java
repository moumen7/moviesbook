package com.example.moviesbook.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.drm.DrmManagerClient;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

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
    //Button button;
    FloatingActionButton button;
    ImageView imageView;
    TextView number;
    TextView listName;
    Boolean pass;
    Query q;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        sp = getSharedPreferences("user",MODE_PRIVATE);
        setContentView(R.layout.activity_view);
        recyclerView = findViewById(R.id.view);
        button =(FloatingActionButton) findViewById(R.id.Add);
        listName = (TextView) findViewById(R.id.Listname);
        imageView = findViewById(R.id.imageView3);
        fb =  FirebaseFirestore.getInstance();
        recyclerView.setHasFixedSize(true);
        number = findViewById(R.id.number);
        recyclerView.setItemViewCacheSize(10);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        /// show only title of movie or book in toolbar when collapsing
        CollapsingToolbarLayout collapsingToolbarLayout;
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.cardInfo_collapsing_list);
        collapsingToolbarLayout.setTitleEnabled(false);
        final androidx.appcompat.widget.Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_list);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.cardInfo_appbar_list);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    button.hide();
                    number.setVisibility(View.GONE);
                    imageView.setVisibility(View.GONE);

                    isShow = true;
                } else if(isShow)
                {
                    button.show();
                    number.setVisibility(View.VISIBLE);
                    isShow = false;

                    button.show();
                    toolbar.setTitle(" ");
                    imageView.setVisibility(View.VISIBLE);
                }
                if(getIntent().hasExtra("hide")|| getIntent().hasExtra("MutualMovies") || getIntent().hasExtra("MutualBooks"))
                {
                    button.hide();
                }
            }
        });
        if(getIntent().hasExtra("hide") || getIntent().hasExtra("MutualMovies") || getIntent().hasExtra("MutualBooks"))
        {
            button.hide();
        }
        if(!getIntent().getStringExtra("id").contains(sp.getString("ID","")))
            //button.setVisibility(View.GONE);
            //button.hide();
            //hideFloatingActionButton(button);
            button.clearAnimation();
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
        if(getIntent().getStringExtra("id").equals(sp.getString("ID","")) || getIntent().getStringExtra("Image") == null) {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.fav));
        }
        else
        {
            Picasso.get().load(getIntent().getStringExtra("Image")).into(imageView);
        }


        if(getIntent().hasExtra("MutualMovies"))
        {
            String id = getIntent().getStringExtra("id");
            String id2 = getIntent().getStringExtra("id2");

            button.hide();
            //hideFloatingActionButton(button);
            //setTitle(getIntent().getStringExtra("Name"));
            listName.setText(getIntent().getStringExtra("Name"));
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

                    }
                    mymoviesadapter.setList(movies);
                    number.setText(movies.size() + " Movies");
                    recyclerView.setAdapter(mymoviesadapter);
                    recyclerView.setNestedScrollingEnabled(false);
                    recyclerView.setLayoutManager(new GridLayoutManager(ViewActivity.this,4));
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setItemViewCacheSize(10);
                    recyclerView.setDrawingCacheEnabled(true);
                    recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

                }

            });
        }
        if(getIntent().hasExtra("MutualBooks"))
        {
            button.hide();
            String id = getIntent().getStringExtra("id");
            String id2 = getIntent().getStringExtra("id2");

            //button.setVisibility(View.GONE);
            //button.hide();
            //hideFloatingActionButton(button);
            //setTitle(getIntent().getStringExtra("Name"));
            button.clearAnimation();
            listName.setText(getIntent().getStringExtra("Name"));
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
                    number.setText(books.size() + " Books");
                    recyclerView.setAdapter(mybooksadapter);
                    recyclerView.setNestedScrollingEnabled(false);
                    recyclerView.setLayoutManager(new GridLayoutManager(ViewActivity.this,4));
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setItemViewCacheSize(10);
                    recyclerView.setDrawingCacheEnabled(true);
                    recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                }

            });
        }

        if(getIntent().hasExtra("Movie"))
        {
            //button.setText("Add Movies");
            button.show();
            //showFloatingActionButton(button);
            //setTitle(getIntent().getStringExtra("Name"));
            listName.setText(getIntent().getStringExtra("Name"));
            //toolbar.setTitle(getIntent().getStringExtra("Name"));

            q = fb.collection("Movies").whereArrayContains("users",getIntent().getStringExtra("id"));


            q
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e)
                        {

                                int x = 0;
                                    usermovies.clear();
                                     for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                    Movie ChatUser = snapshot.toObject(Movie.class);
                                    usermovies.add(ChatUser);
                                    usermovies.get(x).setID(snapshot.getId());
                                    x++;
                                }
                                number.setText(usermovies.size() + " Movies");
                                mymoviesadapter.setList(usermovies);
                            }

                    });

            recyclerView.setAdapter(mymoviesadapter);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setLayoutManager(new GridLayoutManager(ViewActivity.this,4));
            recyclerView.setHasFixedSize(true);
            recyclerView.setItemViewCacheSize(10);
            recyclerView.setDrawingCacheEnabled(true);
            recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        }
        else
        {
            //button.setText("Add Books");
            button.show();
            //showFloatingActionButton(button);
           // setTitle(getIntent().getStringExtra("Name"));
            listName.setText(getIntent().getStringExtra("Name"));

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
                                number.setText(userbooks.size() + " Books");
                                mybooksadapter.setList(userbooks);
                            }
                        }
                    });
            recyclerView.setAdapter(mybooksadapter);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setLayoutManager(new GridLayoutManager(ViewActivity.this,4));
            recyclerView.setHasFixedSize(true);
            recyclerView.setItemViewCacheSize(10);
            recyclerView.setDrawingCacheEnabled(true);
            recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
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
    /// gets the id of user clicked on from Posts adapter
    ///(see "onClick" in Posts adapter line 325)
     public void ToProfile(String id){
        Intent intent = new Intent(ViewActivity.this, ViewProfile.class);
        intent.putExtra("ID", id);
        startActivity(intent);
    }

    //////////////// these don't work //////////////
    private void hideFloatingActionButton(FloatingActionButton fab) {
        CoordinatorLayout.LayoutParams params =
                (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        FloatingActionButton.Behavior behavior =
                (FloatingActionButton.Behavior) params.getBehavior();

        if (behavior != null) {
            behavior.setAutoHideEnabled(false);
        }

        fab.hide();
    }

    private void showFloatingActionButton(FloatingActionButton fab) {
        fab.show();
        CoordinatorLayout.LayoutParams params =
                (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        FloatingActionButton.Behavior behavior =
                (FloatingActionButton.Behavior) params.getBehavior();

        if (behavior != null) {
            behavior.setAutoHideEnabled(true);
        }
    }
}