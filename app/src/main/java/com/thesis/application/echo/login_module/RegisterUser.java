package com.thesis.application.echo.login_module;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.thesis.application.echo.R;
import com.thesis.application.echo.main_home_module.MainHome;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener {

    private ProgressBar progressBarRegister;
    private EditText email, password;

    private DatabaseReference dbRefUser;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        dbRefUser = FirebaseDatabase.getInstance().getReference("users");

        mAuth = FirebaseAuth.getInstance();

        progressBarRegister = findViewById(R.id.progressBarRegister);
        email = findViewById(R.id.editTextEmailSignUp);
        onFocusChangeListener(email);

        password = findViewById(R.id.editTextPasswordSignUp);
        onFocusChangeListener(password);

        findViewById(R.id.btnRegister).setOnClickListener(this);


    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mAuth.getCurrentUser() != null) {
           goToMainHome();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRegister:
                addUser();
                break;
        }
    }

    private void addUser() {
        final String emailUser = email.getText().toString().trim();
        final String passwordUser = password.getText().toString().trim();

        if (userRegisterValidation(emailUser, passwordUser)) {
            progressBarRegister.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(emailUser, passwordUser).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressBarRegister.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        goToMainHome();
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.user_register_failed), Toast.LENGTH_LONG).show();

                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(getApplicationContext(), "Error Occurred: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    private void goToMainHome() {
        Intent intent = new Intent(RegisterUser.this, MainHome.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private boolean userRegisterValidation(String emailInputted, String passwordInputted) {
        emailInputted = email.getText().toString().trim();
        passwordInputted = password.getText().toString().trim();
        if (emailInputted.isEmpty()) {
            email.setError(getString(R.string.email_isEmpty));
            email.requestFocus();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailInputted).matches()) {
            email.setError(getString(R.string.email_invalid));
            email.requestFocus();
            return false;
        }
        if (passwordInputted.isEmpty()) {
            password.setError(getString(R.string.password_isEmpty));
            password.requestFocus();
            return false;
        }
        if (passwordInputted.length() < 6) {
            password.setError(getString(R.string.password_minimum_length));
            password.requestFocus();
            return false;
        }
        return true;
    }

    public void onFocusChangeListener(EditText editText) {
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
