package com.zsolt.licenta.ViewHolders;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import com.zsolt.licenta.Activities.TripActivity;
import com.zsolt.licenta.Activities.UserProfileActivity;
import com.zsolt.licenta.Models.Users;
import com.zsolt.licenta.R;
import com.zsolt.licenta.Utils.AddFriendsDialogListener;

import java.util.List;

public class AddFriendsAdapter extends RecyclerView.Adapter<AddFriendsViewHolder> {
    private List<Users> friendsList;
    private View itemView;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private AddFriendsDialogListener listener;
    private boolean isRemovable;
    private Context context;

    public AddFriendsAdapter(List<Users> friendsList, AddFriendsDialogListener listener, Context context) {
        this.friendsList = friendsList;
        this.listener = listener;
        this.context = context;
    }

    public void setRemovable(boolean isRemovable) {
        this.isRemovable = isRemovable;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public AddFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_add_friends_activity, parent, false);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        return new AddFriendsViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull AddFriendsViewHolder holder, int position) {
        Users user = friendsList.get(position);
        holder.getTextProfileName().setText(user.getName());
        storageReference.child("Images/" + user.getProfileImage()).getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).placeholder(R.drawable.profile_icon).into(holder.getImageProfile()));
        if (isRemovable) {
            holder.getButtonRemoveFriend().setVisibility(View.VISIBLE);
        } else {
            holder.getButtonRemoveFriend().setVisibility(View.GONE);
            holder.itemView.setOnClickListener(v -> {
                Intent userProfile = new Intent(context, UserProfileActivity.class);
                userProfile.putExtra("selectedUser", user);
                context.startActivity(userProfile);

            });
        }
        holder.getButtonRemoveFriend().setOnClickListener(v -> {
            friendsList.remove(holder.getAdapterPosition());
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        if (friendsList == null)
            return 0;
        else
            return friendsList.size();
    }

    public List<Users> getFriendsList() {
        return friendsList;
    }

    public void setFriendList(List<Users>friendsList) {
        this.friendsList=friendsList;
        notifyDataSetChanged();;
    }

}
