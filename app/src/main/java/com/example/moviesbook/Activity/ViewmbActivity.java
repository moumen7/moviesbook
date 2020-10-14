package com.example.moviesbook.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.moviesbook.Adapter.MovieAdapter;
import com.example.moviesbook.Adapter.Mymoviesadapter;
import com.example.moviesbook.Adapter.PostsAdapter;
import com.example.moviesbook.Interfaces.ClickListener;
import com.example.moviesbook.MovieResults;
import com.example.moviesbook.MovieRsults2;
import com.example.moviesbook.Movies2;
import com.example.moviesbook.R;
import com.example.moviesbook.ViewModel.MoviesViewModel;
import com.example.moviesbook.fragments.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;

public class ViewmbActivity extends AppCompatActivity {
    TextView name;
    ImageView Image;
    TextView desc;
    TextView favorites;
    FirebaseFirestore db;
    Query q;
    MoviesViewModel moviesViewModel;
    DocumentSnapshot lastVisible;
    PostsAdapter postsAdapter;
    MovieAdapter movieAdapter;
    RecyclerView recyclerViewPosts;
    ScrollView scrollView;
     ArrayList <Post> posts;
    RecyclerView rv;
    int recbottom = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewmb);
        rv = findViewById(R.id.recommendations);
        favorites = findViewById(R.id.Favorites);
        db = FirebaseFirestore.getInstance();
        desc = findViewById(R.id.Desc);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        recyclerViewPosts = findViewById(R.id.myposts);
        movieAdapter = new MovieAdapter(ViewmbActivity.this,new ClickListener() {
            @Override public void onPositionClicked(int position) {

            }

            @Override public void onLongClicked(int position) {
            }
        });
        posts = new ArrayList<>();
        Image = findViewById(R.id.image);
        scrollView = findViewById(R.id.scroll);
        recyclerViewPosts.setLayoutManager(new LinearLayoutManager(ViewmbActivity.this));
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        moviesViewModel = ViewModelProviders.of(this).get(MoviesViewModel.class);
        DocumentReference docIdRef =db. collection(getIntent().getStringExtra("Choice")).document(
                String.valueOf(getIntent().getStringExtra("ID")));
        docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists())
                    {
                        desc.setText((String.valueOf(document.get("Desc"))));
                        Picasso.get().load(String.valueOf(document.get("Image"))).into(Image);
                        String add = String.valueOf(document.get("favs"));
                        if(!add.equals("null"))
                        favorites.setText(add + " Favorites");
                        else
                            favorites.setText("0" + " Favorites");
                    }
                    else {
                        moviesViewModel.getMovies2(Integer.parseInt(getIntent().getStringExtra("ID")));
                        moviesViewModel.MoviesMutable2.observe(ViewmbActivity.this, new Observer<Movies2>() {
                            @Override
                            public void onChanged(Movies2 postModels) {
                                desc.setText(postModels.getOverview());
                                Picasso.get().load("http://image.tmdb.org/t/p/original"+postModels.getPosterPath()).into(Image);
                                favorites.setText(String.valueOf("0 Favorites"));
                            }
                        });
                    }
                } else {

                }
            }
        });
        postsAdapter = new PostsAdapter(ViewmbActivity.this, new ClickListener() {
            @Override
            public void onPositionClicked(int position) {

            }

            @Override
            public void onLongClicked(int position) {

            }
        });
        if(getIntent().getStringExtra("Choice").equals("Movies"))
        {
            rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            rv.setAdapter(movieAdapter);
            moviesViewModel.getRecommendations(Integer.parseInt(getIntent().getStringExtra("ID")));
            moviesViewModel.MoviesMutable3.observe(ViewmbActivity.this, new Observer<MovieRsults2>() {

                @Override
                public void onChanged(MovieRsults2 postModels) {
                    movieAdapter.setList(postModels.getResults());
                }
            });
        }
        q = db.collection("Posts").
                whereEqualTo("usedid",getIntent().getStringExtra("ID"))
                .limit(1);
        q.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {

                        for(QueryDocumentSnapshot qs:documentSnapshots)
                        {
                            Post ChatUser = qs.toObject(Post.class);


                            posts.add(ChatUser);

                        }
                        if(documentSnapshots.size()!=0) {
                            lastVisible = documentSnapshots.getDocuments()
                                    .get(documentSnapshots.size() - 1);
                        }
                        postsAdapter.setList(posts);

                    }
                });


        scrollView.getViewTreeObserver()
                .addOnScrollChangedListener(new
                                                    ViewTreeObserver.OnScrollChangedListener() {
                                                        @Override
                                                        public void onScrollChanged() {

                                                            if (scrollView.getChildAt(0).getBottom()
                                                                    == (scrollView.getHeight() + scrollView.getScrollY()) && posts.size()!=0)
                                                            {
                                                                if(recbottom != scrollView.getChildAt(0).getBottom()) {
                                                                    recbottom = scrollView.getChildAt(0).getBottom();
                                                                    q = db.collection("Posts").
                                                                            whereEqualTo("usedid",getIntent().getStringExtra("ID"))
                                                                            .limit(3).startAfter(lastVisible);
                                                                    q.get()
                                                                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                                                @Override
                                                                                public void onSuccess(QuerySnapshot documentSnapshots) {
                                                                                    if (documentSnapshots.size() != 0) {

                                                                                        lastVisible = documentSnapshots.getDocuments()
                                                                                                .get(documentSnapshots.size() - 1);
                                                                                        for (QueryDocumentSnapshot qs : documentSnapshots) {
                                                                                            Post ChatUser = qs.toObject(Post.class);
                                                                                            posts.add(ChatUser);
                                                                                        }
                                                                                        postsAdapter.setList(posts);
                                                                                    }

                                                                                }
                                                                            });
                                                                }
                                                            }
                                                            if (!scrollView.canScrollVertically(-1)) {
                                                                // top of scroll view


                                                            }
                                                        }
                                                    });

        recyclerViewPosts.setAdapter(postsAdapter);
        recyclerViewPosts.setNestedScrollingEnabled(false);

    }
}
