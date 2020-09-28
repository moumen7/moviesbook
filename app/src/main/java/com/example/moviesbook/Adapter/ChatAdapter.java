package com.example.moviesbook.Adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviesbook.Chat;
import com.example.moviesbook.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>{

    private static final int VIEW_TYPE_ME = 1;
    private static final int VIEW_TYPE_OTHER = 2;
    List<Chat> mChats;
    Context context;
    FirebaseUser fUser;

    public ChatAdapter(List<Chat> mChats, Context context) {
        this.mChats = mChats;
        this.context = context;
    }



    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case VIEW_TYPE_ME:
                View viewChatMine = layoutInflater.inflate(R.layout.item_chat_mine, parent, false);
                //viewHolder = new MyChatViewHolder(viewChatMine);
                return new ViewHolder(viewChatMine);
            case VIEW_TYPE_OTHER:
                View viewChatOther = layoutInflater.inflate(R.layout.item_chat_other, parent, false);
                return new ViewHolder(viewChatOther);
        }
        notifyDataSetChanged();
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chat = mChats.get(position);
        holder.showMessage.setText(chat.message);
        holder.time = chat.timestamp;
        holder.timeString = holder.time.toString();

    }




    @Override
        public int getItemViewType(int position) {
            fUser = FirebaseAuth.getInstance().getCurrentUser();
            if(fUser.getUid().equals(mChats.get(position).sender)){
                return VIEW_TYPE_ME;
            }else{
                return VIEW_TYPE_OTHER;
            }
        }

    @Override
    public int getItemCount() {
        return mChats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView showMessage;
        private Long time;
        private String timeString;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            showMessage = itemView.findViewById(R.id.text_view_chat_message);

        }
    }
}
