package com.example.moviesbook.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.graphics.Color;
import android.icu.text.CaseMap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.example.moviesbook.Example;
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
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.protobuf.StringValue;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ViewmbActivity extends AppCompatActivity {
    private static final String TAG = "ViewmbActivity";
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
    FloatingActionButton button;
    private List<Movie> MoviesItems = new ArrayList<>();
    Mybooksadapter booksAdapter;
    TextView title;
    RecyclerView recyclerViewPosts;
    TextView getName;
    ScrollView scrollView;
    ConstraintLayout cs ;
    ArrayList <Post> posts;
    RecyclerView rv;
    TextView posttext;
    CollapsingToolbarLayout collapsingToolbarLayout;
    int recbottom = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewmb);
        cs = findViewById(R.id.cons);
        rv = findViewById(R.id.recommendations);
        favorites = findViewById(R.id.Favorites);
        title = findViewById(R.id.title);
        getName = (TextView) findViewById(R.id.NameOfChoosen);
        db = FirebaseFirestore.getInstance();
        books = new ArrayList<>();
        button = findViewById(R.id.Add);
        posttext = findViewById(R.id.posttext);
        Image = findViewById(R.id.image);
        desc = findViewById(R.id.Desc);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        recyclerViewPosts = findViewById(R.id.myposts);

        /// show only title of movie or book in toolbar when collapsing
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.cardInfo_collapsing_mb);
        collapsingToolbarLayout.setTitleEnabled(false);
        final androidx.appcompat.widget.Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_mb);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.cardInfo_appbar_mb);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    title.setVisibility(View.GONE);

                    favorites.setVisibility(View.GONE);
                    button.hide();
                    toolbar.setTitle(getIntent().getStringExtra("name"));
                    Image.setVisibility(View.GONE);
                    isShow = true;
                    Log.w(TAG, "Collapsing toolbar shown.");
                } else if(isShow) {
                    isShow = false;
                    title.setVisibility(View.VISIBLE);
                    favorites.setVisibility(View.VISIBLE);
                    button.show();
                    toolbar.setTitle(getIntent().getStringExtra("name"));
                    Image.setVisibility(View.VISIBLE);
                }
            }
        });

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
        movies = new ArrayList<>();
        scrollView = findViewById(R.id.scroll);
        recyclerViewPosts.setLayoutManager(new LinearLayoutManager(ViewmbActivity.this));
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        moviesViewModel = ViewModelProviders.of(this).get(MoviesViewModel.class);
        booksViewModel = ViewModelProviders.of(this).get(BooksViewModel.class);
        DocumentReference docIdRef =db. collection(getIntent().getStringExtra("Choice")).document(
                String.valueOf(getIntent().getStringExtra("ID")));

        if (getIntent().getStringExtra("Choice").equals("Movies")) {
            moviesViewModel.getMovies2(Integer.parseInt(getIntent().getStringExtra("ID")));
            moviesViewModel.MoviesMutable2.observe(ViewmbActivity.this, new Observer<Movies2>() {
                @Override
                public void onChanged(Movies2 postModels) {
                    desc.setText(postModels.getOverview());
                    Picasso.get().load("http://image.tmdb.org/t/p/original" + postModels.getPosterPath()).into(Image);
                    favorites.setText(String.valueOf("0 Favorites"));
                    if(postModels.getTitle()!=null)
                    title.setText(String.valueOf(postModels.getTitle()));
                    if(postModels.getReleaseDate() != null)
                    {
                        title.setText(postModels.getTitle()
                                + " (" +  postModels.getReleaseDate().substring(0,4) + ")");
                    }
                }
            });
        }
        else
        {
            booksViewModel.getBook(getIntent().getStringExtra("ID"));
            booksViewModel.BooksMutable2.observe(ViewmbActivity.this, new Observer<Example>() {
                @Override
                public void onChanged(Example postModels) {
                    if(postModels.getVolumeInfo().getDescription()!= null)
                    desc.setText(postModels.getVolumeInfo().getDescription());
                    if(postModels.getVolumeInfo().getImageLinks().getThumbnail() !=null)
                        Picasso.get().load( postModels.getVolumeInfo().getImageLinks().getThumbnail()
                        ).into(Image);
                    favorites.setText(String.valueOf("0 Favorites"));
                    title.setText(postModels.getVolumeInfo().getTitle());
                    if(postModels.getVolumeInfo().getPublishedDate() != null)
                    {
                        title.setText(postModels.getVolumeInfo().getTitle()
                                + " (" +  postModels.getVolumeInfo().getPublishedDate().substring(0,4) + ")");
                    }
                }
            });
        }
        docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists())
                    {
                        //getName.setText((String.valueOf(document.get("Name"))));

                        String add = String.valueOf(document.get("favs"));
                        if(!add.equals("null"))
                            favorites.setText(add + " Favorites");
                        else
                            favorites.setText("0" + " Favorites");
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
                        if(posts.size() > 0)
                        {
                            posttext.setVisibility(View.VISIBLE);
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

    /// this one here needs to have a body...
    public void go(View view) {
        if(view.getId() == R.id.Add)
        {
            Intent intent = new Intent(ViewmbActivity.this,ListActivity.class);
            intent.putExtra("choice", getIntent().getStringExtra("Choice"));
            intent.putExtra("id",getIntent().getStringExtra("ID"));
            startActivity(intent);
        }
    }
}
