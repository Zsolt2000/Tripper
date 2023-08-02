package com.zsolt.licenta.Notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.zsolt.licenta.Activities.UserProfileActivity;
import com.zsolt.licenta.Models.Users;
import com.zsolt.licenta.R;


public class TripperMessagingService extends FirebaseMessagingService {
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference();
            databaseReference.child("Users").child(firebaseUser.getUid()).child("deviceToken").setValue(token);
        }

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String sender = remoteMessage.getData().get("sender");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null && sender.equals(firebaseUser.getUid())) {
            sendNotification(remoteMessage);
        }
    }

    private void sendNotification(RemoteMessage remoteMessage) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        databaseReference.child("Users").child(remoteMessage.getData().get("destination")).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Users user = task.getResult().getValue(Users.class);
                createNotification(user);
            }
        });
    }

    private void createNotification(Users user) {
        Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
        intent.putExtra("selectedUser", user);
        String channel_id = "notification_channel";
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent
                = PendingIntent.getActivity(
                getApplicationContext(), 0, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder
                = new NotificationCompat
                .Builder(getApplicationContext(),
                channel_id)
                .setSmallIcon(R.drawable.logo)
                .setAutoCancel(true)
                .setVibrate(new long[]{1000, 1000, 1000,
                        1000, 1000})
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent)
                .setContentTitle("New friend Request")
                .setContentText(user.getName() + " has added you as a friend");
        NotificationManager notificationManager
                = (NotificationManager) getSystemService(
                Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT
                >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel
                    = new NotificationChannel(
                    channel_id, "web_app",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(
                    notificationChannel);
        }

        notificationManager.notify(0, builder.build());
    }

}