package com.example.moviesbook.Adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moviesbook.Activity.CreateListActivity;
import com.example.moviesbook.Activity.ViewActivity;
import com.example.moviesbook.List;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviesbook.Activity.ChatActivity;
import com.example.moviesbook.Activity.HomeActivity;
import com.example.moviesbook.Activity.LoginActivity;
import com.example.moviesbook.Activity.ViewProfile;
import com.example.moviesbook.Friend;
import com.example.moviesbook.Interfaces.ClickListener;
import com.example.moviesbook.R;
import com.example.moviesbook.Userdata;
import com.example.moviesbook.fragments.ActionBottomDialogFragment;
import com.example.moviesbook.fragments.Chats;
import com.example.moviesbook.fragments.CreatelistFragment;
import com.example.moviesbook.fragments.Post;
import com.example.moviesbook.fragments.Search;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ListsAdapter2 extends RecyclerView.Adapter<ListsAdapter2.Holder> {
    private String id;
    private Context context;
    private ArrayList <List> lists;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private SharedPreferences sp;
    private final ClickListener listener;
    private FirebaseUser user;
    private String Type;
    public  ListsAdapter2(Context context , ClickListener listener,String Type,String id)
    {
        this.Type = Type;
        this.id = id;
        lists = new ArrayList<>();
        this.listener = listener;
        this.context = context;
        sp = context.getSharedPreferences("user",Context.MODE_PRIVATE);
    }
    public void setList(ArrayList <List> lists) {
        this.lists = lists;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.grid,parent,false);
        Holder  holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        String s = lists.get(position).getImage();
        holder.button.setVisibility(View.GONE);
        if(s != null)
        {
            Picasso.get().load(s).into(holder.imageView);
        }
        else
        {
            holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.fav));
        }

        holder.textViewName.setText(lists.get(position).getName());
        if(lists.get(position).getID().equals("add"))
        {

            holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.addlist));
        }
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }


    class Holder extends  RecyclerView.ViewHolder implements View.OnClickListener
    {
        private WeakReference<ClickListener> listenerRef;
        TextView textViewName;
        ImageView imageView;
        Button button;

        public Holder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.movietitle);
            imageView = itemView.findViewById(R.id.movieimg);
            button = itemView.findViewById(R.id.addbtn);
            itemView.setClickable(true);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {


        }



    }
}
