package com.zsolt.licenta.ViewHolders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zsolt.licenta.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddFriendsDialogViewHolder extends RecyclerView.ViewHolder {
    private TextView textProfileName;
    private CircleImageView imageProfile;

    public AddFriendsDialogViewHolder(@NonNull View itemView) {
        super(itemView);
        textProfileName=itemView.findViewById(R.id.text_recyclerview_name);
        imageProfile=itemView.findViewById(R.id.image_recyclerview_profile);
    }

    public TextView getTextProfileName() {
        return textProfileName;
    }

    public CircleImageView getImageProfile() {
        return imageProfile;
    }
}
