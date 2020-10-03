package com.example.moviesbook.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.moviesbook.Activity.MoviesorBooks;
import com.example.moviesbook.Adapter.PostsAdapter;
import com.example.moviesbook.Book;
import com.example.moviesbook.Friend;
import com.example.moviesbook.Interfaces.ClickListener;
import com.example.moviesbook.R;
import com.example.moviesbook.User;
import com.example.moviesbook.Userdata;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
    Task [] tasks;
    Boolean end;
    int sum = 0;
    int last = 0;
    private RecyclerView feedrecyclerview;
    private FirebaseFirestore db;
    private DocumentSnapshot[] lastVisible;
    private List<Post> posts;
    private SharedPreferences sp;
    private boolean[] enough;
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
    public static Task[] removeTheElement(Task[] arr,
                                         int index)
    {

        // If the array is empty
        // or the index is not in array range
        // return the original array
        if (arr == null
                || index < 0
                || index >= arr.length) {

            return arr;
        }

        // Create another array of size one less
        Task[] anotherArray = new Task[arr.length - 1];

        // Copy the elements from starting till index
        // from original array to the other array
        System.arraycopy(arr, 0, anotherArray, 0, index);

        // Copy the elements from index + 1 till end
        // from original array to the other array
        System.arraycopy(arr, index + 1,
                anotherArray, index,
                arr.length - index - 1);

        // return the resultant array
        return anotherArray;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        post = (FloatingActionButton) view.findViewById(R.id.fabpost);
        post.setOnClickListener(this);
        recyclerViewposts = view.findViewById(R.id.posts);
        recyclerViewposts.setLayoutManager(new LinearLayoutManager(getContext()));
        db = FirebaseFirestore.getInstance();
        end = false;
        sp = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        posts = new ArrayList<Post>();
        postsAdapter = new PostsAdapter(getContext(), new ClickListener() {
            @Override
            public void onPositionClicked(int position) {

            }

            @Override
            public void onLongClicked(int position) {

            }
        });
        q = db.collection("Users").whereArrayContains("Followers",sp.getString("ID",""));

        q.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Userdata.following2.clear();
                            int x = 0;
                            for (DocumentSnapshot snapshot : task.getResult()) {
                                Friend ChatUser = snapshot.toObject(Friend.class);
                                Userdata.following2.add(snapshot.getId());
                                x++;
                            }

                        }
                        Toast.makeText(getActivity(),String.valueOf(Userdata.following2.size()),Toast.LENGTH_LONG).show();
                        tasks = new Task[Userdata.following2.size()];
                        enough = new boolean[Userdata.following2.size()];
                        lastVisible = new DocumentSnapshot[Userdata.following2.size()];
                        recyclerViewposts.setAdapter(postsAdapter);
                        int i=0;
                        for(String x:Userdata.following2)
                        {
                            tasks[i] = db.collection("Posts").whereEqualTo("userid",x).orderBy("Date")
                                    .limit(2)
                                    .get();
                            i++;

                        }
                        Task<List<QuerySnapshot>> allTasks = Tasks.whenAllSuccess(tasks);
                        allTasks.addOnSuccessListener(new OnSuccessListener<List<QuerySnapshot>>() {
                            @Override
                            public void onSuccess(List<QuerySnapshot> querySnapshots) {
                                String data = "";
                                int i=0;
                                for (QuerySnapshot queryDocumentSnapshots : querySnapshots) {

                                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                        Post ChatUser = documentSnapshot.toObject(Post.class);
                                        posts.add(ChatUser);
                                    }
                                    if(queryDocumentSnapshots.size() > 0) {
                                        lastVisible[i] = queryDocumentSnapshots.getDocuments()
                                                .get(queryDocumentSnapshots.size() - 1);
                                    }


                                }
                                postsAdapter.setList(posts);

                            }
                        });
                    }
                });
        recyclerViewposts.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewposts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                    LinearLayoutManager layoutManager = LinearLayoutManager.class.cast(recyclerView.getLayoutManager());
                    int totalItemCount = layoutManager.getItemCount();
                    int LastVisible = layoutManager.findLastVisibleItemPosition();
                    boolean endHasBeenReached = !recyclerView.canScrollVertically(1);
                    if (totalItemCount > 0 && endHasBeenReached)
                    {
                        tasks = new Task[Userdata.following2.size()];
                        int i=0;
                        for(String x:Userdata.following2)
                        {
                            if(lastVisible[i]!=null) {
                                tasks[i] = db.collection("Posts").whereEqualTo("userid", x).orderBy("Date")
                                        .startAfter(lastVisible[i]).limit(2)
                                        .get();
                            }
                            else
                            {
                                tasks = removeTheElement(tasks, i);
                                i--;
                            }
                            if(tasks[i] == null)
                            {
                                tasks = removeTheElement(tasks, i);
                                i--;
                            }
                            i++;
                        }
                        Task<List<QuerySnapshot>> allTasks = Tasks.whenAllSuccess(tasks);
                        allTasks.addOnSuccessListener(new OnSuccessListener<List<QuerySnapshot>>() {
                            @Override
                            public void onSuccess(List<QuerySnapshot> querySnapshots) {
                                int k =0;
                                for (QuerySnapshot queryDocumentSnapshots : querySnapshots) {
                                    if(queryDocumentSnapshots.size() >0) {
                                        lastVisible[k] = queryDocumentSnapshots.getDocuments()
                                                .get(queryDocumentSnapshots.size() - 1);
                                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                                            Post ChatUser = documentSnapshot.toObject(Post.class);
                                            posts.add(ChatUser);
                                        }

                                    }




                                    k++;
                                }
                                postsAdapter.setList(posts);


                            }
                        });
                        }

                    }


        });
        Collections.sort(posts ,new Comparator<Post>(){

            public int compare(Post o1, Post o2)
            {
                return o1.getDate().compareTo(o2.getDate());
            }
        });

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

