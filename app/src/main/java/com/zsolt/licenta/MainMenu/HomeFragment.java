package com.zsolt.licenta.MainMenu;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.zsolt.licenta.Models.Trips;
import com.zsolt.licenta.Models.Users;
import com.zsolt.licenta.R;
import com.zsolt.licenta.ViewHolders.TripListAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerViewHomeFragment;
    private TripListAdapter tripListAdapter;
    private List<Trips> tripsList;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        setupRecyclerView(view);
        setupRecyclerViewData();
        return view;
    }

    private void setupRecyclerView(View view) {
        recyclerViewHomeFragment = view.findViewById(R.id.recyclerview_home_fragment);
        recyclerViewHomeFragment.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerViewHomeFragment.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom = 10;
            }
        });
        tripsList = new ArrayList<>();
        tripListAdapter = new TripListAdapter(getContext(), tripsList);
        recyclerViewHomeFragment.setAdapter(tripListAdapter);
    }

    private void setupRecyclerViewData() {
        databaseReference.child("Trips").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tripsList = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Trips trips = ds.getValue(Trips.class);
                    List<Users> currentTripInvitedUsers = trips.getInvitedUsers();
                    String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    Users currentUser = null;
                    for (Users user : currentTripInvitedUsers) {
                        if (user.getUid().equals(currentUserUid)) {
                            currentUser = user;
                        }
                    }
                    if (!trips.getCreator().getUid().equals(currentUserUid) && !currentTripInvitedUsers.contains(currentUser)) {
                        tripsList.add(trips);
                    }
                }
                tripListAdapter.setDataAdapter(tripsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}