package com.zsolt.licenta.ViewHolders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zsolt.licenta.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListViewHolder extends RecyclerView.ViewHolder {
    private CircleImageView imageChatList;
    private TextView textChatListName,textChatListMessage;


    public ChatListViewHolder(@NonNull View itemView) {
        super(itemView);
        imageChatList=itemView.findViewById(R.id.image_chat_item);
        textChatListName=itemView.findViewById(R.id.text_chat_item_name);
        textChatListMessage=itemView.findViewById(R.id.text_chat_item_message);
    }

    public CircleImageView getImageChatList() {
        return imageChatList;
    }

    public TextView getTextChatListName() {
        return textChatListName;
    }

    public TextView getTextChatListMessage() {
        return textChatListMessage;
    }

}
