package com.zsolt.licenta;

import com.zsolt.licenta.Models.TripType;
import com.zsolt.licenta.Models.Trips;

import static org.junit.Assert.*;

import org.junit.Test;

public class TripsUnitTest {
    private Trips trip;

    @Test
    public void assertTripTitleNotNull() {
        trip = new Trips();
        String tripTitle = "Hiking";
        trip.setTitle(tripTitle);
        assertEquals(tripTitle, trip.getTitle());

    }

    @Test
    public void assertTripLocationNotNull() {
        trip = new Trips();
        String tripLocation = "Timisoara, Bulevardul Liviu Rebreanu";
        trip.setLocation(tripLocation);
        assertEquals(tripLocation, trip.getLocation());
    }

    @Test
    public void assertTripStartDateNotNull() {
        trip = new Trips();
        String tripStartDate = "2023-15-09";
        trip.setStartDate(tripStartDate);
        assertEquals(tripStartDate, trip.getStartDate());
    }

    @Test
    public void assertTripTypeNotNull() {
        trip = new Trips();
        TripType tripType = TripType.INDOORS;
        trip.setTripType(tripType);
        assertEquals(tripType, trip.getTripType());
    }

    @Test
    public void assertTripPrivacyNotNull() {
        trip = new Trips();
        boolean isTripPrivate = true;
        trip.setPrivate(isTripPrivate);
        assertEquals(isTripPrivate,trip.isPrivate());
    }

    @Test
    public void assertTripInformationNotNull() {
        trip = new Trips();
        String tripInformation="Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.";
        trip.setTripInformation(tripInformation);
        assertEquals(tripInformation,trip.getTripInformation());
    }
}
