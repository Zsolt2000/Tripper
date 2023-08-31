package com.zsolt.licenta.Notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface TripperMessagingData {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAnh25hhA:APA91bGtRQquIpggUEdenmlg8u_E2Nk_0ePG8kZ1KHmAGV3gcv76ON_i_woHIUcD-hEOXKxkO_t-Mf2Vnxk-7Tynyuzwn9xIAbfulJ-7RwBYaFnZaAasT4lfxzJKam4J0prodRiJ4cfK"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body NotificationSender body);
}
