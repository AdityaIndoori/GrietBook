package com.example.aditya.firebaseuser;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

public class ChangeUserDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextUserDetail;
    private Button buttonUserDetail;
    private TextView textViewHeading;
    private String change;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReferenceUpload,databaseReferenceDownload;
    private UserInformation userInformation;
    private FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user_details);
        change = getIntent().getStringExtra("change");
        setTitle(change);
        editTextUserDetail = (EditText) findViewById(R.id.editTextUserDetailCUD);
        buttonUserDetail = (Button) findViewById(R.id.buttonUserDetailCUD);
        textViewHeading = (TextView) findViewById(R.id.textViewHeadingCUD);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (change.equals("No Can Do!")){
            editTextUserDetail.setVisibility(View.GONE);
            buttonUserDetail.setVisibility(View.GONE);
            textViewHeading.setTextSize(14);
            textViewHeading.setText("Its your name!\n\nDon't keep changing it!\n\nSign Out and Sign In again if you want to!");
            return;
        }

            editTextUserDetail.setHint("Enter New " + change);

            textViewHeading.setVisibility(View.GONE);

        buttonUserDetail.setOnClickListener(this);
        databaseReferenceUpload = FirebaseDatabase.getInstance().getReference();
        databaseReferenceDownload = FirebaseDatabase.getInstance().getReference(firebaseUser.getUid());
    }

    @Override
    public void onClick(View view) {
        String userDetail = editTextUserDetail.getText().toString();
        if (change.equals("Name")){
            changeUserName(userDetail);
        }
        if (change.equals("No Can Do!")){

        }
    }

    private void changeUserName(final String userDetail) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Loading Data");
        progressDialog.show();
        databaseReferenceDownload.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot!=null){
                    progressDialog.dismiss();
                    userInformation = dataSnapshot.getValue(UserInformation.class);
                    if (userInformation!=null)
                    userInformation.setName(userDetail);
                    else {
                        userInformation = new UserInformation();
                        userInformation.setName(userDetail);
                    }
                    upLoadChangedData(userDetail);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Failed to get any info",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void upLoadChangedData(String userDetail) {
        //For database
        databaseReferenceUpload.child(firebaseUser.getUid()).setValue(userInformation);
        //For User Profile:
        if (change.equals("Name")){
            UserProfileChangeRequest updateProfile = new UserProfileChangeRequest.Builder().setDisplayName(userDetail).build();
            firebaseUser.updateProfile(updateProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        //Toast.makeText(getApplicationContext(),"Updated Profile",Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(getApplicationContext(),"Failed to update Profile",Toast.LENGTH_SHORT).show();
                }
            });

        }
        finish();
    }
}
