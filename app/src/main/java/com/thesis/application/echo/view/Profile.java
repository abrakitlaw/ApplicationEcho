package com.thesis.application.echo.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thesis.application.echo.R;
import com.thesis.application.echo.models.User;

import static com.thesis.application.echo.view.fragment.ProfileFragment.USER_ID;

public class Profile extends AppCompatActivity implements View.OnClickListener{

    EditText edtTxtUsername, edtTxtEmail, edtTxtPass, edtTxtConfirmPass;

    FirebaseAuth mAuth;
    FirebaseDatabase mFireBaseDatabase;
    FirebaseUser firebaseUser;
    DatabaseReference dbRef;

    String userId;
    User user;
    String emailCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setTitle("Edit Profile");

        mAuth = FirebaseAuth.getInstance();
        mFireBaseDatabase = FirebaseDatabase.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        emailCurrentUser = firebaseUser.getEmail();

        edtTxtUsername = findViewById(R.id.edtTxtUsernameUpdateProfile);
        edtTxtEmail = findViewById(R.id.edtTxtEmailUpdateProfile);
        edtTxtPass = findViewById(R.id.edtTxtPasswordUpdateProfile);
        edtTxtConfirmPass = findViewById(R.id.edtTxtConfirmPasswordUpdateProfile);

        Button btnUpdate = findViewById(R.id.btnUpdateProfile);
        btnUpdate.setOnClickListener(this);

        setTitle("Edit Profile");
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnUpdateProfile:
                updateUser();
                break;
        }
    }

    private void updateUser() {
        Intent intent = new Intent();
        Bundle bd = intent.getExtras();

        userId = bd.getString(USER_ID);


        final String username = edtTxtUsername.getText().toString().trim();
        final String email = edtTxtEmail.getText().toString().trim();
        final String password = edtTxtPass.getText().toString().trim();
        final String confirmPass = edtTxtConfirmPass.getText().toString().trim();

    }

    private boolean updateValidation(String username, String email, String password, String confirmPass) {
        username = edtTxtUsername.getText().toString().trim();
        email = edtTxtEmail.getText().toString().trim();
        password = edtTxtPass.getText().toString().trim();
        confirmPass = edtTxtConfirmPass.getText().toString().trim();

        if(TextUtils.isEmpty(username)) {
            edtTxtUsername.setError(getString(R.string.username_isEmpty));
            edtTxtUsername.requestFocus();
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

    }
}
