package com.example.moviesbook.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.StrictMode;

import com.example.moviesbook.Book;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.moviesbook.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mybooksadapter extends RecyclerView.Adapter<Mybooksadapter.PostViewHolder> {
    FirebaseFirestore db;
    Userdata userdata;
    DocumentReference messageRef;
    private final ClickListener listener;
    Boolean orig = true;
    private List<Book> BooksItems = new ArrayList<>();
    SharedPreferences sp2 ;
    private Context mcontext;
    public Mybooksadapter(Context context,ClickListener listener,Boolean orig)
    {


        userdata = new Userdata();
        db = FirebaseFirestore.getInstance();;
        this.listener = listener;
        mcontext = context;
        this.orig =  orig;
        sp2 = mcontext.getSharedPreferences("user", Context.MODE_PRIVATE);
        Query q = db.collection("Users").document(sp2.getString("ID",""))
                .collection("BooksList");

        q.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                Userdata.Userbooks.clear();
                int x = 0;
                for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                {
                    Userdata.Userbooks.put(snapshot.getId(), true);
                }
            }
        });
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
        holder.title.setText(BooksItems.get(position).getTitle() + " (" + BooksItems.get(position)
                .getYear() + ")");
        if (BooksItems.get(position).getImage() != null) {
            String use = BooksItems.get(position).getImage();
            Picasso.get().load(use).into(holder.image);
        }

        if(orig) {


            holder.add.setBackgroundDrawable
                    (mcontext.getResources().getDrawable(R.drawable.rounder_corners2));

            holder.add.setText("added to favoritess");
        }
        else
        {
            if((userdata.Userbooks.containsKey(String.valueOf(BooksItems.get(position).getID()))))
            {
                holder.add.setBackgroundDrawable
                        (mcontext.getResources().getDrawable(R.drawable.rounder_corners2));
                holder.add.setText("added to favorites");
            }
            else
            {
                holder.add.setBackgroundDrawable
                        (mcontext.getResources().getDrawable(R.drawable.rounder_corners));

                holder.add.setText("add to favoritess");
            }

        }



    }

    @Override
    public int getItemCount() {
        return BooksItems.size();
    }

    public void setList(List<Book> itemList ) {
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


        }

        @Override
        public void onClick(View v) {
            if(v.getId() == add.getId())
            {
                if(orig)
                {
                    int position = getAdapterPosition();
                    String id = String.valueOf
                            (BooksItems.get(position).getID());
                    BooksItems.clear();
                    db.collection("Users").document(sp2.getString("ID", ""))
                            .collection("BooksList").document
                            (id).delete();
                }
                else
                {
                    if(Userdata.Userbooks.containsKey(String.valueOf
                            (BooksItems.get(getAdapterPosition()).getID())))
                    {
                        Userdata.Userbooks.remove(String.valueOf
                                (BooksItems.get(getAdapterPosition()).getID()));
                        add.setBackgroundDrawable
                                (mcontext.getResources().getDrawable(R.drawable.rounder_corners));

                        add.setText("add to favoritess");
                        db.collection("Users").document(sp2.getString("ID", ""))
                                .collection("BooksList").document
                                ( String.valueOf
                                        (BooksItems.get(getAdapterPosition()).getID())).delete();

                    }
                    else
                    {
                        Userdata.Userbooks.put(String.valueOf
                                (BooksItems.get(getAdapterPosition()).getID()),true);
                        add.setBackgroundDrawable
                                (mcontext.getResources().getDrawable(R.drawable.rounder_corners2));

                        add.setText("added to favorites");
                        db.collection("Users").document(sp2.getString("ID", ""))
                                .collection("BooksList").document
                                ( String.valueOf
                                        (BooksItems.get(getAdapterPosition()).getID())).delete();
                        Map<String,Object> write = new HashMap<>();
                        userdata.Usermovies.put(String.valueOf(BooksItems.get(getAdapterPosition()).getID()),true);
                        write.put("Title", String.valueOf
                                (BooksItems.get(getAdapterPosition()).getTitle()));
                        write.put("Desc", String.valueOf
                                (BooksItems.get(getAdapterPosition()).getDesc()));
                        write.put("Image", String.valueOf
                                ( BooksItems.get(getAdapterPosition()).getImage()));
                        write.put("Year", String.valueOf
                                ( BooksItems.get(getAdapterPosition()).getYear()));

                        db.collection("Users").document(sp2.getString("ID",""))
                                .collection("BooksList").document
                                (String.valueOf
                                        (BooksItems.get(getAdapterPosition()).getID())).set(write);
                    }
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