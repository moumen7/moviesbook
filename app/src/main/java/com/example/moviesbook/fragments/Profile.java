package com.example.moviesbook.fragments;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.drm.DrmManagerClient;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.MimeTypeMap;
import android.widget.Adapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moviesbook.Activity.AddActivity;
import com.example.moviesbook.Activity.AddPicActivity;
import com.example.moviesbook.Activity.ViewActivity;
import com.example.moviesbook.Activity.followersorfollowing;
import com.example.moviesbook.Adapter.BooksAdapter;
import com.example.moviesbook.Adapter.ListsAdapter;
import com.example.moviesbook.Adapter.Mybooksadapter;
import com.example.moviesbook.Adapter.Mymoviesadapter;
import com.example.moviesbook.Adapter.PostsAdapter;
import com.example.moviesbook.Book;
import com.example.moviesbook.EndDetectingScrollView;
import com.example.moviesbook.Friend;
import com.example.moviesbook.Interfaces.ClickListener;
import com.example.moviesbook.Movie;
import com.example.moviesbook.R;
import com.example.moviesbook.Userdata;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import  com.example.moviesbook.List;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Chats#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Profile extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView user;
    Uri ImageData;
    private SharedPreferences sp;
    private TextView followers;
    private TextView following;
    private int recbottom = 0;

    private TextView AddMovies;
    private TextView AddBooks;
    private TextView ViewMovies;
    private TextView ViewBooks;
    private FirebaseFirestore db;
    Query   q;
    private ImageView imageView;
    private ListsAdapter adapter;
    private ListsAdapter adapter2;
    private PostsAdapter postsAdapter;
    private RecyclerView recyclerViewmovies;
    private RecyclerView recyclerViewbooks;
    DocumentSnapshot lastVisible;
    private RecyclerView recyclerViewposts;
    private StorageReference Folder;
    private ArrayList<Book> userbooks;
    private ArrayList<Post> posts;
    private ArrayList<Movie> usermovies;
    private ScrollView scrollView;
    private ArrayList<List> lists;
    private ArrayList<List> lists2;
    List Firstlist;
    String img;


    public Profile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Chats.
     */
    // TODO: Rename and change types and number of parameters
    public static Chats newInstance(String param1, String param2) {
        Chats fragment = new Chats();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        db = FirebaseFirestore.getInstance();

        followers = view.findViewById(R.id.followers);
        scrollView = view.findViewById(R.id.scroll);
        Folder = FirebaseStorage.getInstance().getReference("Images");
        following = view.findViewById(R.id.following);
        imageView = view.findViewById(R.id.profiepic);
        ViewMovies = view.findViewById(R.id.Movieslist);
        Firstlist = new List();
        ViewBooks = view.findViewById(R.id.Bookslist);
        img = new String();
        userbooks = new ArrayList<>();
        usermovies= new ArrayList<>();
        lists = new ArrayList<>();
        lists2 = new ArrayList<>();
        posts = new ArrayList<>();
        recyclerViewbooks = view.findViewById(R.id.profile_favsbooks);
        recyclerViewmovies = view.findViewById(R.id.profile_favsmovies);
        recyclerViewposts =  view.findViewById(R.id.myposts);
        recyclerViewmovies.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
        recyclerViewbooks.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
        recyclerViewposts.setLayoutManager(new LinearLayoutManager(getContext()));
        following.setOnClickListener(this);
        followers.setOnClickListener(this);
        imageView.setOnClickListener(this);
        ViewMovies.setOnClickListener(this);
        ViewBooks.setOnClickListener(this);
        user = view.findViewById(R.id.username);
        sp = this.getActivity().getSharedPreferences("user", MODE_PRIVATE);
        adapter = new ListsAdapter(getContext(),new ClickListener() {
            @Override public void onPositionClicked(int position) {

            }

            @Override public void onLongClicked(int position) {
            }
        },"Movie",sp.getString("ID",""));
        adapter2 = new ListsAdapter(getContext(),new ClickListener() {
            @Override public void onPositionClicked(int position) {

            }

            @Override public void onLongClicked(int position) {
            }
        },"Book",sp.getString("ID",""));

        postsAdapter = new PostsAdapter(getContext(), new ClickListener() {
            @Override
            public void onPositionClicked(int position) {

            }

            @Override
            public void onLongClicked(int position) {
            }
        });
        q = db.collection("Users").document(sp.getString("ID",""))
        .collection("MoviesList");

        q
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e)
                    {
                        lists.clear();;
                        Firstlist = new List("",null,"Favorite movies");
                        lists.add(Firstlist);
                            int x = 0;

                            for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
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
                            Firstlist = new List("add",null,"Add List");
                            lists.add(Firstlist);
                            adapter.setList(lists);
                        }
                });

        recyclerViewmovies.setAdapter(adapter);
        recyclerViewmovies.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.HORIZONTAL));
        q = db.collection("Users").document(sp.getString("ID",""))
                .collection("BooksList");
        q
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e)
                    {
                        lists2.clear();
                    Firstlist = new List("",null,"Favorite Books");
                        lists2.add(Firstlist);
                            int x = 0;
                            for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots)
                            {
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
                            Firstlist = new List("add",null,"Add List");
                            lists2.add(Firstlist);
                            adapter2.setList(lists2);
                        }

                });
        recyclerViewbooks.setAdapter(adapter2);
        recyclerViewbooks.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.HORIZONTAL));

        db.collection("Users").document(sp.getString("ID",""))
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (documentSnapshot.get("numoffollowers") != null) {
                            String x = String.valueOf((long) documentSnapshot.getData().get("numoffollowers"));
                            followers.setText(x + " Followers");
                        } else {
                            followers.setText(0 + " Followers");

                        }

                    }

                });
        db.collection("Users").document(sp.getString("ID",""))
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (documentSnapshot.get("numoffollowing") != null) {
                            String x = String.valueOf((long) documentSnapshot.getData().get("numoffollowing"));
                            following.setText(x + " Following");
                        } else {
                            following.setText(0 + " Following");

                        }

                    }

        });

        user.setText(sp.getString("username",""));
        Query query = db.collection("Users").whereEqualTo("id" , sp.getString("ID", ""));

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {


                int x = 0;

                for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                    Friend ChatUser = snapshot.toObject(Friend.class);
                    if (ChatUser.getId() != null) {
                        if (ChatUser.getId().equals(sp.getString("ID", ""))) {
                            String s = ChatUser.getImage();
                            String s2 = "https://i.stack.imgur.com/l60Hf.png";

                            if (s == null) {
                                Picasso.get().load(s2).into(imageView);

                            } else {
                                Picasso.get().load(s).into(imageView);
                            }
                        }
                    }
                }
            }
        });
        q = db.collection("Posts").
                whereEqualTo("userid",sp.getString("ID","")).orderBy("Date", Query.Direction.DESCENDING)
                .limit(3);
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
                                                                            whereEqualTo("userid", sp.getString("ID", "")).orderBy("Date", Query.Direction.DESCENDING)
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

        recyclerViewposts.setAdapter(postsAdapter);
        recyclerViewposts.setNestedScrollingEnabled(false);
        return view;

    }
    public void filechooser()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {

            ImageData = data.getData();
            imageView.setImageURI(ImageData);
            final StorageReference imgname = Folder.child(System.currentTimeMillis()
                    + "." + getextension(ImageData));

            imgname.putFile(ImageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imgname.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            SharedPreferences sp = getActivity().getSharedPreferences("user",MODE_PRIVATE);
                            FirebaseFirestore fb = FirebaseFirestore.getInstance();
                            fb.collection("Users").document(sp.getString("ID","")).
                                    update("image",String.valueOf(uri));
                        }
                    });

                }
            });
        }
    }
    public String getextension(Uri uri)
    {
        ContentResolver cr = getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(ImageData));
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.following:
                Intent intent = new Intent(getActivity(), followersorfollowing.class);
                intent.putExtra("Following",true);
                startActivity(intent);
                break;
            case R.id.followers:
                intent = new Intent(getActivity(), followersorfollowing.class);
                startActivity(intent);
                break;
            case R.id.profiepic:
                filechooser();
                break;
        }
    }

}