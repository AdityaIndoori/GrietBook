package com.example.aditya.firebaseuser;

/**
 * Created by aditya on 27-02-2017.
 */

public class NotificationData {
    private String notificationHeading, notificationMessage, notificationType, notificationFromUID, notificationToUID;

    public Boolean getNotificationStatus() {
        return notificationStatus;
    }

    public void setNotificationStatus(Boolean notificationStatus) {
        this.notificationStatus = notificationStatus;
    }

    private Boolean notificationStatus;

    public NotificationData() {
        notificationHeading = "null";
        notificationMessage = "null";
        notificationType = "null";
        notificationFromUID = "null";
        notificationToUID = "null";
        notificationStatus = false;
    }

    public String getNotificationHeading() {
        return notificationHeading;
    }

    public void setNotificationHeading(String notificationHeading) {
        this.notificationHeading = notificationHeading;
    }

    public String getNotificationMessage() {
        return notificationMessage;
    }

    public void setNotificationMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getNotificationFromUID() {
        return notificationFromUID;
    }

    public void setNotificationFromUID(String notificationFromUID) {
        this.notificationFromUID = notificationFromUID;
    }

    public String getNotificationToUID() {
        return notificationToUID;
    }

    public void setNotificationToUID(String notificationToUID) {
        this.notificationToUID = notificationToUID;
    }
}
