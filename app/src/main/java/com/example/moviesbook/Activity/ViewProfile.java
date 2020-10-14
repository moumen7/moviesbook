package com.example.moviesbook.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moviesbook.Adapter.ListsAdapter;
import com.example.moviesbook.Adapter.Mybooksadapter;
import com.example.moviesbook.Adapter.Mymoviesadapter;
import com.example.moviesbook.Adapter.PostsAdapter;
import com.example.moviesbook.Book;
import com.example.moviesbook.Friend;
import com.example.moviesbook.Interfaces.ClickListener;
import com.example.moviesbook.List;
import com.example.moviesbook.Movie;
import com.example.moviesbook.R;
import com.example.moviesbook.Userdata;
import com.example.moviesbook.fragments.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.example.moviesbook.Userdata.Userbooks;

public class ViewProfile extends AppCompatActivity implements View.OnClickListener {
    private String mParam1;
    private String mParam2;
    private TextView user;
    Uri ImageData;
    private SharedPreferences sp;
    private TextView followers;
    private ArrayList<Post> posts;
    private TextView following;
    private TextView AddMovies;
    List Firstlist;
    private String img = new String();
    private TextView AddBooks;
    private TextView ViewMovies;
    private TextView ViewBooks;
    private FirebaseFirestore db;
    private ArrayList<List> lists;
    private ArrayList<List> lists2;
    Query q;
    private DocumentSnapshot lastVisible;
    private ImageView imageView;
    private ImageButton imageButton;
    private ListsAdapter adapter;
    private ListsAdapter adapter2;
    private PostsAdapter postsAdapter;
    private RecyclerView recyclerViewmovies;
    private RecyclerView recyclerViewbooks;
    private RecyclerView recyclerViewposts;
    private StorageReference Folder;
    private ArrayList<Book> userbooks;
    private ArrayList<Movie> usermovies;
    String m;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        db = FirebaseFirestore.getInstance();
        adapter = new ListsAdapter(ViewProfile.this,new ClickListener() {
            @Override public void onPositionClicked(int position) {

            }

            @Override public void onLongClicked(int position) {
            }
        },"Movie",getIntent().getStringExtra("ID"));
        adapter2 = new ListsAdapter(ViewProfile.this,new ClickListener() {
            @Override public void onPositionClicked(int position) {

            }

            @Override public void onLongClicked(int position) {
            }
        },"Book",getIntent().getStringExtra("ID"));
        postsAdapter = new PostsAdapter(ViewProfile.this,new ClickListener() {
            @Override public void onPositionClicked(int position) {

            }

            @Override public void onLongClicked(int position) {
            }
        });

        followers = findViewById(R.id.followers);
        imageButton = findViewById(R.id.imgbutton);
        Folder = FirebaseStorage.getInstance().getReference("Images");
        following = findViewById(R.id.following);
        imageView = findViewById(R.id.profiepic);
        userbooks = new ArrayList<>();
        usermovies= new ArrayList<>();
        posts = new ArrayList<>();
        followers.setOnClickListener(this);
        lists = new ArrayList<>();
        lists2 = new ArrayList<>();
        following.setOnClickListener(this);
        Firstlist = new List();
        recyclerViewbooks = findViewById(R.id.profile_favsbooks);
        recyclerViewmovies = findViewById(R.id.profile_favsmovies);
        recyclerViewposts = findViewById(R.id.myposts);
        recyclerViewmovies.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
        recyclerViewbooks.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
        user = findViewById(R.id.username);
        sp = getSharedPreferences("user", MODE_PRIVATE);
        m = new String();
        q = db.collection("Users").document(getIntent().getStringExtra("ID"))
                .collection("MoviesList");

        q.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Firstlist = new List("",null,"Favorite movies");
                        lists.add(Firstlist);
                        if (task.isSuccessful()) {
                            int x = 0;
                            for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                List ChatUser = snapshot.toObject(List.class);
                                if(!snapshot.getId().equals("favorites122"))
                                {
                                    lists.add(ChatUser);
                                }
                                else
                                {
                                    lists.get(0).setNumber(ChatUser.getNumber());
                                }
                                x++;
                            }

