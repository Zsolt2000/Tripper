package com.zsolt.licenta.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.zsolt.licenta.Models.Chat;
import com.zsolt.licenta.Models.ChatType;
import com.zsolt.licenta.Models.NotificationType;
import com.zsolt.licenta.Models.Users;
import com.zsolt.licenta.Notifications.Data;
import com.zsolt.licenta.Notifications.MyResponse;
import com.zsolt.licenta.Notifications.NotificationSender;
import com.zsolt.licenta.Notifications.RetrofitClient;
import com.zsolt.licenta.Notifications.TripperMessagingData;
import com.zsolt.licenta.R;
import com.zsolt.licenta.ViewHolders.ChatAdapter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessagingActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ActionBar actionBar;
    private RecyclerView recyclerViewMessages;
    private ChatAdapter chatAdapter;
    private List<Chat> chatList;
    private EditText editSendMessage;
    private ImageButton imageSendMessage;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;
    private Users selectedUser;
    private String currentUserUid;
    private TripperMessagingData tripperMessagingData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);
        selectedUser = (Users) getIntent().getSerializableExtra("chat");
        setupViews();
        setupFirebase();
        setupToolbar();
        setupSendMessage();
        storageReference.child("Images/").child(selectedUser.getProfileImage()).getDownloadUrl().addOnSuccessListener(uri -> readMessages(currentUserUid, selectedUser.getUid(), uri));
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(MessagingActivity.this, LinearLayoutManager.VERTICAL, false));
        recyclerViewMessages.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom = 8;
            }
        });
        recyclerViewMessages.setHasFixedSize(true);
    }

    private void setupSendMessage() {
        imageSendMessage.setOnClickListener(v -> {
            String message = editSendMessage.getText().toString();
            String receiver = selectedUser.getUid();
            String date = "";
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                LocalDateTime currentDateTime = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                date = currentDateTime.format(formatter);
            }
            if (!message.equals("")) {
                sendMessage(currentUserUid, receiver, message, date);
            }

        });
    }

    private void sendMessage(String sender, String receiver, String message, String date) {
        tripperMessagingData = RetrofitClient.getClient("https://fcm.googleapis.com/").create(TripperMessagingData.class);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("date", date);
        hashMap.put("chatType", ChatType.SINGLE);
        databaseReference.child("Chats").push().setValue(hashMap).addOnSuccessListener(unused -> {
            editSendMessage.setText("");
            sendNotification(selectedUser.getDeviceToken(),currentUserUid);
        });
        databaseReference.child("ChatList").child(sender).child(receiver).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    databaseReference.child("ChatList").child(sender).child(receiver).child("id").setValue(receiver);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        databaseReference.child("ChatList").child(receiver).child(sender).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists())
                {
                    databaseReference.child("ChatList").child(receiver).child(sender).child("id").setValue(sender);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void sendNotification(String userToken,String userUid) {
        Data data = new Data(userUid, NotificationType.MESSAGE);
        NotificationSender sender = new NotificationSender(data, userToken);
        tripperMessagingData.sendNotification(sender).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                if (response.code() == 200) {
                    if (response.body().success != 1) {
                        Toast.makeText(MessagingActivity.this, "Failed to send notification", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {

            }
        });
    }

    private void readMessages(String receiverUId, String senderUid, Uri imageUrl) {
        chatList = new ArrayList<>();
        databaseReference.child("Chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Chat chat = ds.getValue(Chat.class);
                    if (chat.getReceiver().equals(receiverUId) && chat.getSender().equals(senderUid) ||
                            chat.getReceiver().equals(senderUid) && chat.getSender().equals(receiverUId)) {
                        chatList.add(chat);
                    }
                }
                chatAdapter = new ChatAdapter(MessagingActivity.this, chatList, imageUrl);
                recyclerViewMessages.setAdapter(chatAdapter);
                recyclerViewMessages.scrollToPosition(chatAdapter.getItemCount() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void setupFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        databaseReference = firebaseDatabase.getReference();
        storageReference = firebaseStorage.getReference();
        currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private void setupViews() {
        editSendMessage = findViewById(R.id.edit_send_message);
        imageSendMessage = findViewById(R.id.image_send_message);
        recyclerViewMessages = findViewById(R.id.recycleview_messages);
        toolbar = findViewById(R.id.toolbar_chat);
    }


    private void setupToolbar() {
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(selectedUser.getName());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}