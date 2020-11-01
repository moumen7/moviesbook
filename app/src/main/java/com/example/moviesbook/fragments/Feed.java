package com.example.moviesbook.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.AttributeSet;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.google.android.material.appbar.AppBarLayout;
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
    ArrayList<Task>tasks;
    private int recbottom = 0;
    private NestedScrollView scrollView;
    Boolean end;
    ProgressBar progressBar ;
    TextView nomore;
    int sum = 0;
    int last = 0;
    private RecyclerView feedrecyclerview;
    private FirebaseFirestore db;
    private ArrayList<Pair<DocumentSnapshot,String>> lastVisible;
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
        progressBar = view.findViewById(R.id.progressBar);
        nomore =  view.findViewById(R.id.nomore);
        recyclerViewposts = view.findViewById(R.id.posts);
        recyclerViewposts.setLayoutManager(new LinearLayoutManager(getContext()));
        db = FirebaseFirestore.getInstance();
        end = false;
        scrollView = view.findViewById(R.id.scroll);
        progressBar = view.findViewById(R.id.progressBar);
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
                                Userdata.following.put(snapshot.getId(),true);
                                x++;
                            }

                        }
                        //Toast.makeText(getActivity(),String.valueOf(Userdata.following2.size()),Toast.LENGTH_LONG).show();
                        tasks = new ArrayList<>();
                        lastVisible = new ArrayList<>();
                        recyclerViewposts.setAdapter(postsAdapter);
                        int i=0;
                        for(String x:Userdata.following2)
                        {
                            tasks.add(db.collection("Posts").whereEqualTo("userid",x).orderBy("Date",Query.Direction.DESCENDING)
                                    .limit(2)
                                    .get());
                            i++;

                        }
                        tasks.add(db.collection("Posts").whereEqualTo("userid",sp.getString("ID","")).orderBy("Date", Query.Direction.DESCENDING)
                                .limit(2)
                                .get());

                        Task<List<QuerySnapshot>> allTasks = Tasks.whenAllSuccess(tasks.toArray(new Task[tasks.size()]));
                        allTasks.addOnSuccessListener(new OnSuccessListener<List<QuerySnapshot>>() {
                            @Override
                            public void onSuccess(List<QuerySnapshot> querySnapshots)
                            {
                                String data = "";
                                int i=0;
                                for (QuerySnapshot queryDocumentSnapshots : querySnapshots) {
                                    String last = "";

                                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                        Post ChatUser = documentSnapshot.toObject(Post.class);
                                        posts.add(ChatUser);
                                        last = ChatUser.userid;
                                    }
                                    if(queryDocumentSnapshots.size() > 0)
                                    {
                                        lastVisible.add(Pair.create(queryDocumentSnapshots.getDocuments()
                                                .get(queryDocumentSnapshots.size() - 1),last));
                                    }
                                    i++;
                                }
                                postsAdapter.setList(posts);

                            }
                        });
                    }
                });

        recyclerViewposts.setLayoutManager(new LinearLayoutManager(getContext()));
        scrollView.getViewTreeObserver()
                .addOnScrollChangedListener(new
                                                    ViewTreeObserver.OnScrollChangedListener() {
                                                        @Override
                                                        public void onScrollChanged() {

                                                            if (scrollView.getChildAt(0).getBottom()
                                                                    == (scrollView.getHeight() + scrollView.getScrollY()) && posts.size() != 0) {
                                                                if (recbottom != scrollView.getChildAt(0).getBottom()) {
                                                                    progressBar.setProgressTintList(ColorStateList.valueOf(Color.RED));
                                                                    recbottom = scrollView.getChildAt(0).getBottom();
                                                                    showProgressView();

                                                                    tasks = new ArrayList<>();
                                                                    int i = 0;
                                                                    for (Pair<DocumentSnapshot, String> last : lastVisible) {

                                                                        tasks.add(db.collection("Posts").whereEqualTo("userid", last.second).orderBy("Date", Query.Direction.DESCENDING)
                                                                                .limit(2).startAfter(last.first)
                                                                                .get());
                                                                        i++;
                                                                    }

                                                                    Task<List<QuerySnapshot>> allTasks = Tasks.whenAllSuccess(tasks.toArray(new Task[tasks.size()]));
                                                                    allTasks.addOnSuccessListener(new OnSuccessListener<List<QuerySnapshot>>() {
                                                                        @Override
                                                                        public void onSuccess(List<QuerySnapshot> querySnapshots) {
                                                                            int k = 0;
                                                                            String store = "";
                                                                            lastVisible.clear();
                                                                            Boolean happened = false;
                                                                            for (QuerySnapshot queryDocumentSnapshots : querySnapshots) {
                                                                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                                                                    Post ChatUser = documentSnapshot.toObject(Post.class);
                                                                                    posts.add(ChatUser);
                                                                                    store = ChatUser.userid;
                                                                                }
                                                                                if (queryDocumentSnapshots.size() > 0) {
                                                                                    lastVisible.add(Pair.create(queryDocumentSnapshots.getDocuments()
                                                                                            .get(queryDocumentSnapshots.size() - 1), store));
                                                                                    happened = true;
                                                                                }
                                                                                k++;
                                                                            }
                                                                            if (!happened) {
                                                                                hideProgressView();
                                                                                nomore.setVisibility(View.VISIBLE);
                                                                            }
                                                                            postsAdapter.setList(posts);
                                                                            k++;


                                                                        }
                                                                    });
                                                                }

                                                            }
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
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }
    void showProgressView() {
        progressBar.setVisibility(View.VISIBLE);
    }

    void hideProgressView() {
        progressBar.setVisibility(View.INVISIBLE);
    }
}

