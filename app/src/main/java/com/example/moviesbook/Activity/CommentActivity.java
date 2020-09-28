package com.example.moviesbook.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.moviesbook.Adapter.CommentsAdapter;
import com.example.moviesbook.Friend;
import com.example.moviesbook.Interfaces.ClickListener;
import com.example.moviesbook.R;
import com.example.moviesbook.comment;
import com.example.moviesbook.fragments.Post;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Comment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class CommentActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ImageButton imgbutton;
    CommentsAdapter commentsAdapter;
    EditText et;
    ArrayList<HashMap<String,String>> Commenters = new ArrayList<>();
    final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    final Date date = new Date();
    SharedPreferences sp;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        recyclerView = findViewById(R.id.comments);
        imgbutton = findViewById(R.id.comm);

        et = findViewById(R.id.commentcontent);
        sp = getSharedPreferences("user",MODE_PRIVATE);
        Query query = db.collection("Posts").whereEqualTo("Postid" ,
                getIntent().getStringExtra("id"));

        commentsAdapter = new CommentsAdapter( CommentActivity.this , Commenters ,new ClickListener() {
            @Override
            public void onPositionClicked(int position) {

            }

            @Override
            public void onLongClicked(int position) {

            }
        });

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                int x = 0;
                for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                    Post ChatUser = snapshot.toObject(Post.class);
                        if (ChatUser.getPostid().equals( getIntent().getStringExtra("id"))) {
                            Commenters = ChatUser.getCommenters();
                        }
                    }
                commentsAdapter.setList(Commenters);
                }
            });
        recyclerView.setAdapter(commentsAdapter);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));
    }

    public void comment(View view) {
        if(view.getId() == imgbutton.getId())
        {
            if(et.getText().toString()!="")
            {
                HashMap <String,String> comment = new HashMap<>();
                comment.put("username",sp.getString("username",""));
                comment.put("id",sp.getString("ID",""));
                comment.put("content",et.getText().toString());
                comment.put("date",formatter.format(date));
                executeTransaction(1);
                Commenters.add(comment);
                db.collection("Posts").document(getIntent().getStringExtra("id"))
                        .update("Commenters",Commenters);
                et.setText("");
                restartActivity(CommentActivity.this);
            }
            else
            {
                Toast.makeText(CommentActivity.this,"can`t make an empty commemt",Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    private void executeTransaction(final int change) {
        db.runTransaction(new Transaction.Function<Long>() {
            @Override
            public Long apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentReference exampleNoteRef = db.collection("Posts").document(getIntent().
                        getStringExtra("id"));
                DocumentSnapshot exampleNoteSnapshot = transaction.get(exampleNoteRef);
                long newPriority = exampleNoteSnapshot.getLong("comments") + change;
                transaction.update(exampleNoteRef, "comments", newPriority);
                return newPriority;
            }
        }).addOnSuccessListener(new OnSuccessListener<Long>() {
            @Override
            public void onSuccess(Long result) {
                Toast.makeText(CommentActivity.this, "New Priority: " + result, Toast.LENGTH_SHORT).show();
            }
        });
    }
    public static void restartActivity(Activity activity) {
        if (Build.VERSION.SDK_INT >= 11) {
            activity.recreate();
        } else {
            activity.startActivity(activity.getIntent());
        }
    }
}
