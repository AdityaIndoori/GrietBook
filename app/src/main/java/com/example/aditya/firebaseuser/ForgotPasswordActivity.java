
package com.example.aditya.firebaseuser;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText editTextEmail;
    private Button buttonSendEmail;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        setTitle("Forgot Password?");
        editTextEmail = (EditText) findViewById(R.id.editTextEmailFP);
        buttonSendEmail = (Button) findViewById(R.id.buttonSendEmailFP);
        firebaseAuth = FirebaseAuth.getInstance();
        buttonSendEmail.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == buttonSendEmail){
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Sending Mail..");
            progressDialog.show();
            firebaseAuth.sendPasswordResetEmail(editTextEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    progressDialog.dismiss();
                    if (!task.isSuccessful()){
                        Toast.makeText(getApplication(),"Email is incorrect",Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(getApplicationContext(),"Check you mail",Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
