package com.example.moviesbook.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.moviesbook.Activity.MoviesorBooks;
import com.example.moviesbook.Adapter.PostsAdapter;
import com.example.moviesbook.Book;
import com.example.moviesbook.Friend;
import com.example.moviesbook.Interfaces.ClickListener;
import com.example.moviesbook.R;
import com.example.moviesbook.Userdata;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Feed#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Feed extends Fragment implements View.OnClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView feedrecyclerview;
    private FirebaseFirestore db;
    private ArrayList<Post> posts;
    private SharedPreferences sp;
    private FloatingActionButton post;
    PostsAdapter postsAdapter;
    Query q;
    private RecyclerView recyclerViewposts;

    public Feed() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Feed.
     */
    // TODO: Rename and change types and number of parameters
    public static Feed newInstance(String param1, String param2) {
        Feed fragment = new Feed();
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

        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        post = (FloatingActionButton) view.findViewById(R.id.fabpost);
        post.setOnClickListener(this);
        recyclerViewposts = view.findViewById(R.id.posts);
        db = FirebaseFirestore.getInstance();
        sp = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        posts = new ArrayList<>();
        postsAdapter = new PostsAdapter(getContext(), new ClickListener() {
            @Override
            public void onPositionClicked(int position) {

            }

            @Override
            public void onLongClicked(int position) {

            }
        });
        q = db.collection("Users").document(sp.getString("ID","")).
                collection("Following");

        q.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                Userdata.following.clear();
                int x = 0;
                for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                    Friend ChatUser = snapshot.toObject(Friend.class);
                    Userdata.following.put(snapshot.getId(), true);
                    x++;
                }
                q = db.collection("Posts").orderBy("Date", Query.Direction.DESCENDING);

                q.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        posts.clear();
                        int x = 0;
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                            Post ChatUser = snapshot.toObject(Post.class);
                            if(Userdata.following.containsKey(ChatUser.getUserid()))
                            {
                                posts.add(ChatUser);
                            }
                            x++;
                        }
                        postsAdapter.setList(posts);
                    }
                });

            }
        });
        recyclerViewposts.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));
        recyclerViewposts.setAdapter(postsAdapter);

        return view;

    }
    @Override
    public void onClick(View v) {
        if(v.getId() == post.getId())
        {
            Intent intent = new Intent(getContext(), MoviesorBooks.class);
            startActivity(intent);
        }
    }
}
