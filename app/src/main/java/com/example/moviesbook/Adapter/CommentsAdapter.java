package com.example.moviesbook.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;


import com.example.moviesbook.Activity.ViewProfile;
import com.example.moviesbook.Book;
import com.example.moviesbook.Friend;
import com.example.moviesbook.Interfaces.ClickListener;
import com.example.moviesbook.Movie;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Document;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CommentsAdapter  extends RecyclerView.Adapter<CommentsAdapter.FriendHolder> {



    private Context context;
    private ArrayList<HashMap<String,String>> CurrentUsersFilter;
    private final ClickListener listener;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private SharedPreferences sp;

    public CommentsAdapter(Context context , ArrayList<HashMap<String,String>> users, ClickListener listener)
    {
        this.listener = listener;
        this.context = context;
        CurrentUsersFilter  = new ArrayList<>();
        sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
    }
    public void setList( ArrayList<HashMap<String,String>>  users) {
        this.CurrentUsersFilter = users;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public FriendHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.one_comment,parent,false);
        FriendHolder  holder = new FriendHolder(view);
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_comment,
                parent, false);
        return new CommentsAdapter.FriendHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final FriendHolder friendholder, final int position) {
        friendholder.textViewName.setText(CurrentUsersFilter.get(position).get("username"));
        friendholder.content.setText(CurrentUsersFilter.get(position).get("content"));
        friendholder.date.setText(CurrentUsersFilter.get(position).get("date"));
         db.collection("Users").document(CurrentUsersFilter.get(position).get("id")).addSnapshotListener(new EventListener<DocumentSnapshot>() {
             @Override
             public void onEvent(@Nullable DocumentSnapshot snapshot,
                                 @Nullable FirebaseFirestoreException e) {
                 if (e != null) {

                 }

                 if (snapshot != null && snapshot.exists()) {
                     if(snapshot.get("image") != null) {
                         Picasso.get().load(snapshot.get("image").toString()).into(friendholder.imageView);
                     }
                     else
                     {
                         Picasso.get().load("https://i.stack.imgur.com/l60Hf.png").into(friendholder.imageView);
                     }
                     return;
                 } else {
                     Picasso.get().load("https://i.stack.imgur.com/l60Hf.png").into(friendholder.imageView);
                 }

             }
         });


    }



    @Override
    public int getItemCount() {
        return CurrentUsersFilter.size();
    }


    class FriendHolder extends  RecyclerView.ViewHolder implements View.OnClickListener
    {
        private WeakReference<ClickListener> listenerRef;
        TextView textViewName,date;
        ImageView imageView;
        TextView content;
        public FriendHolder(View itemView) {
            super(itemView);
            listenerRef = new WeakReference<>(listener);
            textViewName = itemView.findViewById(R.id.name);
            imageView = itemView.findViewById(R.id.PP);
            content = itemView.findViewById(R.id.commenttext);
            date = itemView.findViewById(R.id.date);
            itemView.setClickable(true);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }

    }


}
