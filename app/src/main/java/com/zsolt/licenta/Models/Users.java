package com.zsolt.licenta.Models;

import android.media.Image;
import android.net.Uri;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Users {


    private String uid;
    private String name;
    private String phoneNumber;
    private int age;
    private String dateOfBirth;
    private String homeLocation;
    private List<Interests> interests;

    private HashMap<Integer, Trips> tripsHashMap;

    private Gender gender;

    private String profileImage;

    public Users() {
    }

    public Users(String uid,String name, String phoneNumber, int age, String dateOfBirth, String homeLocation, List<Interests> interests, HashMap<Integer, Trips> tripsHashMap, String profileImage, Gender gender) {
        this.uid=uid;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.age = age;
        this.dateOfBirth = dateOfBirth;
        this.homeLocation = homeLocation;
        this.interests = interests;
        this.tripsHashMap = new HashMap<>();
        this.gender = gender;
        this.profileImage=profileImage;
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


    public HashMap<Integer, Trips> getTripsHashMap() {
        return tripsHashMap;
    }

    public void setTripsHashMap(HashMap<Integer, Trips> tripsHashMap) {
        this.tripsHashMap = tripsHashMap;
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

    @Override
    public String toString() {
        return "Users{" +
                "uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", age=" + age +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", homeLocation='" + homeLocation + '\'' +
                ", interests=" + interests +
                ", tripsHashMap=" + tripsHashMap +
                ", gender=" + gender +
                ", profileImage='" + profileImage + '\'' +
                '}';
    }
}
