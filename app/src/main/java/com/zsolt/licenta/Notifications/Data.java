package com.zsolt.licenta.Notifications;

import com.zsolt.licenta.Models.Users;

public class Data {
    private String Title,Message,sender,destination;



    private Users user;

    public Data(String title, String message,String sender) {
        this.Title = title;
        this.Message = message;
        this.sender=sender;
    }

    public Data(String sender,String destination) {
        this.sender=sender;
        this.destination=destination;
    }


    public Data() {
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

}
