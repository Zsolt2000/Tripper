package com.zsolt.licenta.ViewHolders;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.zsolt.licenta.Models.Users;
import com.zsolt.licenta.R;

import org.w3c.dom.Text;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SuggestionsAdapter extends ArrayAdapter<Users> {
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private Context context;
    private List<Users> usersList;


    public SuggestionsAdapter(@NonNull Context context, int resource, @NonNull List<Users> objects) {
        super(context, resource, objects);
        this.usersList = objects;
        this.context = context;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        if (convertView == null)
            convertView = LayoutInflater.from(context).inflate(R.layout.custom_suggestion_dropdown_item, parent, false);
        TextView textSuggestionName = convertView.findViewById(R.id.text_name_suggestion);
        CircleImageView imageSuggestion = convertView.findViewById(R.id.image_profile_suggestion);
        Users user = getItem(position);
        textSuggestionName.setText(user.getName());
        storageReference.child("Images/" + user.getProfileImage()).getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).placeholder(R.drawable.profile_icon).into(imageSuggestion));
        return convertView;
    }


    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);

    }


}
