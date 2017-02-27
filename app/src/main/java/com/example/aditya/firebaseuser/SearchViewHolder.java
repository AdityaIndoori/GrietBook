package com.example.aditya.firebaseuser;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by aditya on 25-02-2017.
 */

public class SearchViewHolder extends RecyclerView.ViewHolder {
    private final TextView textViewUserName, textViewEmailId;
    private final LinearLayout linearLayout;
    View mView;
    public SearchViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        textViewUserName = (TextView) itemView.findViewById(R.id.textViewUserNameSearch);
        linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayoutListItem);
        textViewEmailId = (TextView) itemView.findViewById(R.id.textViewEmailSearch);
    }

    public void setTextViewUserName(String stringUserName, String stringEmailID){
        textViewUserName.setText(stringUserName);
        textViewEmailId.setText(stringEmailID);
    }

    public void dontShow(){
        linearLayout.setVisibility(View.GONE);
        textViewUserName.setVisibility(View.GONE);
        textViewEmailId.setVisibility(View.GONE);
    }

}
