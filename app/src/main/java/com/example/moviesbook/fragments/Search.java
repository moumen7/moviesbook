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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviesbook.Activity.AddPicActivity;
import com.example.moviesbook.Activity.ChatActivity;
import com.example.moviesbook.Activity.HomeActivity;
import com.example.moviesbook.Activity.LoginActivity;
import com.example.moviesbook.Activity.RegisterActivity;
import com.example.moviesbook.Activity.ViewProfile;
import com.example.moviesbook.Adapter.FriendAdapter;
import com.example.moviesbook.Friend;
import com.example.moviesbook.Interfaces.ClickListener;
import com.example.moviesbook.R;
import com.example.moviesbook.Userdata;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    private RecyclerView recyclerView;
    private ListView mList;
    private ImageButton send_msg;
    // private List<Friend> names = new ArrayList<>();
    LayoutInflater inflater;
    ViewGroup viewGroup;
    ArrayList<Friend> userslist;
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
        ArrayList<Userdata> followers = new ArrayList<>();
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        recyclerView = view.findViewById(R.id.recycler);
        SharedPreferences sp = this.getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Query querry = AllUsernamesQuery();
        userslist = new ArrayList<>();
        ReadUsers();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users")
                .document(sp.getString("ID",""))
                .collection("Following")
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
                    filter(editable.toString());
                }

            }
        });

        return view;
    }
    private void filter(String text) {
         filteredList = new ArrayList<>();
        for (Friend item : userslist) {
            if (item.getUsername().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter.filterList(filteredList);
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

    private Query AllUsernamesQuery(){
        Query query = users.orderBy("username", Query.Direction.ASCENDING);
        return query;
    }


    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
    }



    private void ReadUsers()
    {
        final FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = db.collection("Users");

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                userslist.clear();
                int x=0;
                for(DocumentSnapshot snapshot : queryDocumentSnapshots)
                {
                    Friend ChatUser  = snapshot.toObject(Friend.class);
                    if(!fUser.getUid().equals(ChatUser.getId())) {
                        userslist.add(ChatUser);
                    }

                }

                adapter = new FriendAdapter(getContext(),userslist,new ClickListener() {
                    @Override public void onPositionClicked(int position) {

                    }

                    @Override public void onLongClicked(int position) {

                    }
                });
                recyclerView.setAdapter(adapter);

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