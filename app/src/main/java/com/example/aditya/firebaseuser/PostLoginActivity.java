package com.example.aditya.firebaseuser;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.IOException;

public class PostLoginActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private static final int PICK_IMAGE_REQUEST = 1234;
    private TextView textViewUserName,textViewStatus;
    private RelativeLayout relativeLayoutPhoneNumber, relativeLayoutAddress;
    private EditText editTextStatus;
    private Button buttonStatus;
    private ScrollView scrollView;
    private CircularImageView profileImageView;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;
    private UserInformation userInformation;
    private Toast toast;
    private String viewLongPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_login);
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            //User hasn't Logged In:
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
        toast = new Toast(this);
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        textViewStatus = (TextView) findViewById(R.id.textViewStatusPL);
        textViewUserName = (TextView) findViewById(R.id.textViewUserNamePL);
        editTextStatus = (EditText) findViewById(R.id.editTextDetailsPL);
        buttonStatus = (Button) findViewById(R.id.buttonDetailsPL);
        scrollView = (ScrollView) findViewById(R.id.scrollViewUserDetailsPL);
        profileImageView = (CircularImageView) findViewById(R.id.imageViewProfilePL);
        relativeLayoutPhoneNumber = (RelativeLayout) findViewById(R.id.relativeLayoutPhoneNumberPL);
        relativeLayoutAddress = (RelativeLayout) findViewById(R.id.relativeLayoutAddressPL);
        profileImageView.setOnLongClickListener(this);
        textViewUserName.setOnLongClickListener(this);
        textViewStatus.setOnLongClickListener(this);
        relativeLayoutPhoneNumber.setOnLongClickListener(this);
        relativeLayoutAddress.setOnLongClickListener(this);
        databaseReference = FirebaseDatabase.getInstance().getReference("User/"+firebaseUser.getUid());
        storageReference = FirebaseStorage.getInstance().getReference();
        editTextStatus.setVisibility(View.GONE);
        buttonStatus.setVisibility(View.GONE);
        buttonStatus.setOnClickListener(this);
        userInformation = new UserInformation();
        setTitle("Profile");
        databaseDetails();
    }

    private void databaseDetails() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Loading Your Data");
        progressDialog.show();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()){
                    FirebaseDatabase.getInstance().getReference().child("User").child(firebaseAuth.getCurrentUser().getUid()).setValue(userInformation);
                    UserProfileChangeRequest userName = new UserProfileChangeRequest.Builder().setDisplayName(userInformation.getName()).build();
                    firebaseAuth.getCurrentUser().updateProfile(userName);
                }

                else  {
                    UserInformation userInformation1 = new UserInformation();
                    userInformation1 = dataSnapshot.getValue(UserInformation.class);
                    progressDialog.hide();
                    if (userInformation1.getName()!=null)
                        userInformation.setName(dataSnapshot.getValue(UserInformation.class).getName());

                    if (dataSnapshot.getValue(UserInformation.class).getAddress()!=null)
                        userInformation.setAddress(dataSnapshot.getValue(UserInformation.class).getAddress());

                    if (dataSnapshot.getValue(UserInformation.class).getAddress()!=null)
                        userInformation.setAddress(dataSnapshot.getValue(UserInformation.class).getAddress());

                    if (dataSnapshot.getValue(UserInformation.class).getPhoneNumber()!=null)
                        userInformation.setPhoneNumber(dataSnapshot.getValue(UserInformation.class).getPhoneNumber());

                    if (dataSnapshot.getValue(UserInformation.class).getStatus()!=null)
                        userInformation.setStatus(dataSnapshot.getValue(UserInformation.class).getStatus());

                    userInformation.setUid(firebaseAuth.getCurrentUser().getUid());
                    userInformation.setDpUrl(firebaseAuth.getCurrentUser().getPhotoUrl().toString());
                    userInformation.setEmailID(firebaseAuth.getCurrentUser().getEmail());

                    UserProfileChangeRequest userName = new UserProfileChangeRequest.Builder().setDisplayName(userInformation.getName()).build();
                    firebaseAuth.getCurrentUser().updateProfile(userName);

                    FirebaseDatabase.getInstance().getReference().child("User").child(firebaseAuth.getCurrentUser().getUid()).setValue(userInformation);
                    textViewUserName.setText(userInformation.getName());
                    TextView textView = (TextView) findViewById(R.id.textViewEmailPL);
                    textView.setText(userInformation.getEmailID());
                    textViewStatus.setText(userInformation.getStatus());
                    if (userInformation!= null) {
                        textView = (TextView) findViewById(R.id.textViewPhoneNumberPL);
                        textView.setText(userInformation.getPhoneNumber());
                        textView = (TextView) findViewById(R.id.textViewAddressPL);
                        textView.setText(userInformation.getAddress());
                        //userdetailsTextView.setText("Email ID: " + FirebaseAuth.getInstance().getCurrentUser().getEmail() + "\n\nName: " + userInfo.getName());
                    }
                    storageDetails();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.hide();
            }
        });
    }
    private void storageDetails() {
        progressDialog.setTitle("Almost There..");
        progressDialog.show();

        Glide.with(getApplicationContext()).load(Uri.parse(userInformation.getDpUrl()))
                .listener(new RequestListener<Uri, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource) {
                        progressDialog.setMessage("Unable to Awsomize!");
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        progressDialog.hide();
                        return false;
                    }

                })
                .into(profileImageView);
