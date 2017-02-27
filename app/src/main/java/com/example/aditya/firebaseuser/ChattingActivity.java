package com.example.aditya.firebaseuser;

import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

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

        displayChat();


    }

    private void displayChat() {
        Query query = downloadDatabaseReferenceUserChatList.orderByValue().limitToLast(50);
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
        String message = editTextMessage.getText().toString();
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
                                        //Toast.makeText(getApplicationContext(),"Message Sent",Toast.LENGTH_SHORT).show();
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
