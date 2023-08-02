package com.zsolt.licenta.ViewHolders;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.zsolt.licenta.Models.Users;
import com.zsolt.licenta.R;
import com.zsolt.licenta.Utils.AddFriendsDialogListener;

import java.util.List;

public class AddFriendsAdapter extends RecyclerView.Adapter<AddFriendsViewHolder> {
    private final List<Users> friendsList;
    private View itemView;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private AddFriendsDialogListener listener;

    public AddFriendsAdapter(List<Users> friendsList, AddFriendsDialogListener listener) {
        this.friendsList = friendsList;
        this.listener = listener;
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
        holder.getButtonRemoveFriend().setOnClickListener(v -> {
            friendsList.remove(holder.getAdapterPosition());
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }


}
