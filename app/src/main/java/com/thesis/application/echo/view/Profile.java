package com.thesis.application.echo.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.thesis.application.echo.R;

public class Profile extends AppCompatActivity implements View.OnClickListener{

    EditText edtTxtUsername, edtTxtEmail, edtTxtPass, edtTxtConfirmPass;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        edtTxtUsername = findViewById(R.id.edtTxtUsernameUpdateProfile);
        edtTxtEmail = findViewById(R.id.edtTxtEmailUpdateProfile);
        edtTxtPass = findViewById(R.id.edtTxtPasswordUpdateProfile);
        edtTxtConfirmPass = findViewById(R.id.edtTxtConfirmPasswordUpdateProfile);

        Button btnUpdate = findViewById(R.id.btnUpdateProfile);
        btnUpdate.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnUpdateProfile:
                break;
        }
    }

    private void loadUserInformation() {
        FirebaseUser fbUser = mAuth.getCurrentUser();
    }
}
