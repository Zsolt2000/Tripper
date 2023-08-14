package com.zsolt.licenta.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zsolt.licenta.Models.Trips;
import com.zsolt.licenta.R;
import com.zsolt.licenta.ViewHolders.TripListAdapter;

import java.util.ArrayList;
import java.util.List;

public class YourTripsActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ActionBar actionBar;
    private RecyclerView recyclerviewYourTrips;
    private TripListAdapter tripsListAdapter;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private List<Trips> tripsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_trips);
        setupToolbar();
        recyclerviewYourTrips = findViewById(R.id.recyclerview_your_trips);
        setupRecyclerView();

    }

    private void setupRecyclerView() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        tripsList = new ArrayList<>();
        tripsListAdapter = new TripListAdapter(YourTripsActivity.this, tripsList);
        recyclerviewYourTrips.setAdapter(tripsListAdapter);
        recyclerviewYourTrips.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerviewYourTrips.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom = 10;
            }
        });
        databaseReference.child("Trips").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tripsList=new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Trips trips = ds.getValue(Trips.class);
                    if (trips.getCreator().getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        tripsList.add(trips);
                    }
                }
                tripsListAdapter.setDataAdapter(tripsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void setupToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Your Trips");
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