                            adapter.setList(lists);
                        }
                    }
                });

        recyclerViewmovies.setAdapter(adapter);
        recyclerViewmovies.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.HORIZONTAL));
        q = db.collection("Users").document(getIntent().getStringExtra("ID"))
                .collection("BooksList");
        q.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Firstlist = new List("",null,"Favorite Books");
                        lists2.add(Firstlist);
                        if (task.isSuccessful())
                        {
                            int x = 0;
                            for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                List ChatUser = snapshot.toObject(List.class);

                                if(!snapshot.getId().equals("favorites122"))
                                {
                                    lists2.add(ChatUser);
                                }
                                else
                                {
                                    lists2.get(0).setNumber(ChatUser.getNumber());
                                }
                                x++;
                            }

                            adapter2.setList(lists2);
                        }
                    }
                });
        recyclerViewbooks.setAdapter(adapter2);
        recyclerViewbooks.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.HORIZONTAL));
        db.collection("Users").document(getIntent().getStringExtra("ID"))
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                           if(task.getResult().get("numoffollowers") == null)
                           {
                               followers.setText(0 + " Followers");
                           }
                           else
                           {
                               String x = String.valueOf((long) task.getResult().getData().get("numoffollowers"));
                               followers.setText(x + " Followers");
                           }
                        } else {
                            followers.setText(0 + " Followers");
                        }
                    }
                });
        db.collection("Users").document(getIntent().getStringExtra("ID"))
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    if(task.getResult().get("numoffollowing") == null)
                    {
                        following.setText(0 + " Following");
                    }
                    else
                    {
                        String x = String.valueOf((long) task.getResult().getData().get("numoffollowing"));
                        following.setText(x + " Following");
                    }
                } else {
                    following.setText(0 + " Following");
                }
            }
        });
        q = db.collection("Posts").whereEqualTo("userid",getIntent().getStringExtra("ID"))
        .orderBy("Date", Query.Direction.DESCENDING).limit(5);
        DocumentReference docRef = db.collection("Users").document(getIntent().getStringExtra("ID"));
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                img = documentSnapshot.toObject(Friend.class).getImage();

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
            }
        });

        recyclerViewposts.setAdapter(postsAdapter);
        recyclerViewposts.setLayoutManager(new LinearLayoutManager(ViewProfile.this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        recyclerViewposts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager=LinearLayoutManager.class.cast(recyclerView.getLayoutManager());
                int totalItemCount = layoutManager.getItemCount();
                int LastVisible = layoutManager.findLastVisibleItemPosition();

                boolean endHasBeenReached = !recyclerView.canScrollVertically(1);
                if (totalItemCount > 0 && endHasBeenReached) {
                    q = db.collection("Posts").
                            whereEqualTo("userid",getIntent().getStringExtra("ID")).orderBy("Date", Query.Direction.DESCENDING)
                            .limit(5).startAfter(lastVisible);
                    q.get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot documentSnapshots) {
                                    if(documentSnapshots.size()!=0) {
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
        });
        Query query = db.collection("Users").whereEqualTo("id",getIntent().getStringExtra("ID"));
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {


                int x = 0;
                for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                    Friend ChatUser = snapshot.toObject(Friend.class);
                    if (ChatUser.getId() != null) {
                        if (ChatUser.getId().equals(getIntent().getStringExtra("ID"))) {
                            String s = ChatUser.getImage();
                            String s2 = "https://i.stack.imgur.com/l60Hf.png";
                            user.setText(ChatUser.getUsername());
                            if (s == null) {
                                Picasso.get().load(s2).into(imageView);
                                m = s2;
                            } else {
                                Picasso.get().load(s).into(imageView);
                                m = s;
                            }
                        }
                    }
                }
            }
        });

    }
    public void follow(View v)
    {
        if(Userdata.following.containsKey(getIntent().getStringExtra("ID")))
        {
            imageButton.setImageResource(R.drawable.ic_baseline_person_add_24);
            db.collection("Users").document(sp.getString("ID",""))
                    .update("Following", FieldValue.arrayRemove(getIntent().getStringExtra("ID")));
            db.collection("Users").document(getIntent().getStringExtra("ID"))
                    .update("Followers", FieldValue.arrayRemove(sp.getString("ID","")));
            db.collection("Users").document(sp.getString("ID",""))
                    .update("numoffollowing", FieldValue.increment(-1));
            db.collection("Users").document(getIntent().getStringExtra("ID"))
                    .update("numoffollowers", FieldValue.increment(-1));
            Userdata.following.remove(getIntent().getStringExtra("ID"));
        }
        else
        {

            imageButton.setImageResource(R.drawable.ic_done_black_24dp);
            db.collection("Users").document(sp.getString("ID",""))
                    .update("Following", FieldValue.arrayUnion(getIntent().getStringExtra("ID")));
            db.collection("Users").document(getIntent().getStringExtra("ID"))
                    .update("Followers", FieldValue.arrayUnion(sp.getString("ID","")));
            db.collection("Users").document(sp.getString("ID",""))
                    .update("numoffollowing", FieldValue.increment(1));
            db.collection("Users").document(getIntent().getStringExtra("ID"))
                    .update("numoffollowers", FieldValue.increment(1));

            Userdata.following.put(getIntent().getStringExtra("ID"),true);
        }


    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }
}
