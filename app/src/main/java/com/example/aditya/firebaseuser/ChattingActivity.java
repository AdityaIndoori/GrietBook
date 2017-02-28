package com.example.aditya.firebaseuser;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

public class ChattingActivity extends AppCompatActivity implements View.OnClickListener {
    private Button buttonSend;
    private EditText editTextMessage;
    private RecyclerView recyclerViewChat;
    private UserInformation userInformation;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference fromDatabaseReference,toDatabaseReference,downloadDatabaseReferenceUserChatList,toDatabaseReferenceUserChatList,fromDatabaseReferenceUserChatList;;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private LinearLayoutManager linearLayoutManager;
    private Query query;
    private FirebaseRecyclerAdapter<ChatData,ViewHolderChattingActivity> mAdapter;
    private boolean isChattingWithMe;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();



        Intent intent = getIntent();
        userInformation = (UserInformation)intent.getSerializableExtra("UserInfo");

        setTitle(userInformation.getName());

        downloadDatabaseReferenceUserChatList = firebaseDatabase.getReference().child("Chat").child(firebaseUser.getUid()).child(userInformation.getUid());
        fromDatabaseReferenceUserChatList = firebaseDatabase.getReference().child("ChatList").child(firebaseUser.getUid()).child(userInformation.getUid());
        toDatabaseReferenceUserChatList = firebaseDatabase.getReference().child("ChatList").child(userInformation.getUid()).child(firebaseUser.getUid());
        fromDatabaseReference = firebaseDatabase.getReference().child("Chat").child(firebaseUser.getUid()).child(userInformation.getUid());
        toDatabaseReference = firebaseDatabase.getReference().child("Chat").child(userInformation.getUid()).child(firebaseUser.getUid());

        buttonSend = (Button) findViewById(R.id.buttonSendCA);
        editTextMessage = (EditText) findViewById(R.id.editTextCA);
        recyclerViewChat = (RecyclerView) findViewById(R.id.recyclerViewCA);
        buttonSend.setOnClickListener(this);

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewChat.setLayoutManager(linearLayoutManager);

        userId = "";
        isChattingWithMe = true;

        displayChat();


    }

    private void displayChat() {
        Query query = downloadDatabaseReferenceUserChatList.limitToLast(20);
        mAdapter = new FirebaseRecyclerAdapter<ChatData, ViewHolderChattingActivity>(ChatData.class,R.layout.recycler_view_item_ca,ViewHolderChattingActivity.class,query) {

            @Override
            protected void populateViewHolder(ViewHolderChattingActivity viewHolder, ChatData model, int position) {
                if (model.getFromUID().equals(firebaseUser.getUid())){
                    viewHolder.setTextViewSentMessage(model.getMessage());

                }
                else if (model.getFromUID().equals(userInformation.getUid())){
                    viewHolder.setTextViewReceivedMessage(model.getMessage());
                    Vibrator v = (Vibrator)getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(500);
                }
            }
        };

        // Scroll to bottom on new messages
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                linearLayoutManager.smoothScrollToPosition(recyclerViewChat, null, mAdapter.getItemCount());
            }
        });
        recyclerViewChat.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View view) {
        if (view == buttonSend){
            sendMessage();
            editTextMessage.setText("");
        }
    }

    private void sendMessage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Sending..");
        progressDialog.show();
        String message = editTextMessage.getText().toString();
        recyclerViewChat.invalidate();
        if (editTextMessage.getText().toString().equals(null)||editTextMessage.getText().toString().equals("")){
            displayChat();
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
        fromDatabaseReference.push().setValue(chatData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                toDatabaseReference.push().setValue(chatData).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        fromDatabaseReferenceUserChatList.setValue(userInformation.getUid()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                toDatabaseReferenceUserChatList.setValue(firebaseUser.getUid()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.v("Chat","Successfully sent chat!");
                                        progressDialog.hide();
                                        sendPushNotification();
                                        displayChat();
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

    @Override
    protected void onPause() {
        DatabaseReference databaseReference1 = firebaseDatabase.getReference().child("Status").child(firebaseAuth.getCurrentUser().getUid()).child(userInformation.getUid());
        databaseReference1.setValue(false);
        super.onPause();
    }

    @Override
    protected void onStop() {
        DatabaseReference databaseReference1 = firebaseDatabase.getReference().child("Status").child(firebaseAuth.getCurrentUser().getUid()).child(userInformation.getUid());
        databaseReference1.setValue(false);
        super.onStop();
    }

    @Override
    protected void onResume() {

        DatabaseReference databaseReference1 = firebaseDatabase.getReference().child("Status").child(firebaseAuth.getCurrentUser().getUid()).child(userInformation.getUid());
        databaseReference1.setValue(true);
        super.onResume();
    }

}
