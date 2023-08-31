package com.zsolt.licenta.CustomViews;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zsolt.licenta.Utils.AddFriendsDialogListener;
import com.zsolt.licenta.Models.Users;
import com.zsolt.licenta.R;
import com.zsolt.licenta.ViewHolders.AddFriendsDialogAdapter;

import java.util.ArrayList;
import java.util.List;


public class AddFriendDialogFragment extends AppCompatDialogFragment {
    private RecyclerView recyclerView;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private AddFriendsDialogListener listener;
    private static List<Users> friendsList;

    public void setDialogListener(AddFriendsDialogListener listener) {
        this.listener = listener;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_friends, null);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        setupRecyclerView(view);
        builder.setView(view).setTitle("Invite users").setNegativeButton("Cancel", (dialog, which) -> dismiss());
        return builder.create();
    }

    private void setupRecyclerView(View view) {
        friendsList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerview_add_friend);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom = 10;
                outRect.left = 10;
            }
        });
        databaseReference.child("Users").get().addOnSuccessListener(dataSnapshot -> {
            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                if (ds.getValue(Users.class).getUid().equals(FirebaseAuth.getInstance().getUid())) {
                    continue;
                } else {
                    friendsList.add(ds.getValue(Users.class));
                }
            }
            recyclerView.setAdapter(new AddFriendsDialogAdapter(friendsList, listener));
        });
    }
}



