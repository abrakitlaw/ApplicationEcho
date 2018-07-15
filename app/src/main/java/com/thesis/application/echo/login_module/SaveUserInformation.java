package com.thesis.application.echo.login_module;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.thesis.application.echo.R;
import com.thesis.application.echo.main_home_module.MainHome;
import com.thesis.application.echo.models.User;

import java.io.IOException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SaveUserInformation extends AppCompatActivity {

    private EditText editTextFullName, editTextUsername;
    private RadioGroup radioGender;
    private RadioButton radioButton;
    private CircleImageView profilePicture;
    private ProgressBar progressBarSaveUser;

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference dbRef;
    private LinearLayout grayBackground;

    private StorageReference userProfileImageRef;

    private Uri imageUri;
    private String downloadUrl, fullname, username, gender;

    String currentUserId;
    private static final int GALLERY_PICK = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_user_information);

        userProfileImageRef = FirebaseStorage.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        currentUserId = firebaseUser.getUid();

        dbRef = FirebaseDatabase.getInstance().getReference().child("users");

        progressBarSaveUser = findViewById(R.id.progressBarSaveUserInformation);
        grayBackground = findViewById(R.id.linearLayoutGrayBackgroundSaveUserInformation);
        editTextFullName = findViewById(R.id.editTextFullNameSaveUserInformation);
        editTextUsername = findViewById(R.id.editTextUsernameSaveUserInformation);
        radioGender = findViewById(R.id.radioGroupGenderSaveUserInformation);
        profilePicture = findViewById(R.id.profilePictureSaveUserInformation);

        Button btnSave = findViewById(R.id.btnSaveUserInformation);

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserRegistrationInformation();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK && data!= null) {
            imageUri = data.getData();
            profilePicture.setImageURI(imageUri);

        }
    }

    private void saveUserRegistrationInformation() {
        fullname = editTextFullName.getText().toString();
        username = editTextUsername.getText().toString();
        int selectedGender = radioGender.getCheckedRadioButtonId();
        radioButton = findViewById(selectedGender);
        gender = radioButton.getText().toString();

        if(validationInput(imageUri, fullname, username, gender)) {
            uploadImageToStorage();
        }
    }


    private void uploadImageToStorage() {
        progressBarSaveUser.setVisibility(View.VISIBLE);
        grayBackground.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        StorageReference filePath = userProfileImageRef.child("ProfileImage").child(imageUri.getLastPathSegment() + "_" + username + "_" + System.currentTimeMillis() + ".jpg");
        filePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    downloadUrl = task.getResult().getDownloadUrl().toString();
                    saveUserInfo();
                    goToMainHome();
                } else {
                    Toast.makeText(SaveUserInformation.this, "Error occurred: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveUserInfo() {
        progressBarSaveUser.setVisibility(View.GONE);
        grayBackground.setVisibility(View.VISIBLE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        dbRef.child(currentUserId).setValue(new User(username, fullname, gender, downloadUrl)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(SaveUserInformation.this, "User information successfully saved", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void goToMainHome() {
        Intent intent = new Intent(SaveUserInformation.this, MainHome.class);
        startActivity(intent);
        finish();
    }

    private boolean validationInput(Uri imageUri, String fullname, String username, String gender) {
        if(TextUtils.isEmpty(fullname)) {
            editTextFullName.setError(getString(R.string.full_name_is_empty));
            editTextFullName.requestFocus();
            return false;
        } else if(TextUtils.isEmpty(username)) {
            editTextUsername.setError(getString(R.string.username_isEmpty));
            editTextUsername.requestFocus();
            return false;
        } else if(imageUri == null) {
            Toast.makeText(SaveUserInformation.this, "Please select your profile picture", Toast.LENGTH_LONG).show();
            return false;
        } else if(TextUtils.isEmpty(gender)) {
            radioButton.setError("Please select your gender");
            radioButton.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(progressBarSaveUser.getVisibility() == View.VISIBLE) {
            progressBarSaveUser.setVisibility(View.GONE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }
}
