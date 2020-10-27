package com.example.moviesbook.Adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;

import com.example.moviesbook.Activity.PostActivity;
import com.example.moviesbook.Activity.ViewmbActivity;
import com.example.moviesbook.Interfaces.ClickListener;
import com.example.moviesbook.Json_Books.ImageLinks;
import com.example.moviesbook.Json_Books.Item;
import com.example.moviesbook.Json_Books.VolumeInfo;
import com.example.moviesbook.Userdata;
import com.google.android.gms.tasks.OnCompleteListener;
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

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.PostViewHolder> {
    FirebaseFirestore db;
    Userdata userdata;
    DocumentReference messageRef;
    private final ClickListener listener;
    private List<Item> BooksItems = new ArrayList<>();
    SharedPreferences sp2;
    Boolean orig;
    private Context mcontext;
    String id = new String("1234");

    public BooksAdapter(Context context, ClickListener listener, Boolean orig, String id) {
        userdata = new Userdata();
        db = FirebaseFirestore.getInstance();
        ;
        this.listener = listener;
        mcontext = context;
        this.orig = orig;
        this.id = id;
        sp2 = mcontext.getSharedPreferences("user", Context.MODE_PRIVATE);
        if (id != null) {
            Query q = db.collection("Books").whereArrayContains("users", id);
            q.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                    Userdata.Userbooks.clear();
                    int x = 0;
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        Userdata.Userbooks.put(snapshot.getId(), true);
                    }
                }

            });
        }
    }
    public BooksAdapter(Context context,ClickListener listener)
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
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        holder.title.setText(BooksItems.get(position).getVolumeInfo().getTitle() + " (" + BooksItems.get(position).getVolumeInfo()
                .getPublishedDate() + ")");

        ImageLinks imageLinks = BooksItems.get(position).getVolumeInfo().getImageLinks();
        if (imageLinks != null) {
            String use = imageLinks.getThumbnail();
            Picasso.get().load(use).into(holder.image);
        }
        if(orig==null)
        {
            holder.add.setVisibility(View.GONE);

        }
        else if (orig) {
            if ((userdata.Userbooks.containsKey(String.valueOf(BooksItems.get(position).getId())))) {
                holder.add.setBackgroundDrawable
                        (mcontext.getResources().getDrawable(R.drawable.rounder_corners2));
                holder.add.setText("added");
            } else {
                holder.add.setBackgroundDrawable
                        (mcontext.getResources().getDrawable(R.drawable.rounder_corners));

                holder.add.setText("add");
            }
        } else {
            holder.add.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return BooksItems.size();
    }

    public void setList(List<Item> itemList) {
        this.BooksItems = itemList;
        notifyDataSetChanged();
    }


    public class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private WeakReference<ClickListener> listenerRef;
        TextView author, date, content, title, link;
        Button add;
        ImageView image;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            listenerRef = new WeakReference<>(listener);
            title = itemView.findViewById(R.id.movietitle);
            image = itemView.findViewById(R.id.movieimg);
            add = itemView.findViewById(R.id.addbtn);
            add.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(mcontext, "heree", Toast.LENGTH_LONG).show();
            if (v.getId() == add.getId()) {
                if (!Userdata.Userbooks.containsKey(String.valueOf(BooksItems.get(getAdapterPosition()).getId()))) {

                    add.setBackgroundDrawable
                            (mcontext.getResources().getDrawable(R.drawable.rounder_corners2));
                    add.setText("added to favorites");
                    Map<String, Object> write = new HashMap<>();
                    userdata.Userbooks.put(String.valueOf(BooksItems.get(getAdapterPosition()).getId()), true);
                    write.put("Title", String.valueOf
                            (BooksItems.get(getAdapterPosition()).getVolumeInfo().getTitle()));
                    write.put("Desc", String.valueOf
                            (BooksItems.get(getAdapterPosition()).getVolumeInfo().getSubtitle()));
                    write.put("Image", String.valueOf
                            (BooksItems.get(getAdapterPosition()).getVolumeInfo().getImageLinks().getThumbnail()));
                    write.put("Year", String.valueOf
                            (BooksItems.get(getAdapterPosition()).getVolumeInfo().getPublishedDate()));
                    db.collection("Books").document(BooksItems.get(getAdapterPosition()).getId().toString())
                            .set(write, SetOptions.merge());
                    int one = id.length();
                    String put = id.substring(sp2.getString("ID", "").length(), one);
                    if (put.equals("")) {
                        put = "favorites122";
                        db.collection("Books").document(BooksItems.get(getAdapterPosition()).getId().toString())
                                .update("favs", FieldValue.increment(1));
                    }
                    db.collection("Books").document(BooksItems.get(getAdapterPosition()).getId().toString())
                            .update("users", FieldValue.arrayUnion(id));
                    db.collection("Users").document(sp2.getString("ID", ""))
                            .collection("BooksList").document(put)
                            .update("number", FieldValue.increment(1));
                } else {
                    add.setBackgroundDrawable
                            (mcontext.getResources().getDrawable(R.drawable.rounder_corners));
                    userdata.Userbooks.remove(String.valueOf(BooksItems.get(getAdapterPosition()).getId()));
                    add.setText("add to favoritess");
                    db.collection("Books").document(BooksItems.get(getAdapterPosition()).getId().toString())
                            .update("users", FieldValue.arrayRemove(id));
                    int one = id.length();
                    String put = id.substring(sp2.getString("ID","").length() , one);
                    if(put.equals(""))
                    {
                        put = "favorites122";
                        db.collection("Books").document(BooksItems.get(getAdapterPosition()).getId().toString())
                                .update("favs", FieldValue.increment(-1));
                    }
                    db.collection("Users").document(sp2.getString("ID",""))
                            .collection("BooksList").document(put)
                            .update("number", FieldValue.increment(-1));
                }


            } else {
                if(orig==null)
                {
                    Intent intent = new Intent(mcontext, ViewmbActivity.class);
                    intent.putExtra("Choice", "Books");
                    intent.putExtra("ID",BooksItems.get(getAdapterPosition()).getId());
                    mcontext.startActivity(intent);
                }
                else if (!orig) {
                    Intent intent = new Intent(mcontext, PostActivity.class);
                    intent.putExtra("ID", BooksItems.get(getAdapterPosition()).getId());
                    intent.putExtra("Image",String.valueOf(BooksItems.get(getAdapterPosition())
                            .getVolumeInfo().getImageLinks().getThumbnail()));
                    intent.putExtra("title",title.getText().toString());
                    Pair[] arr = new Pair[2];
                    arr[0]= new Pair<View,String>(title,"mbnametrans");
                    arr[1]= new Pair<View,String>(image,"mbimagetrans");
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) mcontext,arr);
                    mcontext.startActivity(intent,options.toBundle());
                }

            }

            listenerRef.get().onPositionClicked(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            Toast.makeText(mcontext, "heree2", Toast.LENGTH_LONG).show();
            return false;
        }
    }
}