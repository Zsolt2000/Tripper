package com.zsolt.licenta.ViewHolders;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.zsolt.licenta.Activities.MessagingActivity;
import com.zsolt.licenta.Models.Chat;
import com.zsolt.licenta.Models.Users;
import com.zsolt.licenta.R;

import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListViewHolder> {
    private List<Users> usersList;
    private Context context;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private String lastMessage;

    public ChatListAdapter(List<Users> usersList, Context context) {
        this.usersList = usersList;
        this.context = context;
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        storageReference = firebaseStorage.getReference();
        databaseReference = firebaseDatabase.getReference();
    }

    @NonNull
    @Override
    public ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_chat_item, parent, false);
        return new ChatListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListViewHolder holder, int position) {
        Users user = usersList.get(position);
        holder.getTextChatListName().setText(user.getName());
        setLastSentMessage(user.getUid(), holder.getTextChatListMessage());
        storageReference.child("Images/").child(user.getProfileImage()).getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).placeholder(R.drawable.profile_icon).into(holder.getImageChatList()));
        holder.itemView.setOnClickListener(v -> {
            Intent startChatActivity = new Intent(context, MessagingActivity.class);
            startChatActivity.putExtra("chat", user);
            context.startActivity(startChatActivity);
        });
    }

    private void setLastSentMessage(String userUid, final TextView textView) {
        lastMessage = "";
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference.child("Chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Chat chat = ds.getValue(Chat.class);
                    if (chat.getReceiver().equals(currentUserUid) && chat.getSender().equals(userUid)) {
                        lastMessage = chat.getMessage();
                    } else if (chat.getReceiver().equals(userUid) && chat.getSender().equals(currentUserUid)) {
                        lastMessage = "You:" + chat.getMessage();
                    }
                }
                textView.setText(lastMessage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }
}
