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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.zsolt.licenta.Activities.MessagingActivity;
import com.zsolt.licenta.Activities.TripActivity;
import com.zsolt.licenta.Activities.UserProfileActivity;
import com.zsolt.licenta.Models.NotificationType;
import com.zsolt.licenta.Models.Trips;
import com.zsolt.licenta.Models.Users;
import com.zsolt.licenta.R;

import java.io.Serializable;
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
        String destination = remoteMessage.getData().get("destination");
        if (firebaseUser != null) {
            sendNotification(destination);
        }
    }

    private void sendNotification(String destination) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        switch (notificationType) {
            case FRIEND_REQUEST:
                databaseReference.child("Users").child(destination).get().addOnSuccessListener(dataSnapshot -> {
                    Users users = dataSnapshot.getValue(Users.class);
                    createNotification(users);
                });
                break;
            case NEW_TRIP:
                databaseReference.child("Trips").child(destination).get().addOnSuccessListener(dataSnapshot -> {
                    Trips trip = dataSnapshot.getValue(Trips.class);
                    if (trip.getTitle().equals(destination)) {
                        createNotification(trip.getCreator().getName(), trip.getTitle());

                    }
                });
                break;
            case MESSAGE:
                databaseReference.child("Users").child(destination).get().addOnSuccessListener(dataSnapshot -> {
                    Users users = dataSnapshot.getValue(Users.class);
                    createMessageNotification(users);
                });
                break;
            default:
                Log.i("NOTIFICATION_SERVICE", "Unknown notification type");
                break;
        }

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

    private void createNotification(Users user) {
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

    private void createNotification(String tripCreator, String tripTitle) {
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