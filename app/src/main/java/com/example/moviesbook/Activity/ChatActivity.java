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
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    Date date = new Date();
    String path;
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
                if (getFragmentManager().getBackStackEntryCount() == 0) {
                    finish();
                } else {
                   onBackPressed(); //replaced
                }

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
        username.setText(getIntent().getStringExtra("username"));
        if(id.compareTo(FirebaseAuth.getInstance().getCurrentUser().getUid()) > 0)
        {
            path = FirebaseAuth.getInstance().getCurrentUser().getUid()+ id;

        }
        else
        {
            path = id + FirebaseAuth.getInstance().getCurrentUser().getUid() ;
        }


        mChats = new ArrayList<>();
        db.collection("Chats").document(path + "i").collection("messages").orderBy("Date", Query.Direction.ASCENDING).
                addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e)
                    {
                        mChats.clear();
                        Toast.makeText(ChatActivity.this, String.valueOf(path) , Toast.LENGTH_LONG).show();
                               for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                                        Log.d(TAG, "Now iterating over messages...");
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
                                }
                            });



        editText = (EditText) findViewById(R.id.edit_text_message);
        send = (ImageButton) findViewById(R.id.text_message_send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = editText.getText().toString();
                if (msg.trim().length() > 0) {
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

                        date = new Date();
                        hashMap.put("user1",sender);
                        hashMap.put("user2",receiver);
                        hashMap.put("lastmessage",msg);
                        hashMap.put("Date",formatter.format(date));
                        db.collection("Chats").document(path+"i").set(hashMap);
                        hashMap.clear();
                        hashMap.put("sender", sender);
                        hashMap.put("receiver", receiver);
                        hashMap.put("message", msg);
                        hashMap.put("Date", formatter.format(date));
                        db.collection("Chats").document(path + "i")
                                .collection("messages").document().set(hashMap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                    }
                                });
                    }



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




    private void readMessage(final String myId, final String userId) {

    }


}