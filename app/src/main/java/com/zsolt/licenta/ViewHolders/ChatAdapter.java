package com.zsolt.licenta.ViewHolders;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;
import com.zsolt.licenta.Models.Chat;
import com.zsolt.licenta.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatViewHolder> {
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private Context context;
    private List<Chat> chatList;
    private Uri userImageUri;

    public ChatAdapter(Context context, List<Chat> chatList, Uri userImageUri) {
        this.context = context;
        this.chatList = chatList;
        this.userImageUri = userImageUri;
    }


    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.custom_message_right, parent, false);
            return new ChatViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.custom_message_left, parent, false);
            return new ChatViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Chat chat = chatList.get(position);
        holder.getTextMessageContent().setText(chat.getMessage());
        holder.getTextMessageDate().setText(chat.getDate());
        if (holder.getImageUserProfile() != null) {
            Picasso.get().load(userImageUri).placeholder(R.drawable.profile_icon).into(holder.getImageUserProfile());
        }

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (chatList.get(position).getSender().equals(userUid)) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}
