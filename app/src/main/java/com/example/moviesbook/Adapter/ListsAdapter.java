package com.example.moviesbook.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moviesbook.Activity.CreateListActivity;
import com.example.moviesbook.Activity.ViewActivity;
import com.example.moviesbook.List;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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
import com.example.moviesbook.fragments.Chats;
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


public class ListsAdapter extends RecyclerView.Adapter<ListsAdapter.Holder> {
    private String id;
    private Context context;
    private ArrayList <List> lists;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private SharedPreferences sp;
    private final ClickListener listener;
    private FirebaseUser user;
    private String Type;
    public  ListsAdapter(Context context , ClickListener listener,String Type,String id)
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

        View view = LayoutInflater.from(context).inflate(R.layout.onelist,parent,false);
        Holder  holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        String s = lists.get(position).getImage();
        holder.number.setText(String.valueOf(lists.get(position).getNumber()) + " " + Type + "s");
        if(s != null)
        {
            Picasso.get().load(s).into(holder.imageView);
        }

        holder.textViewName.setText(lists.get(position).getName());
        if(lists.get(position).getID().equals("add"))
        {
            holder.number.setVisibility(View.GONE);
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
        TextView textViewName;  TextView number;
        ImageView imageView;

        public Holder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.Listname);
            imageView = itemView.findViewById(R.id.listImage);
            number = itemView.findViewById(R.id.number);
            itemView.setClickable(true);
            itemView.setOnClickListener(this);

        }
        @Override
        public void onClick(View v) {
            if(lists.get(getAdapterPosition()).getID().equals("add"))
            {
                Intent intent = new Intent(context, CreateListActivity.class);
                intent.putExtra(Type,true);
                context.startActivity(intent);

            }
            else
            {
                Intent intent = new Intent(context, ViewActivity.class);

                intent.putExtra("id",id + lists.get(getAdapterPosition()).getID());
                intent.putExtra("Name",lists.get(getAdapterPosition()).getName());
                intent.putExtra(Type,true);
                context.startActivity(intent);
            }
        }
    }
}
