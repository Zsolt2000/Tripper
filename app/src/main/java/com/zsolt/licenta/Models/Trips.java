package com.zsolt.licenta.Models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.zsolt.licenta.Utils.Item;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Trips implements Serializable,Parcelable, Item {
    private String title;
    private Users creator;
    private String startDate;
    private int numberOfPeople;
    private String location;
    private List<Users> invitedUsers;

    private TripType tripType;

    private boolean isPrivate;

    public Trips(String title, Users creator, String startDate, int numberOfPeople, boolean isPrivate,String location, List<Users> invitedUsers,TripType triptype) {
        this.title = title;
        this.creator = creator;
        this.startDate = startDate;
        this.numberOfPeople = numberOfPeople;
        this.location = location;
        this.invitedUsers = invitedUsers;
        this.isPrivate=isPrivate;
        this.tripType=triptype;
    }
    public Trips(){}

    protected Trips(Parcel in) {
        title = in.readString();
        startDate = in.readString();
        numberOfPeople = in.readInt();
        location = in.readString();
        isPrivate = in.readByte() != 0;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Users getCreator() {
        return creator;
    }

    public void setCreator(Users creator) {
        this.creator = creator;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public int getNumberOfPeople() {
        return numberOfPeople;
    }

    public void setNumberOfPeople(int numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<Users> getInvitedUsers() {
        return invitedUsers;
    }

    public void setInvitedUsers(List<Users> invitedUsers) {
        this.invitedUsers = invitedUsers;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public TripType getTripType() {
        return tripType;
    }

    public void setTripType(TripType tripType) {
        this.tripType = tripType;
    }

    @Override
    public String toString() {
        return "Trips{" +
                "title='" + title + '\'' +
                ", creator=" + creator +
                ", startDate='" + startDate + '\'' +
                ", numberOfPeople=" + numberOfPeople +
                ", location='" + location + '\'' +
                ", invitedUsers=" + invitedUsers +
                ", isPrivate=" + isPrivate +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(startDate);
        dest.writeInt(numberOfPeople);
        dest.writeString(location);
        dest.writeByte((byte) (isPrivate ? 1 : 0));
    }


    public static final Creator<Trips> CREATOR = new Creator<Trips>() {
        @Override
        public Trips createFromParcel(Parcel in) {
            return new Trips(in);
        }

        @Override
        public Trips[] newArray(int size) {
            return new Trips[size];
        }
    };
}
