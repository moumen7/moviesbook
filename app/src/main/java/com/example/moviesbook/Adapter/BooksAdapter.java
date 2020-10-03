package com.example.moviesbook.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;

import com.example.moviesbook.Activity.PostActivity;
import com.example.moviesbook.Interfaces.ClickListener;
import com.example.moviesbook.Json_Books.ImageLinks;
import com.example.moviesbook.Json_Books.Item;
import com.example.moviesbook.Json_Books.VolumeInfo;
import com.example.moviesbook.Userdata;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
    SharedPreferences sp2 ;
    Boolean orig = true;
    private Context mcontext;
    public BooksAdapter(Context context,ClickListener listener,Boolean orig)
    {
        userdata = new Userdata();
        db = FirebaseFirestore.getInstance();;
        this.listener = listener;
        mcontext = context;
        this.orig = orig;
        sp2 = mcontext.getSharedPreferences("user", Context.MODE_PRIVATE);
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

        holder.title.setText(BooksItems.get(position).getVolumeInfo().getTitle() +" (" +BooksItems.get(position).getVolumeInfo()
                .getPublishedDate() +")");
        holder.desc.setText(BooksItems.get(position).getVolumeInfo().getSubtitle());
        ImageLinks imageLinks= BooksItems.get(position).getVolumeInfo().getImageLinks();
        if(imageLinks!=null) {
            String use = imageLinks.getThumbnail();
            Picasso.get().load(use).into(holder.image);
        }
        if(orig) {
            if ((userdata.Userbooks.containsKey(String.valueOf(BooksItems.get(position).getId())))) {
                holder.add.setBackgroundDrawable
                        (mcontext.getResources().getDrawable(R.drawable.rounder_corners2));
                holder.add.setText("added to favorites");
            } else {
                holder.add.setBackgroundDrawable
                        (mcontext.getResources().getDrawable(R.drawable.rounder_corners));

                holder.add.setText("add to favoritess");
            }
        }
        else
        {
            holder.add.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return BooksItems.size();
    }

    public void setList(List<Item> itemList ) {
        this.BooksItems = itemList;
        notifyDataSetChanged();
    }


    public class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
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
                db.collection("Books").document(String.valueOf(BooksItems.get(getAdapterPosition())))
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {

                            DocumentSnapshot document = task.getResult();

                            if(!(document.exists()))
                            {
                                add.setBackgroundDrawable
                                        (mcontext.getResources().getDrawable(R.drawable.rounder_corners2));
                                add.setText("added to favorites");
                                Map<String,Object> write = new HashMap<>();
                                userdata.Userbooks.put(String.valueOf(BooksItems.get(getAdapterPosition()).getId()),true);
                                write.put("Title", String.valueOf
                                        (BooksItems.get(getAdapterPosition()).getVolumeInfo().getTitle()));
                                write.put("Desc", String.valueOf
                                        (BooksItems.get(getAdapterPosition()).getVolumeInfo().getSubtitle()));
                                write.put("Image", String.valueOf
                                        ( BooksItems.get(getAdapterPosition()).getVolumeInfo().getImageLinks().getThumbnail()));
                                write.put("Year", String.valueOf
                                        ( BooksItems.get(getAdapterPosition()).getVolumeInfo().getPublishedDate()));

                                db.collection("Books").document(BooksItems.get(getAdapterPosition()).getId().toString())
                                        .set(write, SetOptions.merge());

                                db.collection("Books").document(BooksItems.get(getAdapterPosition()).getId().toString())
                                        .update("users", FieldValue.arrayUnion(sp2.getString("ID","")));
                                db.collection("Books").document(BooksItems.get(getAdapterPosition()).getId().toString())
                                        .update("favs", FieldValue.increment(1));
                            }
                            else
                            {
                                add.setBackgroundDrawable
                                        (mcontext.getResources().getDrawable(R.drawable.rounder_corners));
                                userdata.Userbooks.remove(String.valueOf(BooksItems.get(getAdapterPosition()).getId()));
                                add.setText("add to favoritess");
                                db.collection("Books").document(BooksItems.get(getAdapterPosition()).getId().toString())
                                        .update("users", FieldValue.arrayRemove(sp2.getString("ID","")));
                                db.collection("Books").document(BooksItems.get(getAdapterPosition()).getId().toString())
                                        .update("favs", FieldValue.increment(-1));
                            }
                        }
                    }
                });


            }
            else
            {
                if(!orig)
                {
                    Intent intent = new Intent(mcontext, PostActivity.class);
                    intent.putExtra("ID",BooksItems.get(getAdapterPosition()).getId());
                    intent.putExtra("title",BooksItems.get(getAdapterPosition()).getVolumeInfo().getTitle());
                    mcontext.startActivity(intent);
                }

            }

            listenerRef.get().onPositionClicked(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            Toast.makeText(mcontext,"heree2",Toast.LENGTH_LONG).show();
            return false;
        }
    }
}