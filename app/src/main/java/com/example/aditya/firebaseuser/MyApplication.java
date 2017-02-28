package com.example.aditya.firebaseuser;

import android.app.Application;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.onesignal.OneSignal;

/**
 * Created by aditya on 28-02-2017.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            //User hasn't Logged In:
            return;
        }
        String firebaseUID = firebaseAuth.getCurrentUser().getUid();
        if (firebaseUID.length()>4){
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            final DatabaseReference databaseReference= firebaseDatabase.getReference().child("OneSignalID").child(firebaseUID);
            OneSignal.startInit(this)
                    .setNotificationOpenedHandler(new ExampleNotificationOpenedHandler(getApplicationContext()))
                    .setNotificationReceivedHandler(new ExampleNotificationReceivedHandler())
                    .init();
            OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
                @Override
                public void idsAvailable(String userId, String registrationId) {
                    databaseReference.setValue(userId);
                }
            });
        }
    }
}
