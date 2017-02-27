package com.example.aditya.firebaseuser;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;

/**
 * Created by aditya on 26-02-2017.
 */
public class ChatViewHolder  extends RecyclerView.ViewHolder{
    private final TextView textViewUserName, textViewEmail;
    private final CircularImageView circularImageView;
    View mView;
    public ChatViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        textViewUserName = (TextView) itemView.findViewById(R.id.textViewFromUserNameIBI);
        circularImageView = (CircularImageView) itemView.findViewById(R.id.imageViewProfileIBI);
        textViewEmail = (TextView) itemView.findViewById(R.id.textViewFromEmailIdIBI);
    }

    public void setTextViewUserName(String textViewUserName){
        this.textViewUserName.setText(textViewUserName);
    }

    public void setCircularImageView(Context context, String  imageURl) {
        Glide.with(context).load(Uri.parse(imageURl)).into(circularImageView);
    }

    public void setTextViewEmail(String textViewEmail){
        this.textViewEmail.setText(textViewEmail);
    }
}
