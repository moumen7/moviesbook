package com.example.moviesbook.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.moviesbook.Adapter.FriendAdapter;
import com.example.moviesbook.Friend;
import com.example.moviesbook.Interfaces.ClickListener;
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

import java.util.ArrayList;

public class followersorfollowing extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<Friend> userslist;
    FriendAdapter adapter ;
    SharedPreferences sp;
    Query query;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followersorfollowing);
        recyclerView = findViewById(R.id.followersorfollowing);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        sp =getSharedPreferences("user", Context.MODE_PRIVATE);
        recyclerView.setHasFixedSize(true);
        userslist = new ArrayList<>();
        ReadUsers();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final EditText searchBar = findViewById(R.id.search_f);
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
        recyclerView.setHasFixedSize(true);
    }


    private void filter(String text) {
        ArrayList<Friend> filteredList = new ArrayList<>();
        filteredList.clear();
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







    private void ReadUsers()
    {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if(getIntent().hasExtra("Following")) {
            query = db.collection("Users").whereArrayContains("Followers", sp.getString("ID", ""));
        }
        else
        {
            query = db.collection("Users").whereArrayContains("Following", sp.getString("ID", ""));
        }
        db.collection("Users").document(sp.getString("ID",""))
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if(task.getResult().get("Following") != null)
                    {
                        for (String x : (ArrayList<String>) task.getResult().getData().get("Following")) {
                            Userdata.following.put(x, true);
                        }
                    }


        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                userslist.clear();
                int x=0;
                for(DocumentSnapshot snapshot : queryDocumentSnapshots)
                {
                    Friend ChatUser  = snapshot.toObject(Friend.class);
                    userslist.add(ChatUser);
                    x++;
                }
                adapter = new FriendAdapter(followersorfollowing.this,userslist,new ClickListener() {
                    @Override public void onPositionClicked(int position) {

                    }

                    @Override public void onLongClicked(int position) {

                    }
                });
                recyclerView.setAdapter(adapter);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(followersorfollowing.this));
                adapter.setList(userslist);

            }


        }) ;

                }
            }
        });
    }

}
