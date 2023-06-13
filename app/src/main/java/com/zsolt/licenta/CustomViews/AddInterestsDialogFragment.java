package com.zsolt.licenta.CustomViews;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.zsolt.licenta.Models.Interests;
import com.zsolt.licenta.Utils.AddInterestsDialogListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddInterestsDialogFragment extends AppCompatDialogFragment {

    private List<Interests> userInterests;
    private AddInterestsDialogListener listener;

    public AddInterestsDialogFragment(List<Interests> userInterests, AddInterestsDialogListener listener) {
        if (userInterests == null)
            this.userInterests = new ArrayList<>();
        else
            this.userInterests = userInterests;
        this.listener = listener;

    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String[] dialogItems = new String[Interests.values().length];
        boolean[] checkedInterests = new boolean[dialogItems.length];
        for (int i = 0; i < Interests.values().length; i++) {
            dialogItems[i] = Interests.values()[i].toString();
        }
        for (int i = 0; i < dialogItems.length; i++) {
            if (userInterests.isEmpty()) {
                checkedInterests[i] = false;
            } else if (userInterests.contains(Interests.valueOf(dialogItems[i]))) {
                checkedInterests[i] = true;
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Interests");
        builder.setMultiChoiceItems(dialogItems, checkedInterests, (dialog, which, isChecked) -> checkedInterests[which] = isChecked)
                .setPositiveButton("Add", (dialog, which) -> {
                    for (int i = 0; i < Interests.values().length; i++) {
                        if (checkedInterests[i] && !userInterests.contains(Interests.valueOf(dialogItems[i])))
                            userInterests.add(Interests.valueOf(dialogItems[i]));
                        else if (!checkedInterests[i] && userInterests.contains(Interests.valueOf(dialogItems[i])))
                            userInterests.remove(Interests.valueOf(dialogItems[i]));
                    }
                    listener.onAddInterest(userInterests);
                }).setNegativeButton("Cancel", (dialog, which) -> dismiss());
        return builder.create();
    }
}
