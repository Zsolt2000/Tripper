package com.zsolt.licenta;

import com.zsolt.licenta.Models.Gender;
import com.zsolt.licenta.Models.Users;

import org.junit.Test;
import static org.junit.Assert.*;

public class UsersMockTest {

    private Users user;

    @Test
    public void assertUserNameNotNull() {
        user = new Users();
        String userName="Test";
        user.setName(userName);
        assertEquals(userName,user.getName());
    }
    @Test
    public void assertUserDeviceTokenNotNull() {
        user = new Users();
        String userDeviceToken="KLJHUGUIGY-IOUHoiHUiouhFYTUf";
        user.setDeviceToken(userDeviceToken);
        assertEquals(userDeviceToken,user.getDeviceToken());
    }
    @Test
    public void assertUserAgeNull() {
        user = new Users();
        int userAge=20;
        user.setAge(userAge);
        assertEquals(userAge,user.getAge());
    }
    @Test
    public void assertUserPhoneNotNull() {
        user = new Users();
        String userPhoneNumber="07459865432";
        user.setPhoneNumber(userPhoneNumber);
        assertEquals(userPhoneNumber,user.getPhoneNumber());
    }
    @Test
    public void assertUserLocationNotNull() {
        user = new Users();
        String userLocation="Timisoara";
        user.setHomeLocation(userLocation);
        assertEquals(userLocation,user.getHomeLocation());
    }
    @Test
    public void assertUserDateOfBirthNotNull() {
        user = new Users();
        String userDateOfBirth="2000-14-09";
        user.setDateOfBirth(userDateOfBirth);
        assertEquals(userDateOfBirth,user.getDateOfBirth());
    }
    @Test
    public void assertUserGenderNotNull() {
        user = new Users();
        Gender userGender=Gender.MALE;
        user.setGender(userGender);
        assertEquals(userGender,user.getGender());
    }
    @Test
    public void asserUserUidNotNull()
    {
        user=new Users();
        String userUid="oIUHiouHOUIygYITfYTfjLHuiuioETWSD";
        user.setUid(userUid);
        assertEquals(userUid,user.getUid());
        }

}
