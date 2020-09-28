package com.example.moviesbook.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.moviesbook.Adapter.ChatAdapter;
import com.example.moviesbook.Adapter.FriendAdapter;
import com.example.moviesbook.Chat;
import com.example.moviesbook.User;
import com.example.moviesbook.fragments.Chats;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moviesbook.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ChatActivity extends Activity {

    private static final String TAG ="1";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser fUser;
    Intent intent;
    TextView username;
    Context context = this;
    ImageButton back_btn;
    EditText editText;
    ImageButton send;
    ChatAdapter adapter;

    List<Chat> mChats;
    private RecyclerView recyclerView;
    public ChatActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        back_btn = (ImageButton) findViewById(R.id.back);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, HomeActivity.class);
                startActivity(i);
            }
        });

       recyclerView = findViewById(R.id.recycler_view_chat);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        username = (TextView) findViewById(R.id.show_username);
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        intent = getIntent();
        final String id = intent.getStringExtra("ID");
        db.collection("Users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getId().equals(id)){
                                    username.setText(document.get("username").toString());
                                    readMessage(fUser.getUid(),id);
                                }
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        editText = (EditText) findViewById(R.id.edit_text_message);
        send = (ImageButton) findViewById(R.id.text_message_send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = editText.getText().toString();
                if(!msg.equals("")){
                    sendMessage(fUser.getUid(), id,msg);
                }else{
                    Toast.makeText(context,"Type something!",Toast.LENGTH_SHORT).show();
                }
                editText.setText("");
            }
        });
    }
    private void sendMessage(final String sender, final String receiver, String msg){
        final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        final Date date = new Date();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", msg);
        hashMap.put("Date", formatter.format(date));

        db.collection("Chats").add(hashMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {

            @Override
            public void onSuccess(DocumentReference documentReference) {
                readMessage(sender,receiver);
                Log.d(TAG, "added with ID: " + documentReference.getId());
            }
        });
    }
    private void readMessage(final String myId, final String userId){
        mChats = new ArrayList<>();
       // DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        //Query q = databaseReference.orderByChild("timestamp");

        /*DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Chats");
        mDatabase.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    // TODO: handle the post
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
        db.collection("Chats")
                .orderBy("Date", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.get("receiver").equals(myId) && document.get("sender").equals(userId) ||
                                        document.get("sender").equals(myId) && document.get("receiver").equals(userId)){
                                    Chat chat = new Chat();
                                    chat.sender = document.get("sender").toString();
                                    chat.receiver = document.get("receiver").toString();
                                    chat.message = document.get("message").toString();
                                    mChats.add(chat);

                                }
                                adapter = new ChatAdapter(mChats,context);
                                recyclerView.setAdapter(adapter);
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}