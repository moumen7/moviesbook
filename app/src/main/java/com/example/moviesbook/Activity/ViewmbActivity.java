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

import com.example.moviesbook.Adapter.BooksAdapter;
import com.example.moviesbook.Adapter.MovieAdapter;
import com.example.moviesbook.Adapter.Mybooksadapter;
import com.example.moviesbook.Adapter.Mymoviesadapter;
import com.example.moviesbook.Adapter.PostsAdapter;
import com.example.moviesbook.Book;
import com.example.moviesbook.Interfaces.ClickListener;
import com.example.moviesbook.Json_Books.BooksResult;
import com.example.moviesbook.Json_Books.Item;
import com.example.moviesbook.Movie;
import com.example.moviesbook.MovieResults;
import com.example.moviesbook.MovieRsults2;
import com.example.moviesbook.Movies2;
import com.example.moviesbook.R;
import com.example.moviesbook.Result;
import com.example.moviesbook.ViewModel.BooksViewModel;
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
import java.util.List;

public class ViewmbActivity extends AppCompatActivity {
    TextView name;
    ImageView Image;
    TextView desc;
    TextView favorites;
    FirebaseFirestore db;
    Query q;
    MoviesViewModel moviesViewModel;
    ArrayList<Movie> movies;
    ArrayList<Book> books;
    BooksViewModel booksViewModel;
    DocumentSnapshot lastVisible;
    PostsAdapter postsAdapter;
    Mymoviesadapter movieAdapter;
    private List<Movie> MoviesItems = new ArrayList<>();
    Mybooksadapter booksAdapter;
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
        books = new ArrayList<>();

        desc = findViewById(R.id.Desc);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        recyclerViewPosts = findViewById(R.id.myposts);
        movieAdapter = new Mymoviesadapter(ViewmbActivity.this,new ClickListener() {
            @Override
            public void onPositionClicked(int position) {

            }

            @Override
            public void onLongClicked(int position) {

            }
        });

        booksAdapter = new Mybooksadapter(ViewmbActivity.this,new ClickListener() {
            @Override
            public void onPositionClicked(int position) {

            }

            @Override
            public void onLongClicked(int position) {

            }
        });
        posts = new ArrayList<>();
        Image = findViewById(R.id.image);
        movies = new ArrayList<>();
        scrollView = findViewById(R.id.scroll);
        recyclerViewPosts.setLayoutManager(new LinearLayoutManager(ViewmbActivity.this));
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        moviesViewModel = ViewModelProviders.of(this).get(MoviesViewModel.class);
        booksViewModel = ViewModelProviders.of(this).get(BooksViewModel.class);
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
                        if (getIntent().getStringExtra("Choice").equals("Movies")) {
                            moviesViewModel.getMovies2(Integer.parseInt(getIntent().getStringExtra("ID")));
                            moviesViewModel.MoviesMutable2.observe(ViewmbActivity.this, new Observer<Movies2>() {
                                @Override
                                public void onChanged(Movies2 postModels) {
                                    desc.setText(postModels.getOverview());
                                    Picasso.get().load("http://image.tmdb.org/t/p/original" + postModels.getPosterPath()).into(Image);
                                    favorites.setText(String.valueOf("0 Favorites"));
                                }
                            });
                        }
                        else
                        {
                            booksViewModel.getBook(getIntent().getStringExtra("ID"));
                            booksViewModel.BooksMutable.observe(ViewmbActivity.this, new Observer<BooksResult>() {
                                @Override
                                public void onChanged(BooksResult postModels) {
                                    desc.setText(postModels.getItems().get(0).getVolumeInfo().getSubtitle());
                                    if(postModels.getItems().get(0).getVolumeInfo().getImageLinks().getThumbnail() !=null)
                                        Picasso.get().load( postModels.getItems().get(0).getVolumeInfo().getImageLinks().getThumbnail()
                                        ).into(Image);
                                    favorites.setText(String.valueOf("0 Favorites"));
                                }
                            });
                        }
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
                    for ( Result x:postModels.getResults())
                    {
                        Movie add = new Movie(x.getId().toString(),"http://image.tmdb.org/t/p/original" + x.getPosterPath(),x.getTitle());
                        movies.add(add);
                    }
                    movieAdapter.setList(movies);
                }
            });
        }
        else
        {
            rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            rv.setAdapter(booksAdapter);
            booksViewModel.getBooks(getIntent().getStringExtra("name"));
            booksViewModel.BooksMutable.observe(ViewmbActivity.this, new Observer<BooksResult>() {

                @Override
                public void onChanged(BooksResult postModels) {
                    for ( Item x:postModels.getItems())
                    {
                        Book add = new Book(x.getId().toString(),x.getVolumeInfo().getImageLinks().getThumbnail(),x.getVolumeInfo().getTitle());
                        books.add(add);
                    }
                    booksAdapter.setList(books);
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
