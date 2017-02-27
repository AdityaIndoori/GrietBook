package com.example.aditya.firebaseuser;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SendMessagefromOtherProfile extends AppCompatActivity implements View.OnClickListener {
    private EditText editTextSendMessage;
    private TextView textViewFromTo;
    private Button buttonSendMessage;
    private UserInformation userInformation;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference fromDatabaseReference,toDatabaseReference, toDatabaseReferenceUserChatList,fromDatabaseReferenceUserChatList;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

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
    }

    @Override
    public void onClick(View view) {
        if (view == buttonSendMessage){
            sendMesasge();
        }
    }

    private void sendMesasge() {
        String message = editTextSendMessage.getText().toString();
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
}
