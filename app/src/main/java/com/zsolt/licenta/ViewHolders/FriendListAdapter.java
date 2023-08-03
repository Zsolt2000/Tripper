package com.zsolt.licenta.ViewHolders;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.zsolt.licenta.Activities.UserProfileActivity;
import com.zsolt.licenta.Models.Users;
import com.zsolt.licenta.R;

import java.util.List;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListViewHolder> {
    private List<Users> friendsList;
    private View itemView;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private Context context;

    public FriendListAdapter(Context context,List<Users> friendsList) {
        this.friendsList = friendsList;
        this.context=context;

    }


    @NonNull
    @Override
    public FriendListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_friends_list_item, parent, false);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        return new FriendListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendListViewHolder holder, int position) {
        Users user = friendsList.get(position);
        holder.getTextFriendName().setText(user.getName());
        storageReference.child("Images/" + user.getProfileImage()).getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).placeholder(R.drawable.profile_icon).into(holder.getImageProfile()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userProfile=new Intent(context, UserProfileActivity.class);
                userProfile.putExtra("selectedUser",user);
                context.startActivity(userProfile);
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }
}
