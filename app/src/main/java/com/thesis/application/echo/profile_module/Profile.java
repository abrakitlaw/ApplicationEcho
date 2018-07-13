package com.thesis.application.echo.profile_module;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.thesis.application.echo.R;
import com.thesis.application.echo.main_home_module.MainHome;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {

    private static final int GALLERY_PICK = 1001;

    View view;

    FirebaseAuth mAuth;
    DatabaseReference dbRef;
    FirebaseUser firebaseUser;
    StorageReference profilePictRef;

    TextView txtUsername, txtEmail, txtFullName, txtGender;
    CircleImageView profilePicture;

    Uri imageUri;
    String downloadUrl;

    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setTitle("Profile");

        profilePictRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        currentUserId = mAuth.getCurrentUser().getUid();

        dbRef = FirebaseDatabase.getInstance().getReference().child("users");

        profilePicture = findViewById(R.id.profilePicture);
        txtUsername = findViewById(R.id.textViewUsernameProfile);
        txtFullName = findViewById(R.id.textViewFullnameProfile);
        txtGender = findViewById(R.id.textViewGenderProfile);
        txtEmail = findViewById(R.id.textViewEmailProfile);

        loadUserInformationProfile();

        Button btnUpdate = findViewById(R.id.btnEditProfile);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), EditProfile.class);
                startActivity(intent);
            }
        });

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
    }

    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galleryIntent, "Select EditProfile Image"), GALLERY_PICK);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_PICK && resultCode == Activity.RESULT_OK && data != null ) {
            imageUri = data.getData();
            profilePicture.setImageURI(imageUri);
        }
        if(imageUri != null) {
            StorageReference filePath = profilePictRef.child("ProfileImage").child(currentUserId + "_" + System.currentTimeMillis() + ".jpg");
            filePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        downloadUrl = task.getResult().getDownloadUrl().toString();
                        updateUserProfilePicture();
                    } else {
                        Toast.makeText(getApplicationContext(), "Error Occurred: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void updateUserProfilePicture() {
        HashMap<String, Object> updateProfilePictUrlMap = new HashMap<>();
        updateProfilePictUrlMap.put("profilePictUrl", downloadUrl);

        dbRef.child(currentUserId).updateChildren(updateProfilePictUrlMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "EditProfile Picture Updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Error Occurred: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void loadUserInformationProfile() {
        dbRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    String profilePictUrl = dataSnapshot.child("profilePictUrl").getValue().toString();
                    String username = dataSnapshot.child("username").getValue().toString();
                    String fullname = dataSnapshot.child("fullname").getValue().toString();
                    String gender = dataSnapshot.child("gender").getValue().toString();
                    String email = mAuth.getCurrentUser().getEmail().trim();

                    Picasso.get().load(profilePictUrl).placeholder(R.drawable.ic_male_user_profile_picture).into(profilePicture);
                    txtUsername.setText(username);
                    txtFullName.setText(fullname);
                    txtGender.setText(gender);
                    txtEmail.setText(email);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainHome.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
