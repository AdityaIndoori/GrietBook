package com.example.aditya.firebaseuser;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginFragment extends Fragment implements View.OnClickListener{
    private EditText email, password;
    private Button loginNow;
    private ProgressDialog progressdailog;
    private FirebaseAuth fireBaseAuth;
    private TextView textViewNewUser,textViewForgotPassword;
    public LoginFragment() {
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
        if (fireBaseAuth.getCurrentUser() != null){//i.e user already logged in
            // Start Profile Activity
            getActivity().finish();
            startActivity(new Intent(getActivity(),PostLoginActivity.class));
        }
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_login,container,false);
        email = (EditText)rootView.findViewById(R.id.editTextEmail);
        password = (EditText)rootView.findViewById(R.id.editTextPassword);
        loginNow = (Button)rootView.findViewById(R.id.buttonLogin);
        progressdailog = new ProgressDialog(getContext());

        loginNow.setOnClickListener(this);
        textViewNewUser = (TextView)rootView.findViewById(R.id.textViewNewUser);
        textViewNewUser.setOnClickListener(this);
        textViewForgotPassword = (TextView)rootView.findViewById(R.id.textViewForgotPassword);
        textViewForgotPassword.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View view) {
        if (view == loginNow)
            userLogin();
        if (view == textViewNewUser){
            ViewPager viewpager = MainActivity.mPager;
            viewpager.setCurrentItem(1,true);
        }
        if (view == textViewForgotPassword){
            //Forgot Password?
            startActivity(new Intent(getContext(),ForgotPasswordActivity.class));
        }

    }

    private void userLogin() {
        String userNameStr = email.getText().toString().trim();
        String passwordStr = password.getText().toString().trim();
        if (TextUtils.isEmpty(userNameStr)){
            //Email is Empty
            Toast.makeText(getContext(),"Please Enter Email Address",Toast.LENGTH_SHORT).show();
            //To Stop further execution
            return;
        }
        if (TextUtils.isEmpty(passwordStr)){
            //Password is Empty
            Toast.makeText(getContext(),"Please Enter Password",Toast.LENGTH_SHORT).show();
            //To Stop further execution
            return;
        }

        //If validations are OK:
        //First show a progressDialog
        progressdailog.setMessage("Logging In the User...");
        progressdailog.show();
        fireBaseAuth.signInWithEmailAndPassword(userNameStr,passwordStr).addOnCompleteListener(getActivity(),new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressdailog.dismiss();
                if (task.isSuccessful()){
                    //Intent to Profile Activity
                    Toast.makeText(getActivity(),"Login Successful",Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                    startActivity(new Intent(getActivity(),PostLoginActivity.class));
                }
                else
                    Toast.makeText(getActivity(),"Failed to Login in",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
