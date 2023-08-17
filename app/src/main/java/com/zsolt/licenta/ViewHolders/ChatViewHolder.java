package com.zsolt.licenta.ViewHolders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zsolt.licenta.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatViewHolder extends RecyclerView.ViewHolder {
    private TextView textMessageDate, textMessageContent;
    private CircleImageView imageUserProfile;


    public ChatViewHolder(@NonNull View itemView) {
        super(itemView);
        textMessageDate = itemView.findViewById(R.id.text_message_date);
        textMessageContent = itemView.findViewById(R.id.text_message_content);
        imageUserProfile = itemView.findViewById(R.id.image_message_profile);
    }

    public TextView getTextMessageDate() {
        return textMessageDate;
    }

    public TextView getTextMessageContent() {
        return textMessageContent;
    }

    public CircleImageView getImageUserProfile() {
        return imageUserProfile;
    }


}
