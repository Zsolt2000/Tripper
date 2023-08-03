package com.zsolt.licenta.Models;

import java.util.Date;
import java.util.List;

public class Trips {
    private String title;
    private Users creator;
    private String startDate;
    private int numberOfPeople;
    private String location;
    private List<Users> invitedUsers;

    public Trips(String title, Users creator, String startDate, int numberOfPeople, String location, List<Users> invitedUsers) {
        this.title = title;
        this.creator = creator;
        this.startDate = startDate;
        this.numberOfPeople = numberOfPeople;
        this.location = location;
        this.invitedUsers = invitedUsers;
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

    @Override
    public String toString() {
        return "Trips{" +
                "title='" + title + '\'' +
                ", creator=" + creator +
                ", startDate=" + startDate +
                ", numberOfPeople=" + numberOfPeople +
                ", location='" + location + '\'' +
                ", invitedUsers=" + invitedUsers +
                '}';
    }
}
