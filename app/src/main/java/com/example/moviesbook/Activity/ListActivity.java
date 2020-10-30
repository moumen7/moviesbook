package com.example.moviesbook.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.moviesbook.Adapter.ListsAdapter;
import com.example.moviesbook.Adapter.ListsAdapter2;
import com.example.moviesbook.Interfaces.ClickListener;
import com.example.moviesbook.List;
import com.example.moviesbook.R;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {
    Query q;
    FirebaseFirestore db ;
    SharedPreferences sp;
    List Firstlist;
    ListsAdapter2 adapter;
    RecyclerView recyclerView;
    private ArrayList<List> lists;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        setTitle("Choose List");
        lists = new ArrayList<>();
        Firstlist = new List();
        recyclerView = findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(ListActivity.this));
        db = FirebaseFirestore.getInstance();
        sp = getSharedPreferences("user",MODE_PRIVATE);
        adapter = new ListsAdapter2(ListActivity.this,new ClickListener() {
            @Override public void onPositionClicked(int position) {

            }

            @Override public void onLongClicked(int position) {
            }
        },getIntent().getStringExtra("choice"),getIntent().getStringExtra("id"));

        q = db.collection("Users").document(sp.getString("ID",""))
                .collection(getIntent().getStringExtra("choice") +"List");

        q
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e)
                    {
                        lists.clear();
                        Firstlist = new List("",null,"Favorite " + getIntent().getStringExtra("choice"));
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
                        adapter.setList(lists);
                    }
                });
        recyclerView.setAdapter(adapter);

    }
}