//        StorageReference newStorageRef = storageReference.child("ProfilePhotos/" + firebaseAuth.getCurrentUser().getUid().trim() + ".jpg");
        /*try {
            progressDialog.setTitle("Almost There..");
            progressDialog.show();
            final File localFile = File.createTempFile("images", "jpg");
            newStorageRef.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.hide();
                            profileImageView.setImageURI(Uri.parse(new File(localFile.getAbsolutePath()).toString()));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.hide();
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
        }*/

    }

    private void longPressedProfilePhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a Profile Pic"), PICK_IMAGE_REQUEST);
    }
    private void downLoadDatabaseandUpdatePhoto(final Uri uriFilePath, final Bitmap bitmap) {
        if (uriFilePath!= null && bitmap != null){
            progressDialog.setTitle("Uploading...");
            progressDialog.setMessage("Photo being uploaded!..");
            progressDialog.show();
            //Storage Image Upload:
            StorageReference riversRef = FirebaseStorage.getInstance().getReference().child("ProfilePhoto/"+firebaseAuth.getCurrentUser().getUid().trim()+".jpg");
            riversRef.putFile(uriFilePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.hide();
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            Log.v("URL",downloadUrl.toString()+"");
                            //Profile Image Upload:
                            progressDialog.setMessage("Photo almost uploaded!..");
                            progressDialog.show();
                            UserProfileChangeRequest profilePicUpdate = new UserProfileChangeRequest.Builder().setPhotoUri(Uri.parse(downloadUrl.toString())).build();
                            FirebaseAuth.getInstance().getCurrentUser().updateProfile(profilePicUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        userInformation.setDpUrl(firebaseAuth.getCurrentUser().getPhotoUrl().toString());
                                        FirebaseDatabase.getInstance().getReference().child("User").child(firebaseAuth.getCurrentUser().getUid()).setValue(userInformation);
                                        progressDialog.hide();
                                        if (toast!=null)
                                            toast.cancel();
                                        toast = Toast.makeText(getApplicationContext(), "Uploaded Image!", Toast.LENGTH_SHORT);
                                        toast.show();
                                        profileImageView.setImageBitmap(bitmap);
                                    }
                                    else{
                                        if (toast!=null)
                                            toast.cancel();
                                        toast = Toast.makeText(getApplicationContext(),"Couldn't update Profile Pic",Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                }
                            });
                        }
                    })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.hide();
                            if (toast!=null)
                                toast.cancel();
                            toast = Toast.makeText(getApplicationContext(),"Unable to upload to Storage",Toast.LENGTH_SHORT);
                            toast.show();
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

    private void longPressedUserName() {
        viewLongPressed = "User Name";
        buttonStatus.setText("Update");
        editTextStatus.setText(userInformation.getName());
        editTextStatus.setHint("Enter new Name..");
        editTextStatus.setInputType(InputType.TYPE_CLASS_TEXT);
        editTextStatus.setMaxLines(1);
    }
    private void downLoadDatabaseandUpdateUserName(String stringStatus) {
        userInformation.setName(stringStatus);
        FirebaseDatabase.getInstance().getReference().child("User").child(firebaseAuth.getCurrentUser().getUid()).setValue(userInformation);
        UserProfileChangeRequest profileNameUpdate = new UserProfileChangeRequest.Builder().setDisplayName(stringStatus).build();
        firebaseAuth.getCurrentUser().updateProfile(profileNameUpdate).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                finish();
                startActivity(new Intent(getApplicationContext(),PostLoginActivity.class));
            }
        });
    }

    private void longPressedStatus() {
        viewLongPressed = "Status";
        buttonStatus.setText("Update");
        editTextStatus.setHint("Enter new status..");
        editTextStatus.setText(userInformation.getStatus());
        editTextStatus.setInputType(InputType.TYPE_CLASS_TEXT);
        editTextStatus.setMaxLines(2);
    }
    private void downLoadDatabaseandUpdateStatus(final String stringStatus) {
        userInformation.setStatus(stringStatus);
        FirebaseDatabase.getInstance().getReference().child("User").child(firebaseAuth.getCurrentUser().getUid()).setValue(userInformation);
        finish();
        startActivity(new Intent(this,PostLoginActivity.class));
    }

    private void longPressedPhoneNumber() {
        viewLongPressed = "Phone Number";
        buttonStatus.setText("Update");
        editTextStatus.setText(userInformation.getPhoneNumber());
        editTextStatus.setHint("Enter new Phone Number..");
        editTextStatus.setInputType(InputType.TYPE_CLASS_PHONE);
    }
    private void downLoadDatabaseandUpdatePhoneNumber(String stringStatus) {
        userInformation.setPhoneNumber(stringStatus);
        FirebaseDatabase.getInstance().getReference().child("User").child(firebaseAuth.getCurrentUser().getUid()).setValue(userInformation);
        finish();
        startActivity(new Intent(this,PostLoginActivity.class));
    }

    private void longPressedAddress() {
        viewLongPressed = "Address";
        buttonStatus.setText("Update");
        editTextStatus.setText(userInformation.getAddress());
        editTextStatus.setHint("Enter new Address..");
        editTextStatus.setInputType(InputType.TYPE_CLASS_TEXT);
    }
    private void downLoadDatabaseandUpdateAddress(String stringStatus) {
        userInformation.setAddress(stringStatus);
        FirebaseDatabase.getInstance().getReference().child("User").child(firebaseAuth.getCurrentUser().getUid()).setValue(userInformation);
        finish();
        startActivity(new Intent(this,PostLoginActivity.class));
    }

    private void verificationEmail(){
        if (!firebaseAuth.getCurrentUser().isEmailVerified()) {
            progressDialog.setTitle("Verification..");
            progressDialog.setMessage("Logging out, Please check your Email Inbox to verify!");
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
                    progressDialog.hide();
                    if (toast!=null)
                        toast.cancel();
                    toast = Toast.makeText(getApplicationContext(), "Failed to send mail", Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
        }
    }

    private void signOutUser() {
        if (firebaseAuth.getCurrentUser()!=null){
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uriFilePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriFilePath);
                downLoadDatabaseandUpdatePhoto(uriFilePath, bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
            signOutUser();
        }
        if (itemID == R.id.menuVerifyItemPL) {
            verificationEmail();
        }

        if (itemID == R.id.menuSearchItemPL){
            startActivity(new Intent(this,SearchUserActivity.class));
        }

        if (itemID == R.id.menuInboxItemPL)
            startActivity(new Intent(this,InboxActivity.class));

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if (view == buttonStatus){
            String stringStatus = editTextStatus.getText().toString().trim();
            if (stringStatus.length()>0){
                if (viewLongPressed.equals("User Name"))
                downLoadDatabaseandUpdateUserName(stringStatus);

                if (viewLongPressed.equals("Status"))
                    downLoadDatabaseandUpdateStatus(stringStatus);

                if (viewLongPressed.equals("Phone Number"))
                    downLoadDatabaseandUpdatePhoneNumber(stringStatus);

                if (viewLongPressed.equals("Address"))
                    downLoadDatabaseandUpdateAddress(stringStatus);
            }
        }
    }

    @Override
    public boolean onLongClick(View view) {

        if (view == profileImageView) {
            longPressedProfilePhoto();
        }
        else {
            textViewUserName.setVisibility(View.GONE);
            textViewStatus.setVisibility(View.GONE);
            scrollView.setVisibility(View.GONE);

            editTextStatus.setVisibility(View.VISIBLE);
            buttonStatus.setVisibility(View.VISIBLE);

            if (view == textViewUserName){
                longPressedUserName();
            }

            if (view == textViewStatus){
                longPressedStatus();
            }

            if (view == relativeLayoutPhoneNumber){
                longPressedPhoneNumber();
            }

            if (view == relativeLayoutAddress){
                longPressedAddress();
            }

        }
        return false;
    }


}
