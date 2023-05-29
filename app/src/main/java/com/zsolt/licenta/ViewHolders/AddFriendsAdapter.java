package com.zsolt.licenta.ViewHolders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.zsolt.licenta.Models.Users;
import com.zsolt.licenta.R;

import java.util.List;

public class AddFriendsAdapter extends RecyclerView.Adapter<AddFriendsViewHolder> {
    private final List<Users> friendsList;
    private View itemView;

    public AddFriendsAdapter(List<Users> friendsList) {
        this.friendsList = friendsList;
    }


    @NonNull
    @Override
    public AddFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_add_friends, parent, false);
        return new AddFriendsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AddFriendsViewHolder holder, int position) {
        Users user = friendsList.get(position);

    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }
}
