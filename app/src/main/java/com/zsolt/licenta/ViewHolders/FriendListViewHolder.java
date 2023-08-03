package com.zsolt.licenta.ViewHolders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zsolt.licenta.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendListViewHolder extends RecyclerView.ViewHolder {
    private CircleImageView imageProfile;
    private TextView textFriendName;

    public FriendListViewHolder(@NonNull View itemView) {
        super(itemView);
        imageProfile=itemView.findViewById(R.id.image_friend_profile);
        textFriendName=itemView.findViewById(R.id.text_friend_name);
    }

    public CircleImageView getImageProfile() {
        return imageProfile;
    }

    public TextView getTextFriendName() {
        return textFriendName;
    }
}
