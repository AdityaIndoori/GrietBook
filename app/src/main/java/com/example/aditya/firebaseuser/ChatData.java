package com.example.aditya.firebaseuser;

/**
 * Created by aditya on 26-02-2017.
 */

public class ChatData {
    private String fromUserName;
    private String toUserName;
    private String fromUID;
    private String toUID;
    private String message;
    private String toDpUrl;
    private String fromDpUrl;

    public String getToDpUrl() {
        return toDpUrl;
    }

    public void setToDpUrl(String toDpUrl) {
        this.toDpUrl = toDpUrl;
    }

    public String getFromDpUrl() {
        return fromDpUrl;
    }

    public void setFromDpUrl(String fromDpUrl) {
        this.fromDpUrl = fromDpUrl;
    }

    public ChatData() {
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    public String getFromUID() {
        return fromUID;
    }

    public void setFromUID(String fromUID) {
        this.fromUID = fromUID;
    }

    public String getToUID() {
        return toUID;
    }

    public void setToUID(String toUID) {
        this.toUID = toUID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
