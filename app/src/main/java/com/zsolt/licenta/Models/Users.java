package com.zsolt.licenta.Models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class Users {
    private String name;
    private String phoneNumber;
    private int age;
    private Date dateOfBirth;
    private String homeLocation;
    private List<Interests> interests;
    private ProfileStatus profileStatus;
    private HashMap<Integer, Trips> tripsHashMap;

    public Users(String name, String phoneNumber, int age, Date dateOfBirth, String homeLocation, List<Interests> interests, ProfileStatus profileStatus, HashMap<Integer, Trips> tripsHashMap) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.age = age;
        this.dateOfBirth = dateOfBirth;
        this.homeLocation = homeLocation;
        this.interests = new ArrayList<Interests>();
        this.profileStatus = profileStatus;
        this.tripsHashMap = new HashMap<Integer,Trips>();
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

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
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

    public ProfileStatus getProfileStatus() {
        return profileStatus;
    }

    public void setProfileStatus(ProfileStatus profileStatus) {
        this.profileStatus = profileStatus;
    }

    public HashMap<Integer, Trips> getTripsHashMap() {
        return tripsHashMap;
    }

    public void setTripsHashMap(HashMap<Integer, Trips> tripsHashMap) {
        this.tripsHashMap = tripsHashMap;
    }
}
