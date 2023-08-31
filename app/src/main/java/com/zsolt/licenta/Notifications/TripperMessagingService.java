package com.zsolt.licenta.Notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.zsolt.licenta.Activities.MessagingActivity;
import com.zsolt.licenta.Activities.TripActivity;
import com.zsolt.licenta.Activities.UserProfileActivity;
import com.zsolt.licenta.Models.Trips;
import com.zsolt.licenta.Models.Users;
import com.zsolt.licenta.R;

import java.util.Random;


public class TripperMessagingService extends FirebaseMessagingService {
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private NotificationType notificationType;
    private int notificationId;

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
        Random random=new Random();
        notificationId=random.nextInt(9999-1000);
        notificationType = NotificationType.valueOf(remoteMessage.getData().get("notificationType"));
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String messageData = remoteMessage.getData().get("messageData");
        if (firebaseUser != null) {
            sendNotification(messageData);
        }
    }

    private void sendNotification(String messageData) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        switch (notificationType) {
            case FRIEND_REQUEST:
                databaseReference.child("Users").child(messageData).get().addOnSuccessListener(dataSnapshot -> {
                    Users users = dataSnapshot.getValue(Users.class);
                    createFriendRequestNotification(users);
                });
                break;
            case NEW_TRIP:
                databaseReference.child("Trips").child(messageData).get().addOnSuccessListener(dataSnapshot -> {
                    Trips trip = dataSnapshot.getValue(Trips.class);
                    if (trip.getTitle().equals(messageData)) {
                        createTripInviteNotification(trip.getCreator().getName(), trip.getTitle());

                    }
                });
                break;
            case MESSAGE:
                databaseReference.child("Users").child(messageData).get().addOnSuccessListener(dataSnapshot -> {
                    Users users = dataSnapshot.getValue(Users.class);
                    createMessageNotification(users);
                });
                break;
            case JOIN_TRIP:
                databaseReference.child("Trips").child(messageData).get().addOnSuccessListener(dataSnapshot -> {
                    Trips trip = dataSnapshot.getValue(Trips.class);
                    if (trip.getTitle().equals(messageData)) {
                        createJoinedTripNotification(trip.getTitle());

                    }
                });
            default:
                Log.i("NOTIFICATION_SERVICE", "Unknown notification type");
                break;
        }

    }

    private void createJoinedTripNotification(String title) {
        Intent intent = new Intent(getApplicationContext(), TripActivity.class);
        intent.putExtra("trip", title);
        String channel_id = "joined_channel";
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
                .setContentTitle("User joined your trip")
                .setContentText("A new user has joined your trip: "+title);
        NotificationManager notificationManager
                = (NotificationManager) getSystemService(
                Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT
                >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel
                    = new NotificationChannel(
                    channel_id, channel_id,
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(
                    notificationChannel);
        }

        notificationManager.notify(notificationId, builder.build());
    }

    private void createMessageNotification(Users user) {
        Intent intent = new Intent(getApplicationContext(), MessagingActivity.class);
        intent.putExtra("chat", user);
        String channel_id = "message_channel";
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
                .setContentTitle("New Message")
                .setContentText(user.getName() + " has sent you a message");
        NotificationManager notificationManager
                = (NotificationManager) getSystemService(
                Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT
                >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel
                    = new NotificationChannel(
                    channel_id, channel_id,
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(
                    notificationChannel);
        }

        notificationManager.notify(notificationId, builder.build());
    }

    private void createFriendRequestNotification(Users user) {
        Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
        intent.putExtra("selectedUser", user);
        String channel_id = "friend_channel";
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
                    channel_id, channel_id,
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(
                    notificationChannel);
        }

        notificationManager.notify(notificationId, builder.build());
    }

    private void createTripInviteNotification(String tripCreator, String tripTitle) {
        Intent intent = new Intent(getApplicationContext(), TripActivity.class);
        intent.putExtra("trip", tripTitle);
        String channel_id = "trip_channel";
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
                .setContentTitle("New trip invite")
                .setContentText(tripCreator + " has invited you to a trip");
        NotificationManager notificationManager
                = (NotificationManager) getSystemService(
                Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT
                >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel
                    = new NotificationChannel(
                    channel_id, channel_id,
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(
                    notificationChannel);
        }

        notificationManager.notify(notificationId, builder.build());
    }


}