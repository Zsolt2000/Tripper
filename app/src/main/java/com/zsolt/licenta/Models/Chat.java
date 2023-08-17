package com.zsolt.licenta.Models;

import androidx.annotation.NonNull;

import java.util.List;

public class Chat {
    private String sender;
    private String receiver;
    private String message;
    private ChatType chatType;
    private List<String>receiverList;



    private String date;

    public Chat(String sender, String receiver, String message,String date,ChatType chatType) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.date=date;
        this.chatType=chatType;
    }
    public Chat(String sender,List<String> receiverList,String message,String date,ChatType chatType){
        this.sender = sender;
        this.receiverList = receiverList;
        this.message = message;
        this.date=date;
        this.chatType=chatType;
    }
    public Chat(){}


    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ChatType getChatType() {
        return chatType;
    }

    public void setChatType(ChatType chatType) {
        this.chatType = chatType;
    }

    public List<String> getReceiverList() {
        return receiverList;
    }

    public void setReceiverList(List<String> receiverList) {
        this.receiverList = receiverList;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", message='" + message + '\'' +
                ", chatType=" + chatType +
                ", receiverList=" + receiverList +
                ", date='" + date + '\'' +
                '}';
    }
}
