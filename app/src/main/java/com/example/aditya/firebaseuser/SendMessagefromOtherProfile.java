package com.example.aditya.firebaseuser;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

public class SendMessagefromOtherProfile extends AppCompatActivity implements View.OnClickListener {
    private EditText editTextSendMessage;
    private TextView textViewFromTo;
    private Button buttonSendMessage;
    private UserInformation userInformation;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference fromDatabaseReference,toDatabaseReference, toDatabaseReferenceUserChatList,fromDatabaseReferenceUserChatList;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private boolean isChattingWithMe;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_messagefrom_other_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        setTitle("Send Message");
        Intent intent = getIntent();
        userInformation = (UserInformation)intent.getSerializableExtra("UserInfo");

        fromDatabaseReferenceUserChatList = firebaseDatabase.getReference().child("ChatList").child(firebaseUser.getUid()).child(userInformation.getUid());
        toDatabaseReferenceUserChatList = firebaseDatabase.getReference().child("ChatList").child(userInformation.getUid()).child(firebaseUser.getUid());
        fromDatabaseReference = firebaseDatabase.getReference().child("Chat").child(firebaseUser.getUid()).child(userInformation.getUid()).push();
        toDatabaseReference = firebaseDatabase.getReference().child("Chat").child(userInformation.getUid()).child(firebaseUser.getUid()).push();

        textViewFromTo = (TextView) findViewById(R.id.textViewFromToMOP);
        editTextSendMessage = (EditText) findViewById(R.id.editTextSendMessageMOP);
        buttonSendMessage = (Button) findViewById(R.id.buttonSendMessageMOP);

        textViewFromTo.setText("From: " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName() + "\nTo: " + userInformation.getName());
        editTextSendMessage.setHint("Enter Message...");
        buttonSendMessage.setOnClickListener(this);

        isChattingWithMe = true;
        userId = "";
    }

    @Override
    public void onClick(View view) {
        if (view == buttonSendMessage){
            sendMesasge();
        }
    }

    private void sendMesasge() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Sending..");
        progressDialog.show();
        String message = editTextSendMessage.getText().toString();
        if (message.length()<1){
            progressDialog.hide();
            return;
        }
        final ChatData chatData = new ChatData();
        chatData.setFromUID(firebaseUser.getUid());
        chatData.setToUID(userInformation.getUid());
        chatData.setFromUserName(firebaseUser.getDisplayName());
        chatData.setToUserName(userInformation.getName());
        chatData.setMessage(message);
        chatData.setFromDpUrl(firebaseUser.getPhotoUrl().toString());
        chatData.setToDpUrl(userInformation.getDpUrl());
        fromDatabaseReference.setValue(chatData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                toDatabaseReference.setValue(chatData).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        fromDatabaseReferenceUserChatList.setValue(userInformation.getUid()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                toDatabaseReferenceUserChatList.setValue(firebaseUser.getUid()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        progressDialog.dismiss();
                                        sendPushNotification();
                                        Toast.makeText(getApplicationContext(),"Message Sent",Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }

    private void sendPushNotification() {
        Log.v("send","Inside sendPushNotification method!");

        DatabaseReference databaseReference1 = firebaseDatabase.getReference().child("Status").child(userInformation.getUid()).child(firebaseAuth.getCurrentUser().getUid());
        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.v("DatasnapShot","Value of boolean: " + dataSnapshot.getValue());
                if (dataSnapshot.exists())
                    isChattingWithMe = (boolean)dataSnapshot.getValue();
                Log.v("Boolean","New Value of boolean: " + isChattingWithMe);
                if (!isChattingWithMe){
                    DatabaseReference databaseReference2 = firebaseDatabase.getReference().child("OneSignalID").child(userInformation.getUid());
                    databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.v("DatasnapShot","Value of userId: " + dataSnapshot.getValue());
                            if (dataSnapshot.exists())
                                userId = (String) dataSnapshot.getValue();
                            Log.v("UserID","Value of userId: " + userId);
                            if (!userId.equals("")) {
                                Log.v("OneSignal","Inside The IF");
                                try {
                                    JSONObject jsonObject = new JSONObject("{'contents': {'en':'Message from: "+firebaseUser.getDisplayName()+"'}, 'include_player_ids': ['" + userId + "']}");
                                    OneSignal.postNotification(jsonObject, new OneSignal.PostNotificationResponseHandler() {
                                        @Override
                                        public void onSuccess(JSONObject response) {
                                            Log.v("OneSignal","Push Mesasge Success");
                                        }

                                        @Override
                                        public void onFailure(JSONObject response) {
                                            Log.v("OneSignal","Failed to post a notification");
                                        }
                                    });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.v("Cancelled","Cancelled USer Id");
                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v("Cancelled","Cancelled Boolean");
            }
        });
//        Log.v("OneSignal","Value of: isCHattingWithMe: "  + isChattingWithMe.toString() + "Value of userId: " + userId[0].trim());
    }

}
