package com.example.aditya.firebaseuser;

import java.io.Serializable;

/**
 * Created by aditya on 21-02-2017.
 */

public class UserInformation implements Serializable{
private String Name, Address, PhoneNumber, status, uid, dpUrl, emailID;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDpUrl() {
        return dpUrl;
    }

    public void setDpUrl(String dpUrl) {
        this.dpUrl = dpUrl;
    }

    public UserInformation() {
        Name = "No Name";
        Address = "No Address";
        PhoneNumber = "No Phone Number";
        status = "No Status";
        uid = "No Uid";
        dpUrl = "No DP Url";
        emailID = "No Email ID";
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEmailID() {
        return emailID;
    }

    public void setEmailID(String emailID) {
        this.emailID = emailID;
    }
}
