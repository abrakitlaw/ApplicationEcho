package com.thesis.application.echo.view;

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
import com.thesis.application.echo.models.User;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener {

    private ProgressBar progressBarRegister;
    private EditText email, username, password;

    private DatabaseReference dbRefUser;
    private FirebaseAuth userAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        dbRefUser = FirebaseDatabase.getInstance().getReference("users");

        userAuth = FirebaseAuth.getInstance();

        progressBarRegister = findViewById(R.id.progressBarRegister);
        email = findViewById(R.id.editTextEmailSignUp);
        onFocusChangeListener(email);

        username = findViewById(R.id.editTextUsernameSignUp);
        onFocusChangeListener(username);

        password = findViewById(R.id.editTextPasswordSignUp);
        onFocusChangeListener(password);

        findViewById(R.id.btnRegister).setOnClickListener(this);


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
        final String usernameUser = username.getText().toString().trim();

        if (userRegisterValidation()) {
            progressBarRegister.setVisibility(View.VISIBLE);
            userAuth.createUserWithEmailAndPassword(emailUser, passwordUser).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressBarRegister.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), getString(R.string.user_register_success), Toast.LENGTH_LONG).show();

                        String idUser = "";
                        if(userAuth.getCurrentUser() != null) {
                            idUser = userAuth.getCurrentUser().getUid();
                        }

                        User user = new User(idUser, usernameUser, emailUser, passwordUser);
                        dbRefUser.child(idUser).setValue(user);

                        finish();
                        startActivity(new Intent(RegisterUser.this, MainHome.class));
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.user_register_failed), Toast.LENGTH_LONG).show();

                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(getApplicationContext(), "You are already registered", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    private boolean userRegisterValidation() {
        String emailInputted = email.getText().toString().trim();
        String usernameInputted = username.getText().toString().trim();
        String passwordInputted = password.getText().toString().trim();
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
        if (usernameInputted.isEmpty()) {
            username.setError(getString(R.string.username_isEmpty));
            username.requestFocus();
            return false;
        }
        if (usernameInputted.length() < 4) {
            username.setError(getString(R.string.username_minimum_length));
            username.requestFocus();
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
