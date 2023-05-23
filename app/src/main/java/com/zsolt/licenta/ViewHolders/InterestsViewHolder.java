package com.zsolt.licenta.ViewHolders;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zsolt.licenta.R;

public class InterestsViewHolder extends RecyclerView.ViewHolder {
    private TextView textInterestName;
    private ImageButton removeInterestButton;

    public InterestsViewHolder(@NonNull View itemView) {
        super(itemView);
        textInterestName = itemView.findViewById(R.id.text_interest_name);
        removeInterestButton = itemView.findViewById(R.id.remove_button_interest);

    }

    public TextView getTextInterestName() {
        return textInterestName;
    }

    public ImageButton getRemoveInterestButton() {
        return removeInterestButton;
    }
}
