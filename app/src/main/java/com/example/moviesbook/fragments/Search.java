package com.example.moviesbook.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.moviesbook.Activity.AddActivity;
import com.example.moviesbook.Activity.AddPicActivity;
import com.example.moviesbook.Activity.ChatActivity;
import com.example.moviesbook.Activity.HomeActivity;
import com.example.moviesbook.Activity.LoginActivity;
import com.example.moviesbook.Activity.RegisterActivity;
import com.example.moviesbook.Activity.ViewProfile;
import com.example.moviesbook.Adapter.BooksAdapter;
import com.example.moviesbook.Adapter.FriendAdapter;
import com.example.moviesbook.Adapter.MovieAdapter;
import com.example.moviesbook.Friend;
import com.example.moviesbook.Interfaces.ClickListener;
import com.example.moviesbook.Json_Books.BooksResult;
import com.example.moviesbook.MovieResults;
import com.example.moviesbook.R;
import com.example.moviesbook.Userdata;
import com.example.moviesbook.ViewModel.BooksViewModel;
import com.example.moviesbook.ViewModel.MoviesViewModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

//public View v;
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Search#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Search extends Fragment implements FriendAdapter.onNoteListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    FirestoreRecyclerOptions<Friend> options;

    ArrayList<Friend> filteredList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference users = db.collection("Users");
    private FriendAdapter adapter;
    private TabLayout tabLayout;
    MoviesViewModel moviesViewModel;
    BooksViewModel booksViewModel;
    private RecyclerView recyclerView;
    Query first;

    SharedPreferences sp;
    MovieAdapter movieAdapter;
    BooksAdapter booksAdapter;
    DocumentSnapshot lastVisible;
    private ListView mList;
    Query query;
    private ImageButton send_msg;
    EditText ET;
    boolean end = false;
    boolean f = true;
    // private List<Friend> names = new ArrayList<>();
    LayoutInflater inflater;
    ViewGroup viewGroup;
    ArrayList<Friend> userslist;
    ArrayList<String> same;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Search() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Search.
     */
    // TODO: Rename and change types and number of parameters
    public static Search newInstance(String param1, String param2) {
        Search fragment = new Search();
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
        setRetainInstance(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater = inflater;
        this.viewGroup = container;
        adapter = new FriendAdapter(getContext(), userslist, new ClickListener() {
            @Override
            public void onPositionClicked(int position) {

            }

            @Override
            public void onLongClicked(int position) {

            }
        });

        same = new ArrayList<>();
        moviesViewModel = ViewModelProviders.of(this).get(MoviesViewModel.class);
        booksViewModel = ViewModelProviders.of(this).get(BooksViewModel.class);
        ArrayList<Userdata> followers = new ArrayList<>();
        final View view = inflater.inflate(R.layout.fragment_search, container, false);
        recyclerView = view.findViewById(R.id.recycler);
        sp = this.getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        tabLayout = view.findViewById(R.id.tablay);
        ET = view.findViewById(R.id.search_edit_text);
        booksAdapter = new BooksAdapter(getContext(), new ClickListener() {
            @Override
            public void onPositionClicked(int position) {

            }

            @Override
            public void onLongClicked(int position) {

            }
        },null,sp.getString("ID",""));
        movieAdapter = new MovieAdapter(getContext(), new ClickListener() {
            @Override public void onPositionClicked(int position) {

            }

            @Override public void onLongClicked(int position) {

            }
        },null,sp.getString("ID",""));
        userslist = new ArrayList<>();
        tabLayout.addTab(tabLayout.newTab().setText("People"));
        tabLayout.addTab(tabLayout.newTab().setText("Movies"));
        tabLayout.addTab(tabLayout.newTab().setText("Books"));

        end = false;
        recyclerView.setAdapter(adapter);

        ReadUsers();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tabLayout.getSelectedTabPosition()== 0)
                {

                    filter(ET.getText().toString());
                }
                else  if(tabLayout.getSelectedTabPosition()== 2)
                {
                  showbooks(ET.getText().toString());
                }
                else
                {
                    showmovies(ET.getText().toString());
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });





        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users")
                .whereArrayContains("Followers" ,sp.getString("ID",""))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Userdata.following.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Userdata.following.put(document.getId(),true);
                            }
                        }
                        else
                        {

                        }
                    }
                });

        final EditText searchBar = (EditText) view.findViewById(R.id.search_edit_text);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void afterTextChanged(Editable editable) {

                if(!editable.equals(""))
                {
                    if(tabLayout.getSelectedTabPosition()== 0)
                    filter(editable.toString());
                    else  if(tabLayout.getSelectedTabPosition()== 1)
                        showmovies(editable.toString());
                    else
                        showbooks(editable.toString());
                }

            }
        });

        return view;
    }
    private void filter(String text) {
        if (text != "")
        {
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
            recyclerView.setAdapter(adapter);
            filteredList = new ArrayList<>();
            for (Friend item : userslist) {
                if (item.getUsername().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(item);
                }
            }
            adapter.filterList(filteredList);
        }
    }
    private void showmovies(String x)
    {
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(movieAdapter);
        if(x!=null && !x.equals("")) {

            moviesViewModel.getMovies(x);
            moviesViewModel.MoviesMutable.observe(getActivity(), new Observer<MovieResults>() {

                @Override
                public void onChanged(MovieResults postModels) {
                    movieAdapter.setList(postModels.getResults());
                }
            });
        }

    }
    private void showbooks(String x)
    {
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(booksAdapter);
        if(x!=null && !x.equals("")) {

            booksViewModel.getBooks(x);
            booksViewModel.BooksMutable.observe(getActivity(), new Observer<BooksResult>() {

                @Override
                public void onChanged(BooksResult postModels) {
                    booksAdapter.setList(postModels.getItems());
                }
            });
        }
    }

    // adapter.filterList(filteredList);

    /*void showAdapter(Query q1) {
        q1.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                ArrayList<Friend> names = new ArrayList<>();
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Friend model = document.toObject(Friend.class);
                        options.getSnapshots().clear();
                        options = new FirestoreRecyclerOptions.Builder<Friend>().build().getClass().;

                    }
                   // mList = findViewById(R.id.listSearch);

                    adapter = new FriendAdapter( options);
                    recyclerView.setAdapter((FriendAdapter) adapter);

                }
            }
        });
    }*/







    private void ReadUsers()
    {
        final FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        query = db.collection("Users").limit(30).orderBy("username");

        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        if (documentSnapshots.size() != 0) {
                            lastVisible = documentSnapshots.getDocuments()
                                    .get(documentSnapshots.size() - 1);
                        }
                        userslist.clear();
                        int x = 0;
                        for (DocumentSnapshot snapshot : documentSnapshots) {
                            Friend ChatUser = snapshot.toObject(Friend.class);
                            if (!fUser.getUid().equals(ChatUser.getId())) {
                                userslist.add(ChatUser);
                            }
                        }


                        adapter.setList(userslist);



                    }

                }) ;



    }

    @Override
    public void onnoteclick(int position) {

    }
    public void replaceFragment() {


        /*FragmentManager manager = getActivity().getSupportFragmentManager();
        if(manager !=null) {
            manager.beginTransaction()
                    .replace(R.id.search_frag, someFragment, "Chats")
                    .addToBackStack(null)
                    .commit();
        }*/
    }
}