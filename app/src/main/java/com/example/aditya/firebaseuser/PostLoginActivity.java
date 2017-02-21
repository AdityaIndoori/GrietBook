package com.example.aditya.firebaseuser;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.File;
import java.io.IOException;

public class PostLoginActivity extends AppCompatActivity {

    private TextView PLTextView,userdetailsTextView;
    private CircularImageView profileImageView;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_login);
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null){
            //User hasn't Logged In:
            startActivity(new Intent(this,MainActivity.class));
        }

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        PLTextView = (TextView)findViewById(R.id.textViewPL);
        userdetailsTextView = (TextView)findViewById(R.id.textViewUserDetailsPL);
        profileImageView = (CircularImageView) findViewById(R.id.imageViewProfilePL);

        databaseReference = FirebaseDatabase.getInstance().getReference(firebaseUser.getUid());
        storageReference = FirebaseStorage.getInstance().getReference();

        storageDetails();
        databaseDetails();
        setTitle("Profile");
    }

    private void storageDetails() {
        StorageReference newStorageRef = storageReference.child("images/DP_" + firebaseAuth.getCurrentUser().getUid().trim() + ".jpg");
        try {
            final ProgressDialog progressDialog  = new ProgressDialog(this);
            progressDialog.setTitle("Loading Profile...");
            progressDialog.show();
            final File localFile = File.createTempFile("images", "jpg");
            newStorageRef.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            profileImageView.setImageURI(Uri.parse(new File(localFile.getAbsolutePath()).toString()));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Unable to download",Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100 * taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage((int)progress + "% Loaded");
                        }
                    });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void databaseDetails() {

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot!=null){
                    UserInformation userInfo = dataSnapshot.getValue(UserInformation.class);
                    PLTextView.setText("Welcome " + userInfo.getName()+"!");
                    userdetailsTextView.setText("\nName: " + userInfo.getName() + "\n\nAddress: " + userInfo.getAddress() + "\n\nPhone Number: " + userInfo.getPhoneNumber());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Toast.makeText(getApplicationContext(),"Failed to get any info",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.post_login_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemID = item.getItemId();
        if (itemID == R.id.menuLogoutItemPL){
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this,MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
