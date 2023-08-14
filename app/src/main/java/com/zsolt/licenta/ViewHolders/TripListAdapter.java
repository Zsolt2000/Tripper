package com.zsolt.licenta.ViewHolders;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.zsolt.licenta.Activities.TripActivity;
import com.zsolt.licenta.Models.Trips;
import com.zsolt.licenta.R;

import java.io.Serializable;
import java.util.List;

public class TripListAdapter extends RecyclerView.Adapter<TripListViewHolder> {
    private List<Trips> tripsList;
    private View itemView;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Context context;

    public TripListAdapter(Context context, List<Trips> tripsList) {
        this.tripsList = tripsList;
        this.context = context;
    }


    @NonNull
    @Override
    public TripListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_trip_item, parent, false);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        return new TripListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TripListViewHolder holder, int position) {
        Trips trip = tripsList.get(position);
        int availableSpots = trip.getNumberOfPeople() - trip.getInvitedUsers().size();
        holder.getTextTripTitle().setText(trip.getTitle());
        holder.getTextTripStartDate().setText("Start Date: " + trip.getStartDate());
        holder.getTextTripAvailability().setText("Available spots: " + availableSpots);
        if (trip.isPrivate() == true) {
            holder.getTextTripVisibility().setText("Private");
        } else {
            holder.getTextTripVisibility().setText("Public");
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tripActivity = new Intent(context, TripActivity.class);
                tripActivity.putExtra("trip", trip.getTitle());
                context.startActivity(tripActivity);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tripsList.size();
    }

    public void setDataAdapter(List<Trips> tripList) {
        if (tripList == null) {
            return;
        }
        this.tripsList.clear();
        this.tripsList.addAll(tripList);
        notifyDataSetChanged();
    }

}
