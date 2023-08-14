package com.zsolt.licenta.Notifications;

import com.zsolt.licenta.Models.NotificationType;
import com.zsolt.licenta.Models.Users;

import java.util.List;

public class Data {
    private String destination;
    private NotificationType notificationType;

    public Data(String destination,NotificationType notificationType) {
        this.destination=destination;
        this.notificationType=notificationType;
    }
}
