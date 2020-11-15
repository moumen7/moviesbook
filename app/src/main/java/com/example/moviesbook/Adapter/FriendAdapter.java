package com.example.moviesbook.Adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.List;


public class FriendAdapter  extends RecyclerView.Adapter<FriendAdapter.FriendHolder> {



    private Context context;
    private List<Friend> CurrentUsersFilter;
    private final ClickListener listener;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private SharedPreferences sp;
    private FirebaseUser user;

    public  FriendAdapter(Context context , ArrayList<Friend>users, ClickListener listener)
    {
        this.listener = listener;
        this.context = context;
        CurrentUsersFilter = new ArrayList<>();
        sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);


    }
    public void setList(List<Friend> friends) {
        this.CurrentUsersFilter = friends;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public FriendHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.one_user,parent,false);
        FriendHolder  holder = new FriendHolder(view);
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_user,
                parent, false);
        return new FriendAdapter.FriendHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendHolder friendholder, final int position) {

        friendholder.textViewName.setText(CurrentUsersFilter.get(position).getUsername());
        String s = CurrentUsersFilter.get(position).getImage();
        String s2 = "https://i.stack.imgur.com/l60Hf.png";

        if(s == null){
            Picasso.get().load(s2).into(friendholder.imageView);

        }
        else
            {
            Picasso.get().load(s).into(friendholder.imageView);
        }

        if(Userdata.following.containsKey(CurrentUsersFilter.get(position).getId()))
        {
            friendholder.imageButton.setImageResource(R.drawable.ic_done_black_24dp);
            friendholder.sendMsgImgBtn.setImageResource(R.drawable.ic_baseline_chat_bubble_outline_24);
        }
        else
        {
            friendholder.imageButton.setImageResource(R.drawable.ic_baseline_person_add_24);
        }
        //holder.itemView.setOnClickListener(new View.OnClickListener() {
         //   @Override
         //   public void onClick(View view) {
         //       Intent intent = new Intent(context, MessageActivity.class);
         //       intent.putExtra("CurrentUser", CurrentUsers.get(position));
         //       context.startActivity(intent);
         //   }
        //});
    }



    @Override
    public int getItemCount() {
        return CurrentUsersFilter.size();
    }


    class FriendHolder extends  RecyclerView.ViewHolder implements View.OnClickListener
    {
        private WeakReference<ClickListener> listenerRef;
        TextView textViewName;
        ImageView imageView;
        ImageButton imageButton;
        ImageButton sendMsgImgBtn;
        public FriendHolder(View itemView) {
            super(itemView);
            listenerRef = new WeakReference<>(listener);
            textViewName = itemView.findViewById(R.id.name);
            imageView = itemView.findViewById(R.id.PP);
            imageButton = itemView.findViewById(R.id.imgbutton);
            sendMsgImgBtn = itemView.findViewById(R.id.send_message_ImageBtn);
            imageButton.setOnClickListener(this);

            itemView.setClickable(true);
            itemView.setOnClickListener(this);
            final Search s = new Search();
            sendMsgImgBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("ID", CurrentUsersFilter.get(getAdapterPosition()).getId());
                intent.putExtra("username",CurrentUsersFilter.get(getAdapterPosition()).getUsername());
                context.startActivity(intent);
                }
            });
        }
        @Override
        public void onClick(View v) {
            if (v.getId() == imageButton.getId())
            {
                            if(Userdata.following.containsKey(CurrentUsersFilter.get(getAdapterPosition()).getId()))
                            {
                                imageButton.setImageResource(R.drawable.ic_baseline_person_add_24);
                                sendMsgImgBtn.setImageResource(R.drawable.send_message);
                                db.collection("Users").document(sp.getString("ID",""))
                                        .update("Following", FieldValue.arrayRemove(CurrentUsersFilter.get(getAdapterPosition()).getId()));
                                db.collection("Users").document(CurrentUsersFilter.get(getAdapterPosition()).getId())
                                        .update("Followers", FieldValue.arrayRemove(sp.getString("ID","")));
                                db.collection("Users").document(sp.getString("ID",""))
                                        .update("numoffollowing", FieldValue.increment(-1));
                                db.collection("Users").document(CurrentUsersFilter.get(getAdapterPosition()).getId())
                                        .update("numoffollowers", FieldValue.increment(-1));
                                Userdata.following.remove(CurrentUsersFilter.get(getAdapterPosition()).getId());
                            }
                            else
                            {

                                imageButton.setImageResource(R.drawable.ic_done_black_24dp);
                                sendMsgImgBtn.setImageResource(R.drawable.ic_baseline_chat_bubble_outline_24);
                                db.collection("Users").document(sp.getString("ID",""))
                                        .update("Following", FieldValue.arrayUnion(CurrentUsersFilter.get(getAdapterPosition()).getId()));
                                db.collection("Users").document(CurrentUsersFilter.get(getAdapterPosition()).getId())
                                        .update("Followers", FieldValue.arrayUnion(sp.getString("ID","")));
                                db.collection("Users").document(sp.getString("ID",""))
                                        .update("numoffollowing", FieldValue.increment(1));
                                db.collection("Users").document(CurrentUsersFilter.get(getAdapterPosition()).getId())
                                        .update("numoffollowers", FieldValue.increment(1));
                                Userdata.following.put(CurrentUsersFilter.get(getAdapterPosition()).getId(),true);
                            }
             }
            else
            {
                        Intent intent = new Intent(context, ViewProfile.class);
                        intent.putExtra("ID",CurrentUsersFilter.get(getAdapterPosition()).getId());
                        Pair[] pairs = new Pair[1];
                        pairs[0] = new Pair<View,String>(imageView,"imagetrans");
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context,pairs);
                        context.startActivity(intent,options.toBundle());
            }
        }
    }
    public void filterList(ArrayList<Friend> filteredList) {
        CurrentUsersFilter = filteredList;
        notifyDataSetChanged();
    }
    public interface onNoteListener
    {
        void onnoteclick(int position);
    }

}
