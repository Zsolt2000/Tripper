package com.zsolt.licenta.ViewHolders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zsolt.licenta.R;


public class TripListViewHolder extends RecyclerView.ViewHolder {
    private TextView textTripTitle,textTripStartDate,textTripAvailability,textTripVisibility;

    public TripListViewHolder(@NonNull View itemView) {
        super(itemView);
        textTripTitle=itemView.findViewById(R.id.text_trip_title);
        textTripStartDate=itemView.findViewById(R.id.text_trip_start_date);
        textTripAvailability=itemView.findViewById(R.id.text_available_space);
        textTripVisibility=itemView.findViewById(R.id.text_view_trip_visibility);

    }

    public TextView getTextTripTitle() {
        return textTripTitle;
    }

    public TextView getTextTripStartDate() {
        return textTripStartDate;
    }

    public TextView getTextTripAvailability() {
        return textTripAvailability;
    }

    public TextView getTextTripVisibility() {
        return textTripVisibility;
    }
}
