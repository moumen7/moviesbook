package com.example.moviesbook.Adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.fonts.Font;
import android.icu.text.Transliterator;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;

import com.example.moviesbook.Activity.CommentActivity;
import com.example.moviesbook.Activity.FullscreenActivity;
import com.example.moviesbook.Activity.MainActivity;
import com.example.moviesbook.Activity.PostActivity;
import com.example.moviesbook.Activity.ViewActivity;
import com.example.moviesbook.Activity.ViewProfile;
import com.example.moviesbook.Activity.ViewmbActivity;
import com.example.moviesbook.Interfaces.ClickListener;
import com.example.moviesbook.Prefmanager;
import com.example.moviesbook.Userdata;
import com.example.moviesbook.fragments.ActionBottomDialogFragment;
import com.example.moviesbook.fragments.ActionBottomDialogPostListDialogFragment;
import com.example.moviesbook.fragments.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.squareup.picasso.Picasso;

import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviesbook.R;
import com.example.moviesbook.Result;

import org.w3c.dom.Comment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static android.provider.MediaStore.MediaColumns.DOCUMENT_ID;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostViewHolder> {
    String url = "http://image.tmdb.org/t/p/original";
    FirebaseFirestore db;
    Userdata userdata;
    DocumentReference messageRef;
    Boolean orig;
    ViewActivity viewActivity;
    private final ClickListener listener;
    private List<Post> posts = new ArrayList<>();


    SharedPreferences sp2 ;
    private Context mcontext;
    public PostsAdapter(Context context,ClickListener listener)
    {
        db = FirebaseFirestore.getInstance();
        this.listener = listener;
        mcontext = context;
        sp2 = mcontext.getSharedPreferences("user", Context.MODE_PRIVATE);
    }
    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.post, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final PostViewHolder holder, final int position) {


        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        String text = posts.get(position).getUsername();
        String text2 = posts.get(position).getUsedtitle();
        ForegroundColorSpan fcs = new ForegroundColorSpan(Color.BLUE);
        SpannableString SS = new SpannableString(text + " is posting about " + text2);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent intent = new Intent(mcontext, ViewmbActivity.class);

                    intent.putExtra("name",posts.get(position).getUsedtitle() );

                if (String.valueOf(posts.get(position).getUsedid()).matches("[0-9]+")) {
                    intent.putExtra("Choice", "Movies");
                    intent.putExtra("ID", posts.get(position).getUsedid());
                }
                else {
                    intent.putExtra("Choice", "Books");
                    intent.putExtra("ID", posts.get(position).getUsedid());
                }

                mcontext.startActivity(intent);

            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(mcontext.getResources().getColor(R.color.Orange));
                ds.setUnderlineText(false);
            }
        };

        SS.setSpan(clickableSpan, text.length() + 18 ,text.length() + 18 + text2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        StyleSpan sp = new StyleSpan(Typeface.BOLD);
        StyleSpan SP2 = new StyleSpan(Typeface.BOLD);
        SS.setSpan(SP2,0,text.length() , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        SS.setSpan(sp,text.length() + 17 ,SS.length()   , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.username.setText(SS);
        holder.date.setText(posts.get(position).getDate());
        holder.username.setMovementMethod(LinkMovementMethod.getInstance());
        holder.desc.setText(posts.get(position).getPostdesc());
        holder.numberoflikes.setText(String.valueOf(posts.get(position).getLikes()));
        holder.numberofcomments.setText(String.valueOf(posts.get(position).getComments()));
        db.collection("Users").document(posts.get(position).getUserid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {

                }

                if (snapshot != null && snapshot.exists()) {
                    if(snapshot.get("image") != null) {
                        Picasso.get().load(snapshot.get("image").toString()).into(holder.userimage);
                    }
                    else
                    {
                        Picasso.get().load("https://i.stack.imgur.com/l60Hf.png").into(holder.userimage);
                    }
                    return;
                } else {
                    Picasso.get().load("https://i.stack.imgur.com/l60Hf.png").into(holder.userimage);
                }

            }
        });
        if(posts.get(position).getPostdesc() == null || posts.get(position).getPostdesc().equals("") )
            holder.desc.setVisibility(View.GONE);
        else {
            holder.desc.setVisibility(View.VISIBLE);
        }

        if(posts.get(position).getImage() == null)
            holder.postimage.setVisibility(View.GONE);
        else {
            Picasso.get().load(posts.get(position).getImage()).into(holder.postimage);
            holder.postimage.setVisibility(View.VISIBLE);
        }
        Map <String,Boolean> map = new HashMap<>();
        map =  posts.get(position).getLikers();
        if(map.containsKey(sp2.getString("ID","")))
        {
            holder.like.setChecked(true);
        }
        else
        {
            holder.like.setChecked(false);
        }


        holder.button.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void setList(List<Post> posts) {
        this.posts = posts;
        notifyDataSetChanged();
    }


    public class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{
        private WeakReference<ClickListener> listenerRef;
        TextView date, desc,username,movieorbook,numberoflikes,numberofcomments;
        ImageView userimage,postimage,comment;
        ImageButton button;
        CheckBox like;
        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            listenerRef = new WeakReference<>(listener);
            button = (ImageButton) itemView.findViewById(R.id.delete);
            date = itemView.findViewById(R.id.date);
            desc = itemView.findViewById(R.id.desc);
            username = itemView.findViewById(R.id.username);
            userimage = itemView.findViewById(R.id.userimage);
            postimage = itemView.findViewById(R.id.postimage);
            numberoflikes = itemView.findViewById(R.id.likesnum);
            numberofcomments = itemView.findViewById(R.id.commentnum);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            like.setOnClickListener(this);
            comment.setOnClickListener(this);
            button.setOnClickListener(this);
            userimage.setOnClickListener(this);
            postimage.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        @Override
        public void onClick(View v) {

            if (like.getId() == v.getId()) {

                if(posts.get(getAdapterPosition()).getLikers().containsKey(sp2.getString("ID","")))
                {
                    executeTransaction(-1,0);
                    numberoflikes.setText(String.valueOf(posts.get(getAdapterPosition()).getLikes() - 1));
                    posts.get(getAdapterPosition()).setLikes(posts.get(getAdapterPosition()).getLikes() - 1);
                }
                else
                {

                    executeTransaction(1,1);
                    numberoflikes.setText(String.valueOf(posts.get(getAdapterPosition()).getLikes() + 1));
                    posts.get(getAdapterPosition()).setLikes(posts.get(getAdapterPosition()).getLikes() + 1);
                }

            }
            else if(comment.getId() == v.getId())
            {
                Intent intent = new Intent(mcontext, CommentActivity.class);
                intent.putExtra("id",posts.get(getAdapterPosition()).getPostid());
                mcontext.startActivity(intent);
            }
            else if(v.getId() == button.getId())
            {
                new AlertDialog.Builder(mcontext)
                        .setTitle("Delete Post")
                        .setMessage("Are you sure you want to delete this post?")

                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                db.collection("Posts").document(posts.get(getAdapterPosition()).getPostid())
                                        .delete();
                            }
                        })


                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
            else if(v.getId() == postimage.getId())
            {

                Intent intent = new Intent(mcontext, FullscreenActivity.class);
                intent.putExtra("Image",posts.get(getAdapterPosition()).getImage());
                Pair x = new Pair<View,String>(postimage,"full");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) mcontext,x);
                mcontext.startActivity(intent,options.toBundle());


            }
            ///// if clicked on name of post author go to toProfile() in viewActivity
            ///// but it doesn't work so I didn't do the same in feeds fragment
            else if(v.getId() == username.getId()){
                viewActivity.ToProfile(posts.get(getAdapterPosition()).getUserid());
            }
            else if(v.getId() == userimage.getId())
            {
                Intent intent = new Intent(mcontext, ViewProfile.class);
                intent.putExtra("ID",posts.get(getAdapterPosition()).getUserid());

                mcontext.startActivity(intent);
            }
            listenerRef.get().onPositionClicked(getAdapterPosition());
        }
        private void executeTransaction(final int change, final int state) {
            final int use = getAdapterPosition();
            final Map <String,Boolean> map;
            map = (Map <String,Boolean>) posts.get(getAdapterPosition()).getLikers();

            db.runTransaction(new Transaction.Function<Long>() {
                @Override
                public Long apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                    DocumentReference exampleNoteRef = db.collection("Posts").document(posts
                            .get(getAdapterPosition()).getPostid());
                    DocumentSnapshot exampleNoteSnapshot = transaction.get(exampleNoteRef);
                    long newPriority = exampleNoteSnapshot.getLong("likes") + change;
                    if(state==0)
                    {
                        map.remove(sp2.getString("ID",""));
                        transaction.update(exampleNoteRef,"Likers",map);
                    }
                    else
                    {
                        map.put(sp2.getString("ID",""),true);
                        transaction.update(exampleNoteRef,"Likers",map);
                    }

                    transaction.update(exampleNoteRef, "likes", newPriority);

                    return newPriority;
                }
            }).addOnSuccessListener(new OnSuccessListener<Long>() {
                @Override
                public void onSuccess(Long result) {

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
            listenerRef.get().onPositionClicked(getAdapterPosition());

        }


        @Override
        public boolean onLongClick(View v) {
            if (posts.get(getAdapterPosition()).getUserid().equals(sp2.getString("ID", "")))
            {
                ActionBottomDialogPostListDialogFragment addPhotoBottomDialogFragment =
                        ActionBottomDialogPostListDialogFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("ID", posts.get(getAdapterPosition()).getPostid());
                bundle.putString("choice","false" );
                addPhotoBottomDialogFragment.setArguments(bundle);
                addPhotoBottomDialogFragment.show(((FragmentActivity) mcontext).getSupportFragmentManager(),
                        ActionBottomDialogFragment.TAG);
            }
            return false;
        }
    }

}
