package com.example.aditya.firebaseuser;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class RegisterFragment extends Fragment implements View.OnClickListener{
    private EditText emailEditText, passwordEditText;
    private Button registerButton;
    private ProgressDialog progressDialog;
    private FirebaseAuth fireBaseAuth;
    private TextView alreadyRegisteredTextView, forgotPasswordTextView;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fireBaseAuth = FirebaseAuth.getInstance();
        if (fireBaseAuth.getCurrentUser() != null){
            // Start Profile Activity
            //getActivity().finish();
        }
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_register,container,false);
        emailEditText = (EditText)rootView.findViewById(R.id.editTextEmailRegister);
        passwordEditText = (EditText)rootView.findViewById(R.id.editTextPasswordRegister);
        registerButton = (Button)rootView.findViewById(R.id.buttonRegister);
        progressDialog = new ProgressDialog(getContext());
        alreadyRegisteredTextView = (TextView)rootView.findViewById(R.id.textViewAlreadyRegistered);
        forgotPasswordTextView = (TextView)rootView.findViewById(R.id.textViewForgotPasswordRegister);
        alreadyRegisteredTextView.setOnClickListener(this);
        forgotPasswordTextView.setOnClickListener(this);
        registerButton.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View view) {
        if (view == registerButton){
            registerUser();
        }
        if (view == alreadyRegisteredTextView){
            ViewPager viewPager = MainActivity.mPager;
            viewPager.setCurrentItem(0,true);
        }
        if (view == forgotPasswordTextView){
            startActivity(new Intent(getContext(),ForgotPasswordActivity.class));
            //Forgot Password
        }
    }

    private void registerUser() {
        final String emailIdStr = emailEditText.getText().toString().trim();
        final String passwordStr = passwordEditText.getText().toString().trim();
        if (TextUtils.isEmpty(emailIdStr)){
            //Email is Empty
            Toast.makeText(getActivity(),"Please Enter Email Address",Toast.LENGTH_SHORT).show();
            //To Stop further execution
            return;
        }
        if (TextUtils.isEmpty(passwordStr)){
            //Password is Empty
            Toast.makeText(getActivity(),"Please Enter Password",Toast.LENGTH_SHORT).show();
            //To Stop further execution
            return;
        }

        progressDialog.setMessage("Registering You...");
        progressDialog.show();
        fireBaseAuth.createUserWithEmailAndPassword(emailIdStr,passwordStr).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    progressDialog.dismiss();
                    defaultDetails(emailIdStr);
                }
                else{
                    //User Failed to register
                    // Lets just display a toast:
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(),"Registration Failed!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void defaultDetails(String emailIdStr) {
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();//Store Large Data like Images
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();//Store only text format
        UserInformation userInformation = new UserInformation();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        firebaseUser.updateEmail(emailIdStr)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful())
                    Toast.makeText(getContext(),"Unable to set User Email ID",Toast.LENGTH_SHORT).show();
            }
        });

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(userInformation.getName())
                .setPhotoUri(Uri.parse("https://firebasestorage.googleapis.com/v0/b/fir-user-3480c.appspot.com/o/Default%20Image%2Fdefault_pic.jpg?alt=media&token=47b49f58-063b-4f73-a431-9efccfd432c0"))
                .build();

        firebaseUser.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful())
                    Toast.makeText(getContext(),"Unable to set User Profile",Toast.LENGTH_SHORT).show();
            }
        });

        databaseReference.child(firebaseUser.getUid()).setValue(userInformation);//We have stored the text data
        uploadDefaultImage(emailIdStr);//Upload the default profile photo
    }

    private void uploadDefaultImage(final String emailIdStr) {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Please wait...");
        progressDialog.show();
        StorageReference riversRef = storageReference.child("images/DP_"+firebaseAuth.getCurrentUser().getUid().trim()+".jpg");
        Uri uriOfDefaultImage = Uri.parse("android.resource://com.example.aditya.firebaseuser/drawable/"+R.drawable.default_profile);
        riversRef.putFile(uriOfDefaultImage)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        getActivity().finish();
                        startActivity(new Intent(getContext(),VerifyEmailActivity.class).putExtra("email",emailIdStr));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(),"Unable to upload photo",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = 100 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount();
                        progressDialog.setMessage((int)progress + "% Completed");
                    }
                });
    }
}