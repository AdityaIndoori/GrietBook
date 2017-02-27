package com.example.aditya.firebaseuser;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by aditya on 27-02-2017.
 */

public class ViewHolderChattingActivity extends RecyclerView.ViewHolder {
    private final TextView textViewMessageSent, textViewMessageReceived;
    public ViewHolderChattingActivity(View itemView) {
        super(itemView);
        textViewMessageReceived = (TextView) itemView.findViewById(R.id.textViewChatItemReceiveCA);
        textViewMessageSent = (TextView) itemView.findViewById(R.id.textViewChatItemSentCA);
    }

    public void setTextViewSentMessage(String textViewMessage1){
        textViewMessageSent.setText(textViewMessage1);
        textViewMessageReceived.setVisibility(View.GONE);
    }

    public void setTextViewReceivedMessage(String textViewMessage1){
        textViewMessageReceived.setText(textViewMessage1);
        textViewMessageSent.setVisibility(View.GONE);
    }

}
