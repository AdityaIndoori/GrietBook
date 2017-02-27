package com.example.aditya.firebaseuser;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;

public class OtherProfilePostSearchActivity extends AppCompatActivity {
    private UserInformation userInformation;
    private TextView textViewUserName, textViewStatus, textViewEmailId;
    private CircularImageView circularImageViewProfilePic;
    private String stringUid;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile_post_search);
        setTitle("You're at:");
        Intent intent = getIntent();
        userInformation = (UserInformation)intent.getSerializableExtra("UserInfo");
        if (userInformation==null){
            Log.v("IF","information null");
            return;
        }
        stringUid = userInformation.getUid();
        textViewUserName = (TextView) findViewById(R.id.textViewUserNameOP);
        textViewStatus = (TextView) findViewById(R.id.textViewStatusOP);
        textViewEmailId = (TextView) findViewById(R.id.textViewEmailOP);
        circularImageViewProfilePic = (CircularImageView) findViewById(R.id.imageViewProfileOP);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("User").child(stringUid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists())
                    return;
                userInformation = dataSnapshot.getValue(UserInformation.class);
                Glide.with(getApplicationContext()).load(Uri.parse(userInformation.getDpUrl())).into(circularImageViewProfilePic);
                textViewUserName.setText(userInformation.getName());
                textViewStatus.setText(userInformation.getStatus());
                textViewEmailId.setText(userInformation.getEmailID());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        storageReference = FirebaseStorage.getInstance().getReference().child("ProfilePhoto").child(stringUid + ".jpg");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.other_profile_post_search_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menuSendMessageOP){
            Intent intent = new Intent(this,SendMessagefromOtherProfile.class);
            intent.putExtra("UserInfo",userInformation);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
