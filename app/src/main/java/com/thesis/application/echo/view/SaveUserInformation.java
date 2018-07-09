package com.thesis.application.echo.view;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.thesis.application.echo.R;

import java.io.IOException;
import java.util.HashMap;

public class SaveUserInformation extends AppCompatActivity {

    private EditText editTextFullName, editTextUsername;
    private RadioGroup radioGender;
    private RadioButton radioButton;
    private ImageView profilePicture;

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference dbRef;

    private StorageReference userProfileImageRef;

    private ProgressDialog progressDialog;

    private Uri imageUri;
    private Uri downloadUrl;

    String currentUserId;
    private static final int GALLERY_PICK = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_user_information);

        userProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        currentUserId = firebaseUser.getUid();

        dbRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserId);

        progressDialog = new ProgressDialog(this);
        editTextFullName = findViewById(R.id.editTextFullNameSaveUserInformation);
        editTextUsername = findViewById(R.id.editTextUsernameSaveUserInformation);
        radioGender = findViewById(R.id.radioGroupGenderSaveUserInformation);
        profilePicture = findViewById(R.id.profilePictureSaveUserInformation);

        Button btnSave = findViewById(R.id.btnSaveUserInformation);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInfo();
            }
        });

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "Select Profile Image"), GALLERY_PICK);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK && data!= null && data.getData() != null) {
            imageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                profilePicture.setImageBitmap(bitmap);
                uploadImageToStorage();
            } catch (IOException e) {
                Toast.makeText(this, "Error occurred: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }

        }
    }

    private void uploadImageToStorage() {
        StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile_pict/" + currentUserId + "_" + System.currentTimeMillis() + ".jpg");
        if(imageUri != null) {
            filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    downloadUrl = taskSnapshot.getDownloadUrl();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SaveUserInformation.this, "Error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void saveUserInfo() {
        String fullname = editTextFullName.getText().toString();
        String username = editTextUsername.getText().toString();

        int selectedGender = radioGender.getCheckedRadioButtonId();
        radioButton = findViewById(selectedGender);
        String gender = radioButton.getText().toString();

        if(validationInput(fullname, username, gender)) {
            progressDialog.setTitle("Saving Information");
            progressDialog.setMessage("Please wait, while we are creating your new account..");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(true);

            HashMap<String, Object> userMap = new HashMap<>();
            userMap.put("fullname", fullname);
            userMap.put("username", username);
            userMap.put("gender", gender);
            dbRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        goToMainHome();
                        progressDialog.dismiss();
                        Toast.makeText(SaveUserInformation.this, "Your Account Successfully Registered", Toast.LENGTH_LONG).show();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(SaveUserInformation.this,  "Error occurred: " + task.getException().getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    }

    private void goToMainHome() {
        Intent intent = new Intent(SaveUserInformation.this, MainHome.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private boolean validationInput(String fullname, String username, String gender) {
        if(TextUtils.isEmpty(fullname)) {
            editTextFullName.setError(getString(R.string.full_name_is_empty));
            editTextFullName.requestFocus();
            return false;
        } else if(TextUtils.isEmpty(username)) {
            editTextUsername.setError(getString(R.string.username_isEmpty));
            editTextUsername.requestFocus();
            return false;
        } else {
            return true;
        }
    }
}
