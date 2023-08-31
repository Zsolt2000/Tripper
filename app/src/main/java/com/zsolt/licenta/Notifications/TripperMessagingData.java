package com.zsolt.licenta.Notifications;

import com.zsolt.licenta.BuildConfig;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface TripperMessagingData {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key="+BuildConfig.FCM_AUTH_KEY
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body NotificationSender body);
}
