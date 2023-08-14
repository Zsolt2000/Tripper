package com.zsolt.licenta.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.nex3z.flowlayout.FlowLayout;
import com.squareup.picasso.Picasso;
import com.zsolt.licenta.Models.NotificationType;
import com.zsolt.licenta.Models.Users;
import com.zsolt.licenta.Notifications.Data;
import com.zsolt.licenta.Notifications.RetrofitClient;
import com.zsolt.licenta.Notifications.MyResponse;
import com.zsolt.licenta.Notifications.NotificationSender;
import com.zsolt.licenta.Notifications.TripperMessagingData;

import com.zsolt.licenta.R;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfileActivity extends AppCompatActivity {
    private TextView textUserName, textUserAge, textUserPhone, textUserGender, textUserDate, textUserLocation;
    private Button buttonAddFriend, buttonStartChat;
    private FlowLayout flowLayout;
    private CircleImageView imageUserProfile;
    private Toolbar toolbar;
    private ActionBar actionbar;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private Users selectedUser;
    private TripperMessagingData tripperMessagingData;
    private boolean isFriend =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Intent getIntent = getIntent();
        selectedUser = (Users) getIntent.getSerializableExtra("selectedUser");
        setupViews();
        setupToolbar();
        setupFirebase();
        setupFlowLayout();
        verifyIfFriend();
        setupProfile(selectedUser);
        setupSendNotifications();
    }

    private void setupSendNotifications() {
        tripperMessagingData = RetrofitClient.getClient("https://fcm.googleapis.com/").create(TripperMessagingData.class);
        buttonAddFriend.setOnClickListener(v -> {
            if (isFriend) {
                buttonAddFriend.setText("Add friend");
                buttonStartChat.setVisibility(View.GONE);
                updateFriendList(false);
            } else {
                String userDeviceToken = selectedUser.getDeviceToken();
                String userUid=getCurrentUser().getUid();
                sendNotifications(userDeviceToken,userUid);
                updateFriendList(true);
                buttonAddFriend.setText("Remove friend");
                buttonStartChat.setVisibility(View.VISIBLE);
            }
            isFriend =!isFriend;
        });
    }

    private void verifyIfFriend() {
        databaseReference.child("Users").child(FirebaseAuth.getInstance().getUid()).get().addOnSuccessListener(dataSnapshot -> {
            Users currentUser = dataSnapshot.getValue(Users.class);
            if (currentUser.getFriendsList() == null) {
                buttonAddFriend.setText("Add friend");
                buttonStartChat.setVisibility(View.GONE);
                isFriend = false;
            } else if (currentUser.getFriendsList().containsKey(selectedUser.getUid())) {
                buttonAddFriend.setText("Remove friend");
                buttonStartChat.setVisibility(View.VISIBLE);
                isFriend = true;
            }
        });
    }


    private void updateFriendList(boolean friendState) {
        String currentUserUid = FirebaseAuth.getInstance().getUid();
        String selectedUserNode = "/" + selectedUser.getUid() + "/friendsList/" + currentUserUid;
        String currentUserNode = "/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/friendsList/" + selectedUser.getUid();
        Map<String, Object> usersMap = new HashMap<>();
        if (friendState) {
            usersMap.put(selectedUserNode, getCurrentUser());
            usersMap.put(currentUserNode, selectedUser);
            databaseReference.child("Users").updateChildren(usersMap).addOnSuccessListener(unused -> {
            });
        } else {
            usersMap.put(selectedUserNode, null);
            usersMap.put(currentUserNode, null);
            databaseReference.child("Users").updateChildren(usersMap).addOnSuccessListener(unused -> {
            });
        }
    }

    private Users getCurrentUser() {
        SharedPreferences sharedPreferences = getSharedPreferences("TripperPreference", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String defaultValue = "";
        return gson.fromJson(sharedPreferences.getString("currentUser", defaultValue), Users.class);
    }


    private void sendNotifications(String userToken,String userUID) {
        Data data = new Data(userUID, NotificationType.FRIEND_REQUEST);
        NotificationSender sender = new NotificationSender(data, userToken);
        tripperMessagingData.sendNotification(sender).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                if (response.code() == 200) {
                    if (response.body().success != 1) {
                        Toast.makeText(UserProfileActivity.this, "Failed to send notification", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {

            }
        });
    }

    private void setupFlowLayout() {
        flowLayout.setChildSpacing(5);
        flowLayout.setRowSpacing(15f);
        for (int i = 0; i < selectedUser.getInterests().size(); i++) {
            View itemView = LayoutInflater.from(this).inflate(R.layout.custom_add_interests, flowLayout, false);
            TextView text = itemView.findViewById(R.id.text_interest_name);
            ImageButton buttonRemoveInterest = itemView.findViewById(R.id.remove_button_interest);
            buttonRemoveInterest.setVisibility(View.GONE);
            text.setText(selectedUser.getInterests().get(i).toString());
            flowLayout.addView(itemView);
        }
    }

    private void setupFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        databaseReference = firebaseDatabase.getReference();
        storageReference = firebaseStorage.getReference();
    }

    private void setupProfile(Users user) {

        textUserName.setText(user.getName());
        textUserAge.setText(String.valueOf(user.getAge()));
        textUserPhone.setText(user.getPhoneNumber());
        textUserGender.setText(user.getGender().toString());
        textUserDate.setText(user.getDateOfBirth());
        textUserLocation.setText(user.getHomeLocation());
        storageReference.child("Images/" + user.getProfileImage()).getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).placeholder(R.drawable.profile_icon).into(imageUserProfile));
    }

    private void setupViews() {
        toolbar = findViewById(R.id.toolbar);
        textUserName = findViewById(R.id.text_user_profile_name);
        textUserAge = findViewById(R.id.text_user_profile_age);
        textUserPhone = findViewById(R.id.text_user_profile_phone);
        textUserGender = findViewById(R.id.text_user_profile_gender);
        textUserDate = findViewById(R.id.text_user_profile_date);
        textUserLocation = findViewById(R.id.text_user_profile_place);
        buttonAddFriend = findViewById(R.id.button_add_friend);
        buttonStartChat = findViewById(R.id.button_start_chat);
        imageUserProfile = findViewById(R.id.image_user_profile);
        flowLayout = findViewById(R.id.flowlayout_user_profile_interests);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setupProfile((Users) intent.getSerializableExtra("selectedUser"));


    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setTitle("");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


}