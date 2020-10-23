package com.example.moviesbook.Adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;

import com.example.moviesbook.Activity.ViewmbActivity;
import com.example.moviesbook.Book;
import com.example.moviesbook.Friend;
import com.example.moviesbook.Interfaces.ClickListener;
import com.example.moviesbook.Json_Books.ImageLinks;
import com.example.moviesbook.Json_Books.Item;
import com.example.moviesbook.Json_Books.VolumeInfo;
import com.example.moviesbook.Movie;
import com.example.moviesbook.Userdata;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviesbook.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mymoviesadapter extends RecyclerView.Adapter<Mymoviesadapter.PostViewHolder> {
    FirebaseFirestore db;
    DocumentReference messageRef;
    private ClickListener listener;
    private List<Movie> MoviesItems = new ArrayList<>();
    SharedPreferences sp2;
    private Context mcontext;
    Boolean orig = true;
    String id;
    public Mymoviesadapter(Context context, ClickListener listener, String id) {
        db = FirebaseFirestore.getInstance();
        this.id = id;
        this.listener = listener;
        mcontext = context;
        sp2 = mcontext.getSharedPreferences("user", Context.MODE_PRIVATE);
        Query q = db.collection("Movies").whereArrayContains("users",id);

        q.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                Userdata.Usermovies.clear();
                int x = 0;
                for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                    Userdata.Usermovies.put(snapshot.getId(), true);
                }
            }
        });
    }
    public Mymoviesadapter(Context context, ClickListener listener) {
        db = FirebaseFirestore.getInstance();
        this.id = id;
        this.listener = listener;
        mcontext = context;
        sp2 = mcontext.getSharedPreferences("user", Context.MODE_PRIVATE);
    }
    public Mymoviesadapter(Context context) {

        db = FirebaseFirestore.getInstance();
        mcontext = context;
        sp2 = mcontext.getSharedPreferences("user", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.anothergrid, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final PostViewHolder holder, final int position) {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        if (MoviesItems.get(position).getImage() != null) {
            String use = MoviesItems.get(position).getImage();
            Picasso.get().load(use).fit().into(holder.image);
        }

    }

    @Override
    public int getItemCount() {
        return MoviesItems.size();
    }

    public void setList(List<Movie> itemList) {
        this.MoviesItems = itemList;
        notifyDataSetChanged();
    }


    public class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private WeakReference<ClickListener> listenerRef;
        TextView author, date, desc, content, title, link;
        Button add;
        ImageView image;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            listenerRef = new WeakReference<>(listener);
            image = itemView.findViewById(R.id.movieimg);
            image.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mcontext, ViewmbActivity.class);
            if(MoviesItems.get(getAdapterPosition()).getTitle().length() > 5) {
                intent.putExtra("name", MoviesItems.get(getAdapterPosition()).getTitle().substring(0,4) );
            }
            else
            {
                intent.putExtra("name", MoviesItems.get(getAdapterPosition()).getTitle() );
            }
            intent.putExtra("Choice", "Movies");
            intent.putExtra("ID",MoviesItems.get(getAdapterPosition()).getID());


            mcontext.startActivity(intent);

        }



    }
}
