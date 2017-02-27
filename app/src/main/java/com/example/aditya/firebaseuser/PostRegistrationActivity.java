package com.example.aditya.firebaseuser;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.IOException;

public class PostRegistrationActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int PICK_IMAGE_REQUEST = 1234;
    private TextView emailPRTextView;
    private Button saveButton;
    private EditText nameEditText, addressEditText, phoneNumberEditText;
    private CircularImageView profileImageView;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private Uri uriFilePath;
    private StorageReference storageReference;
    private UserInformation userInformation;
    private String dpURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_registration);
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null){
            //User hasn't Logged In:
            finish();
            startActivity(new Intent(this,MainActivity.class));
        }

        emailPRTextView = (TextView)findViewById(R.id.textViewHeadingPR);
        saveButton = (Button)findViewById(R.id.buttonSaveInfoPR);
        nameEditText = (EditText)findViewById(R.id.editTextNamePR);
        addressEditText = (EditText)findViewById(R.id.editTextAddressPR);
        phoneNumberEditText = (EditText)findViewById(R.id.editTextPhonePR);
        profileImageView = (CircularImageView )findViewById(R.id.imageViewProfilePR);

        storageReference = FirebaseStorage.getInstance().getReference();//Store Large Data like Images
        databaseReference = FirebaseDatabase.getInstance().getReference();//Store only text format

        emailPRTextView.setText("Fill in the details");
        profileImageView.setImageResource(R.drawable.default_profile);
        profileImageView.setOnClickListener(this);

        userInformation = new UserInformation();
        dpURL = "";
        saveButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        if (view == saveButton){
            pressedSave();
        }

        if (view == profileImageView){
            //Open File Chooser
            pressedImageView();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.post_registration_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemID = item.getItemId();
        if (itemID == R.id.menuLogoutItemPR){
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }
        if (itemID == R.id.menuSkipItemPR){
            finish();
            startActivity(new Intent(getApplicationContext(),PostLoginActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void pressedSave() {
        String name = nameEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();
        String phoneNumber = phoneNumberEditText.getText().toString().trim();
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(address) || TextUtils.isEmpty(phoneNumber)){
            String toastMessage = "Please Enter";
            if (TextUtils.isEmpty(name))
                toastMessage = toastMessage + ", Name";
            if (TextUtils.isEmpty(address))
                toastMessage = toastMessage + ", Address";
            if (TextUtils.isEmpty(phoneNumber))
                toastMessage = toastMessage + ", phone Number";
            Toast.makeText(getApplicationContext(),toastMessage,Toast.LENGTH_SHORT).show();
            return;
        }
        userInformation.setName(name);
        userInformation.setAddress(address);
        userInformation.setPhoneNumber(phoneNumber);
        userInformation.setUid(firebaseAuth.getCurrentUser().getUid());

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(name).build();

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Hold On...");
        progressDialog.show();
        firebaseUser.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                userInformation.setEmailID(firebaseAuth.getCurrentUser().getEmail());
                if (!task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"Couldn't update DisplayName",Toast.LENGTH_SHORT).show();
                }
            }
        });
        databaseReference.child("User").child(firebaseUser.getUid()).setValue(userInformation);
        Toast.makeText(this,"Information Saved!",Toast.LENGTH_SHORT).show();
        uploadImageAndSave();
    }

    private void uploadImageAndSave() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        if (uriFilePath!= null){
            progressDialog.show();
            StorageReference riversRef = storageReference.child("ProfilePhoto/"+firebaseAuth.getCurrentUser().getUid().trim()+".jpg");
            riversRef.putFile(uriFilePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    dpURL = downloadUrl.toString();
                    userInformation.setDpUrl(dpURL);
                    Log.v("URL",downloadUrl.toString()+"");
                    progressDialog.setTitle("One moment..");
                    progressDialog.show();
                    databaseReference.child("User").child(firebaseAuth.getCurrentUser().getUid()).setValue(userInformation);
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(Uri.parse(downloadUrl.toString()))
                            .build();
                    FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Uploaded Image!",Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(getApplicationContext(),PostLoginActivity.class));
                        }
                            else
                                Toast.makeText(getApplicationContext(),"Couldn't update PhotoUri",Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Unable to upload",Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = 100 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount();
                    progressDialog.setMessage((int)progress + "% Uploaded");
                }
            });
        }
        else {
            {
                progressDialog.show();
                StorageReference riversRef = FirebaseStorage.getInstance().getReference().child("ProfilePhoto/"+FirebaseAuth.getInstance().getCurrentUser().getUid().trim()+".jpg");

                Uri uriOfDefaultImage = Uri.parse("android.resource://com.example.aditya.firebaseuser/drawable/"+R.drawable.default_profile);
                riversRef.putFile(uriOfDefaultImage)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.dismiss();
                                finish();
                                startActivity(new Intent(getApplicationContext(),PostLoginActivity.class));
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(),"Unable to upload photo",Toast.LENGTH_SHORT).show();
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
    }

    private void pressedImageView() {
        Intent intent =new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select a Profile Pic"),PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data!=null && data.getData()!=null){
            uriFilePath = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uriFilePath);
                profileImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}