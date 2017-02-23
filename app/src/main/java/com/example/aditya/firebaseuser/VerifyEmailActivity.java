package com.example.aditya.firebaseuser;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerifyEmailActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private TextView textViewEmailVerification;
    private Button buttonVerifyEV,buttonSkipEV;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);
        firebaseAuth =FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser == null){
                    finish();
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                }
            }
        };
        email = getIntent().getStringExtra("email");
        textViewEmailVerification = (TextView) findViewById(R.id.textViewEmailVeriication);
        buttonSkipEV = (Button) findViewById(R.id.buttonSkipEV);
        buttonVerifyEV = (Button) findViewById(R.id.buttonVerifyEV);

        buttonSkipEV.setOnClickListener(this);
        buttonVerifyEV.setOnClickListener(this);

        textViewEmailVerification.setText("Press Verify to send an email to: " + FirebaseAuth.getInstance().getCurrentUser().getEmail() + " and verify, or else Skip");
    }

    @Override
    public void onClick(View view) {
        if (view == buttonSkipEV){
            //Skip to profile
            pressedSkip();
        }
        if (view == buttonVerifyEV){
            pressedVerify();
        }
    }

    private void pressedVerify() {
        if (!firebaseUser.isEmailVerified()){
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Sending Email & Logging Out");
            progressDialog.show();
            firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    firebaseAuth.signOut();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    textViewEmailVerification.setText("Couldn't send verification email.\nPlease skip");
                }
            });
        }

    }

    private void pressedSkip() {
        finish();
        startActivity(new Intent(this,PostRegistrationActivity.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthStateListener!=null)
            firebaseAuth.removeAuthStateListener(mAuthStateListener);
    }
}
