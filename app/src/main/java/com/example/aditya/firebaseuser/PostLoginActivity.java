package com.example.aditya.firebaseuser;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.File;
import java.io.IOException;

public class PostLoginActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private static final int PICK_IMAGE_REQUEST = 1234;
    private TextView textViewUserName;
    private CircularImageView profileImageView;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private Boolean changedUserNameOnce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_login);
        changedUserNameOnce = false;
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            //User hasn't Logged In:
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        textViewUserName = (TextView) findViewById(R.id.textViewUserNamePL);
        profileImageView = (CircularImageView) findViewById(R.id.imageViewProfilePL);
        profileImageView.setOnLongClickListener(this);
        textViewUserName.setOnLongClickListener(this);
        databaseReference = FirebaseDatabase.getInstance().getReference(firebaseUser.getUid());
        storageReference = FirebaseStorage.getInstance().getReference();

        databaseDetails();
        setTitle("Profile");
    }

    private void storageDetails() {

//        progressDialog.show();

        StorageReference newStorageRef = storageReference.child("images/DP_" + firebaseAuth.getCurrentUser().getUid().trim() + ".jpg");
/*        Glide.with(this).load(firebaseAuth.getCurrentUser().getPhotoUrl())
                .listener(new RequestListener<Uri, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource) {
                        progressDialog.setMessage("Unable to Awsomize!");
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        progressDialog.dismiss();
                        return false;
                    }

                })
                .into(profileImageView);*/
        try {
            final ProgressDialog progressDialog  = new ProgressDialog(this);
            progressDialog.setTitle("Almost There..");
            progressDialog.setMessage("Dethadi pochamma Gudi!");
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
                            double progress = 100 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage((int)progress + "% Loaded");
                        }
                    });

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void databaseDetails() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Loading You Data");
        progressDialog.show();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    progressDialog.dismiss();
                    UserInformation userInfo = dataSnapshot.getValue(UserInformation.class);
                    textViewUserName.setText(userInfo.getName());
                    TextView textView = (TextView) findViewById(R.id.textViewEmailPL);
                    textView.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    if (userInfo!= null) {
                        textView = (TextView) findViewById(R.id.textViewPhoneNumberPL);
                        textView.setText(userInfo.getPhoneNumber());
                        textView = (TextView) findViewById(R.id.textViewAddressPL);
                        textView.setText(userInfo.getAddress());
                        //userdetailsTextView.setText("Email ID: " + FirebaseAuth.getInstance().getCurrentUser().getEmail() + "\n\nName: " + userInfo.getName());
                    }
                    if (!firebaseAuth.getCurrentUser().isEmailVerified()){
                        TextView verificationTextView = (TextView)findViewById(R.id.textViewVerificationPL);
                        verificationTextView.setText(R.string.verificationNotice);
                    }
                    storageDetails();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
                //Toast.makeText(getApplicationContext(),"Failed to get any info",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.post_login_menu, menu);
        if (firebaseAuth.getCurrentUser().isEmailVerified()) {
            MenuItem verifyMenuItem = menu.findItem(R.id.menuVerifyItemPL);
            verifyMenuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemID = item.getItemId();
        if (itemID == R.id.menuLogoutItemPL) {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
        if (itemID == R.id.menuVerifyItemPL) {
            if (!firebaseAuth.getCurrentUser().isEmailVerified()) {
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Sending Email & Logging Out");
                progressDialog.show();
                FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Check your mail", Toast.LENGTH_LONG).show();
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Failed to send mail", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public boolean onLongClick(View view) {
        if (view == profileImageView) {
            longPressedProfilePhoto();
        }

        if (view == textViewUserName){
                longPressedUserName();
        }
        return false;
    }

    private void longPressedUserName() {
        if (!changedUserNameOnce){
            changedUserNameOnce = true;

            Intent intent = new Intent(this,ChangeUserDetailsActivity.class);
            intent.putExtra("change","Name");
            startActivity(intent);
        }
        else
        {
            Intent intent = new Intent(this,ChangeUserDetailsActivity.class);
            intent.putExtra("change","No Can Do!");
            startActivity(intent);
        }
    }

    private void longPressedProfilePhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a Profile Pic"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uriFilePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriFilePath);
                uploadNewPhoto(uriFilePath, bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadNewPhoto(final Uri uriFilePath, final Bitmap bitmap) {
        if (uriFilePath!= null && bitmap != null){
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            StorageReference riversRef = FirebaseStorage.getInstance().getReference().child("images/DP_"+firebaseAuth.getCurrentUser().getUid().trim()+".jpg");
            riversRef.putFile(uriFilePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            Log.v("URL",downloadUrl.toString()+"");
                            progressDialog.setTitle("One moment..");
                            progressDialog.show();
                            UserProfileChangeRequest profilePicUpdate = new UserProfileChangeRequest.Builder().setPhotoUri(Uri.parse(downloadUrl.toString())).build();
                            FirebaseAuth.getInstance().getCurrentUser().updateProfile(profilePicUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "Uploaded Image!", Toast.LENGTH_SHORT).show();
                                        profileImageView.setImageBitmap(bitmap);
                                    }
                                    else
                                        Toast.makeText(getApplicationContext(),"Couldn't update PhotoUri",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Unable to upload",Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = 100 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage((int)progress + "% Uploaded");
                        }
                    });
        }
    }
}
