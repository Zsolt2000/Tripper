package com.zsolt.licenta.ViewHolders;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.zsolt.licenta.Utils.AddFriendsDialogListener;
import com.zsolt.licenta.Models.Users;
import com.zsolt.licenta.R;

import java.util.List;

public class AddFriendsDialogAdapter extends RecyclerView.Adapter<AddFriendsDialogViewHolder> {
    private final List<Users> friendsList;
    private View itemView;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private AddFriendsDialogListener listener;

    public AddFriendsDialogAdapter(List<Users> friendsList, AddFriendsDialogListener listener) {
        this.friendsList = friendsList;
        this.listener = listener;
    }


    @NonNull
    @Override
    public AddFriendsDialogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_add_friends_dialog, parent, false);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        return new AddFriendsDialogViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AddFriendsDialogViewHolder holder, int position) {

        Users user = friendsList.get(position);
        holder.getTextProfileName().setText(user.getName());
        String imagePath = "Images/" + user.getProfileImage();
        final long IMAGE_SIZE = 1024 * 1024;
        storageReference.child(imagePath).getBytes(IMAGE_SIZE).addOnSuccessListener(bytes -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            holder.getImageProfile().setImageBitmap(bitmap);
        });
        holder.itemView.setOnClickListener(v -> listener.addFriendtoRecyclerView(user));
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }


}
