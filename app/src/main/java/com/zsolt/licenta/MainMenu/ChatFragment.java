package com.zsolt.licenta.MainMenu;

import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zsolt.licenta.Models.ChatList;
import com.zsolt.licenta.Models.Users;
import com.zsolt.licenta.R;
import com.zsolt.licenta.ViewHolders.ChatListAdapter;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {
    private RecyclerView recyclerviewChats;
    private ChatListAdapter chatListAdapter;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private String userUid;
    List<Users> usersList;
    private List<ChatList> chatList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        setupFirebase();
        setupRecyclerView(view);
        setupChatList();
        readChats();
        return view;


    }

    private void setupChatList() {
        chatList = new ArrayList<>();
        databaseReference.child("ChatList").child(userUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ChatList chat = ds.getValue(ChatList.class);
                    chatList.add(chat);
                }
                readChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readChats() {
        usersList = new ArrayList<>();
        databaseReference.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Users user = ds.getValue(Users.class);
                    for (ChatList chatList1 : chatList) {
                        if (user.getUid().equals(chatList1.getId())) {
                            usersList.add(user);
                        }
                    }
                }
                chatListAdapter = new ChatListAdapter(usersList, getContext());
                recyclerviewChats.setAdapter(chatListAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    private void setupRecyclerView(View view) {
        recyclerviewChats = view.findViewById(R.id.recyclerview_chat_fragment);
        recyclerviewChats.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerviewChats.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom = 10;
            }
        });
    }

    private void setupFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}