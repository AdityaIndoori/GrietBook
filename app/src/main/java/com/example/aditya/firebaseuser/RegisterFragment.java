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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RegisterFragment extends Fragment implements View.OnClickListener{
    private EditText emailEditText, passwordEditText;
    private Button registerButton;
    private ProgressDialog progressDialog;
    private FirebaseAuth fireBaseAuth;
    private TextView alreadyRegisteredTextView, forgotPasswordTextView;
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
            Toast.makeText(getContext(),getString(R.string.forgot_password_toast),Toast.LENGTH_LONG).show();
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

        progressDialog.setMessage("Registering User...");
        progressDialog.show();
        fireBaseAuth.createUserWithEmailAndPassword(emailIdStr,passwordStr).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(),"Registration Successful!",Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                    startActivity(new Intent(getContext(),PostRegistrationActivity.class));
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
}