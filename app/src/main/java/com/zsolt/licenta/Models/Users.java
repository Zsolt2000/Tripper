package com.zsolt.licenta.Models;

import android.media.Image;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zsolt.licenta.Utils.Item;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
public class Users implements Serializable, Item {


    private String uid;
    private String name;
    private String phoneNumber;
    private int age;
    private String dateOfBirth;
    private String homeLocation;
    private List<Interests> interests;
    private HashMap<String,Users> friendsList;
    private String deviceToken;
    private Gender gender;
    private String profileImage;
    public Users() {
    }

    public Users(String uid, String name, String phoneNumber, int age, String dateOfBirth, String homeLocation, List<Interests> interests, String profileImage, Gender gender,HashMap<String,Users>friendsList,String deviceToken) {
        this.uid = uid;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.age = age;
        this.dateOfBirth = dateOfBirth;
        this.homeLocation = homeLocation;
        this.interests = interests;
        this.gender = gender;
        this.profileImage = profileImage;
        this.deviceToken=deviceToken;
        this.friendsList=friendsList;
    }


    protected Users(Parcel in) {
        uid = in.readString();
        name = in.readString();
        phoneNumber = in.readString();
        age = in.readInt();
        dateOfBirth = in.readString();
        homeLocation = in.readString();
        deviceToken = in.readString();
        profileImage = in.readString();
    }


    public HashMap<String,Users> getFriendsList() {
        return friendsList;
    }

    public void setFriendsList(HashMap<String,Users> friendsList) {
        this.friendsList = friendsList;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getHomeLocation() {
        return homeLocation;
    }

    public void setHomeLocation(String homeLocation) {
        this.homeLocation = homeLocation;
    }

    public List<Interests> getInterests() {
        return interests;
    }

    public void setInterests(List<Interests> interests) {
        this.interests = interests;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Users user = (Users) obj;
        return Objects.equals(uid, user.uid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid);
    }

}
