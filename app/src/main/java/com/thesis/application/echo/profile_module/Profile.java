package com.thesis.application.echo.profile_module;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.thesis.application.echo.R;
import com.thesis.application.echo.main_home_module.MainHome;

import java.util.HashMap;

public class Profile extends AppCompatActivity {

    EditText edtTxtUsername, edtTxtFullname, edtTxtEmail, edtTxtPass, edtTxtConfirmPass;
    RadioGroup radioSexGroup;
    RadioButton radioBtnSex;

    FirebaseAuth mAuth;
    FirebaseDatabase mFireBaseDatabase;
    FirebaseUser firebaseUser;
    DatabaseReference dbRef;

    int selectedGender;
    String currentUserId;
    String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setTitle("Edit Profile");

        mAuth = FirebaseAuth.getInstance();
        mFireBaseDatabase = FirebaseDatabase.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        currentUserId = mAuth.getCurrentUser().getUid();
        currentUserEmail = mAuth.getCurrentUser().getEmail();


        dbRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserId);

        edtTxtUsername = findViewById(R.id.edtTxtUsernameUpdateProfile);
        edtTxtFullname = findViewById(R.id.edtTxtFullnameUpdateProfile);
        radioSexGroup = findViewById(R.id.radioGroupGenderEdit);
        selectedGender = radioSexGroup.getCheckedRadioButtonId();
        radioBtnSex = findViewById(selectedGender);
        edtTxtEmail = findViewById(R.id.edtTxtEmailUpdateProfile);
        edtTxtPass = findViewById(R.id.edtTxtPasswordUpdateProfile);
        edtTxtConfirmPass = findViewById(R.id.edtTxtConfirmPasswordUpdateProfile);

        Button btnUpdate = findViewById(R.id.btnUpdateProfile);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUser();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadUserInformation();
    }

    private void goToMainMenu() {
        Intent intent = new Intent(getApplicationContext(), MainHome.class );
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void updateUser() {
        String username = edtTxtUsername.getText().toString().trim();
        String fullname = edtTxtFullname.getText().toString().trim();
        final String email = edtTxtEmail.getText().toString();
        int selectedGender = radioSexGroup.getCheckedRadioButtonId();
        radioBtnSex = findViewById(selectedGender);
        String gender = radioBtnSex.getText().toString();
        String password = edtTxtPass.getText().toString();
        final String confirmPass = edtTxtConfirmPass.getText().toString();

        if(updateValidation(username, fullname, email, password, confirmPass)) {
            HashMap<String, Object> userMapUpdate = new HashMap<>();
            userMapUpdate.put("username", username);
            userMapUpdate.put("fullname", fullname);
            userMapUpdate.put("gender", gender);

            dbRef.updateChildren(userMapUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        updateAuth(email, confirmPass);
                        goToMainMenu();
                        Toast.makeText(Profile.this, "Profile Successfully Updated", Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(Profile.this, "Error Occurred: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });

        }

    }

    private void updateAuth(String currentEmail, String confirmPass) {
        FirebaseUser firebaseUserUpdate =  mAuth.getCurrentUser();
        firebaseUserUpdate.updateEmail(currentEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {

                } else {
                    Toast.makeText(getApplicationContext(), "Error occurred: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        firebaseUserUpdate.updatePassword(confirmPass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {

                } else {
                    Toast.makeText(getApplicationContext(), "Error occurred: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean updateValidation(String username, String fullname, String email, String password, String confirmPass) {

        if(TextUtils.isEmpty(username)) {
            edtTxtUsername.setError(getString(R.string.username_isEmpty));
            edtTxtUsername.requestFocus();
            return false;
        }
        if(TextUtils.isEmpty(fullname)) {
            edtTxtFullname.setError(getString(R.string.full_name_is_empty));
            edtTxtFullname.requestFocus();
            return false;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtTxtEmail.setError(getString(R.string.email_invalid));
            edtTxtEmail.requestFocus();
            return false;
        }
        if(TextUtils.isEmpty(password)) {
            edtTxtPass.setError(getString(R.string.password_isEmpty));
            edtTxtPass.requestFocus();
            return false;
        }
        if(TextUtils.isEmpty(confirmPass)) {
            edtTxtConfirmPass.setError(getString(R.string.password_isEmpty));
            edtTxtConfirmPass.requestFocus();
            return false;
        }
        if(!password.equals(confirmPass)){
            Toast.makeText(this, "Your Password doesn't match", Toast.LENGTH_LONG).show();
            edtTxtConfirmPass.requestFocus();
            return false;
        }
        return true;
    }


    private void loadUserInformation() {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    String username = dataSnapshot.child("username").getValue().toString();
                    String fullname = dataSnapshot.child("fullname").getValue().toString();

                    String email = currentUserEmail;

                    edtTxtUsername.setText(username);
                    edtTxtFullname.setText(fullname);
                    edtTxtEmail.setText(email);
                    }
                }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
