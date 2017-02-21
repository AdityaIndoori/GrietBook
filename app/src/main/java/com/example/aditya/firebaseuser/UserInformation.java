package com.example.aditya.firebaseuser;

/**
 * Created by aditya on 21-02-2017.
 */

public class UserInformation {
private String Name, Address, PhoneNumber;

    public UserInformation() {
        Name = "No Name";
        Address = "No Address";
        PhoneNumber = "No Phone Number";
    }

    public void setName(String name) {
        Name = name;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getName() {
        return Name;
    }

    public String getAddress() {
        return Address;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }
}
