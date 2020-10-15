package com.example.moviesbook.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.moviesbook.Adapter.ChatAdapter;
import com.example.moviesbook.Chat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

interface MyCallback {
    void onCallback(String value);
}
public class ChatActivity extends Activity {

    private static final String TAG = "1";
    final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    final Date date = new Date();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Chats");
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
    boolean exists = false;
    String room_id;
    boolean is_sent = false;
    HashMap<String, Object> hashMap = new HashMap<>();

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
                                if (document.getId().equals(id)) {
                                    username.setText(document.get("username").toString());
                                    // searchForRoom_id(fUser.getUid(), id);
                                    readMessage(fUser.getUid(), id);
                                    Log.w(TAG, "Done, finding the user");
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
                if (!msg.equals("")) {
                    sendMessage(fUser.getUid(), id, msg);
                } else {
                    Toast.makeText(context, "Type something!", Toast.LENGTH_SHORT).show();
                }
                editText.setText("");
            }
        });
    }

    private void sendMessage(final String sender, final String receiver, final String msg) {
        hashMap.clear();
        db.collection("Chats").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    boolean isFound=false;
                    for(QueryDocumentSnapshot doc:task.getResult()){
                        if(doc.getId().contains(sender) && doc.getId().contains(receiver)){
                            isFound=true;
                            Log.d(TAG,"Room was found");
                            hashMap.put("sender", sender);
                            hashMap.put("receiver", receiver);
                            hashMap.put("message", msg);
                            hashMap.put("Date", formatter.format(date));
                            db.collection("Chats").document(doc.getId()).collection("messages").document()
                                    .set(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    readMessage(sender,receiver);
                                }
                            });

                        }
                    }
                    if(!isFound){
                        Log.d(TAG,"Room wasn't found");
                        hashMap.put("user1",sender);
                        hashMap.put("user2",receiver);
                        db.collection("Chats").document(sender+receiver+"i").set(hashMap);
                        hashMap.clear();
                        hashMap.put("sender", sender);
                        hashMap.put("receiver", receiver);
                        hashMap.put("message", msg);
                        hashMap.put("Date", formatter.format(date));
                        db.collection("Chats").document(sender+receiver+"i")
                                .collection("messages").document().set(hashMap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        readMessage(sender,receiver);
                                    }
                                });
                    }
                }else{
                    Log.d(TAG,"Error getting documents");
                }
            }
        });

        /*if(id.equals(null)){
            hashMap.put("user1",sender);
            hashMap.put("user2",receiver);

            db.collection("Chats").document(sender+receiver+"i").set(hashMap);
            db.collection("Chats").document(sender+receiver+"i")
                    .collection("messages").document().set(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    readMessage(sender, receiver);
                }
            });
            Log.d(TAG,"room id wasn't found");
        }else{
            hashMap.put("sender", sender);
            hashMap.put("receiver", receiver);
            hashMap.put("message", msg);
            hashMap.put("Date", formatter.format(date));
            db.collection("Chats").document(id).collection("messages").document()
                    .set(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    readMessage(sender,receiver);
                }
            });
            Log.d(TAG,"room id was found");
        }*/


    }

    private void readMessage(final String myId, final String userId) {
        mChats = new ArrayList<>();

        db.collection("Chats").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        if (documentSnapshot.getId().contains(myId) && documentSnapshot.getId().contains(userId)) {
                            db.collection("Chats").document(documentSnapshot.getId())
                                    .collection("messages").orderBy("Date", Query.Direction.ASCENDING)
                                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "Now iterating over messages...");
                                        for (final QueryDocumentSnapshot document : task.getResult()) {
                                            Chat chat = new Chat();
                                            chat.sender = document.get("sender").toString();
                                            chat.receiver = document.get("receiver").toString();
                                            chat.message = document.get("message").toString();
                                            // chat.timestamp = Long.parseLong(document.get("Date").toString());
                                            mChats.add(chat);
                                            Log.d(TAG, "messages added!");
                                        }
                                        adapter = new ChatAdapter(mChats, context);
                                        recyclerView.setAdapter(adapter);
                                        Log.w(TAG, "Adapter attached");
                                    } else {
                                        Log.w(TAG, "Error getting documents.", task.getException());
                                    }
                                }
                            });
                        }
                    }
                } else {
                    Log.d(TAG, "Error getting docs" + task.getException());
                }
            }
        });
    }


}