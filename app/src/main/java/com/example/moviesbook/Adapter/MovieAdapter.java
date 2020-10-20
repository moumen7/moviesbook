package com.example.moviesbook.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.Transliterator;
import android.os.StrictMode;

import com.example.moviesbook.Activity.PostActivity;
import com.example.moviesbook.Activity.ViewmbActivity;
import com.example.moviesbook.Interfaces.ClickListener;
import com.example.moviesbook.Prefmanager;
import com.example.moviesbook.Userdata;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.firestore.Transaction;
import com.squareup.picasso.Picasso;

import android.preference.PreferenceManager;
import android.util.Log;
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
import com.example.moviesbook.Result;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static android.provider.MediaStore.MediaColumns.DOCUMENT_ID;
import static androidx.constraintlayout.motion.widget.MotionScene.TAG;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.PostViewHolder> {
    String url = "http://image.tmdb.org/t/p/original";
    FirebaseFirestore db;

    DocumentReference messageRef;
    Boolean orig;
    private ClickListener listener;
    private List<Result> MoviesList = new ArrayList<>();
    SharedPreferences sp2 ;
    private Context mcontext;
    String id = new String("1234");
    public MovieAdapter(Context context,ClickListener listener,Boolean orig, String id)
    {
        db = FirebaseFirestore.getInstance();
        this.listener = listener;
        mcontext = context;
        this.id = id;
        this.orig = orig;
        sp2 = mcontext.getSharedPreferences("user", Context.MODE_PRIVATE);


        if(id!=null) {
            Query q = db.collection("Movies").whereArrayContains("users", id);
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

    }
    public MovieAdapter(Context context,ClickListener listener)
    {
        mcontext = context;
        this.listener = listener;
    }
    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.grid, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final PostViewHolder holder, final int position) {
        Toast.makeText(mcontext,String.valueOf(android.os.Build.VERSION.SDK_INT),Toast.LENGTH_LONG);
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        if(MoviesList.get(position).getReleaseDate()!=null && MoviesList.get(position).getReleaseDate().length()>=4)
            holder.title.setText(MoviesList.get(position).getTitle() +" (" + MoviesList.get(position).getReleaseDate().substring(0,4) +")");
        else
            holder.title.setText(MoviesList.get(position).getTitle());

        holder.desc.setText(MoviesList.get(position).getOverview());
        String use= url + MoviesList.get(position).getPosterPath();
        Picasso.get().load(use).into(holder.image);

        if (orig==null )
        {
            holder.add.setVisibility(View.GONE);
            holder.desc.setVisibility(View.GONE);

        }
        else if(!orig)
        {
            holder.add.setVisibility(View.GONE);
        }
        else {
            if ((Userdata.Usermovies.containsKey(String.valueOf(MoviesList.get(position).getId())))) {
                holder.add.setBackgroundDrawable
                        (mcontext.getResources().getDrawable(R.drawable.rounder_corners2));
                holder.add.setText("added");
            } else {
                holder.add.setBackgroundDrawable
                        (mcontext.getResources().getDrawable(R.drawable.rounder_corners));

                holder.add.setText("add");
            }
        }



    }

    @Override
    public int getItemCount() {
        return MoviesList.size();
    }

    public void setList(List<Result> newsList) {
        this.MoviesList = newsList;
        notifyDataSetChanged();
    }


    public class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private WeakReference<ClickListener> listenerRef;
        TextView author, date, desc, content,title,link;
        Button add;
        ImageView image;
        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            listenerRef = new WeakReference<>(listener);
            title = itemView.findViewById(R.id.movietitle);
            desc = itemView.findViewById(R.id.moviedesc);
            image = itemView.findViewById(R.id.movieimg);
            add = itemView.findViewById(R.id.addbtn);
            add.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(mcontext,"heree",Toast.LENGTH_LONG).show();
            if (v.getId() == add.getId())
            {
                if(!Userdata.Usermovies.containsKey(String.valueOf(MoviesList.get(getAdapterPosition()).getId())))
                {
                    add.setBackgroundDrawable
                            (mcontext.getResources().getDrawable(R.drawable.rounder_corners2));
                    add.setText("added");
                    Map<String,Object> write = new HashMap<>();
                    Userdata.Usermovies.put(String.valueOf(MoviesList.get(getAdapterPosition()).getId()),true);
                    write.put("Title", String.valueOf
                            (MoviesList.get(getAdapterPosition()).getTitle()));
                    write.put("Desc", String.valueOf
                            (MoviesList.get(getAdapterPosition()).getOverview()));
                    write.put("Image", String.valueOf
                            ( url + MoviesList.get(getAdapterPosition()).getPosterPath()));
                    write.put("Rating", String.valueOf
                            ( MoviesList.get(getAdapterPosition()).getVoteAverage()));
                    if(MoviesList.get(getAdapterPosition()).getReleaseDate()!=null && MoviesList.get(getAdapterPosition()).getReleaseDate().length()>=4)
                        write.put("Year", String.valueOf
                                ( MoviesList.get(getAdapterPosition()).getReleaseDate().substring(0,4)));
                    else
                        write.put("Year", String.valueOf
                                ( MoviesList.get(getAdapterPosition()).getReleaseDate()));

                    db.collection("Movies").document(MoviesList.get(getAdapterPosition()).getId().toString())
                            .set(write, SetOptions.merge());
                    int one = id.length();
                    String put = id.substring(sp2.getString("ID","").length() , one);
                    if(put.equals(""))
                    {
                        put = "favorites122";
                        db.collection("Movies").document(MoviesList.get(getAdapterPosition()).getId().toString())
                                .update("favs", FieldValue.increment(1));
                    }
                    db.collection("Movies").document(MoviesList.get(getAdapterPosition()).getId().toString())
                            .update("users", FieldValue.arrayUnion(id));
                    db.collection("Users").document(sp2.getString("ID",""))
                            .collection("MoviesList").document(put)
                            .update("number", FieldValue.increment(1));
                }
                else
                {
                    add.setBackgroundDrawable
                            (mcontext.getResources().getDrawable(R.drawable.rounder_corners));
                    Userdata.Usermovies.remove(String.valueOf(MoviesList.get(getAdapterPosition()).getId()));
                    add.setText("add");
                    db.collection("Movies").document(MoviesList.get(getAdapterPosition()).getId().toString())
                            .update("users", FieldValue.arrayRemove(id));
                    int one = id.length();
                    String put = id.substring(sp2.getString("ID","").length() , one);
                    if(put.equals(""))
                    {
                        put = "favorites122";
                        db.collection("Movies").document(MoviesList.get(getAdapterPosition()).getId().toString())
                                .update("favs", FieldValue.increment(-1));
                    }
                    db.collection("Users").document(sp2.getString("ID",""))
                            .collection("MoviesList").document(put)
                            .update("number", FieldValue.increment(-1));
                }


            }
            else
            {
                if(orig==null)
                {
                    Intent intent = new Intent(mcontext, ViewmbActivity.class);
                    intent.putExtra("Choice", "Movies");
                    intent.putExtra("ID", MoviesList.get(getAdapterPosition()).getId().toString());
                    mcontext.startActivity(intent);
                }
                else if(!orig)
                {
                    Intent intent = new Intent(mcontext, PostActivity.class);
                    intent.putExtra("ID",String.valueOf(MoviesList.get(getAdapterPosition()).getId()));
                    intent.putExtra("title",MoviesList.get(getAdapterPosition()).getTitle());
                    mcontext.startActivity(intent);
                }
            }

            listenerRef.get().onPositionClicked(getAdapterPosition());
        }



    }


}
