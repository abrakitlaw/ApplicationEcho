package com.thesis.application.echo.view;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.thesis.application.echo.R;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private EditText email;
    private EditText password;
    private ProgressBar progressBarLogin;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.editTextEmailLogin);
        onFocusChangeListener(email);

        password = findViewById(R.id.editTextPasswordLogin);
        onFocusChangeListener(password);

        progressBarLogin = findViewById(R.id.progressBarLogin);

        TextView signUp = findViewById(R.id.textViewSignUp);
        TextView forgotPass = findViewById(R.id.textViewForgotPass);
        Button signIn = findViewById(R.id.btnSignIn);

        signIn.setOnClickListener(this);
        signUp.setOnClickListener(this);
        forgotPass.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mAuth.getCurrentUser() != null) {
            finish();
            Intent intent = new Intent(this, MainHome.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textViewSignUp:
                startActivity(new Intent(this, RegisterUser.class));
                break;

            case R.id.btnSignIn:
                userLogin();
                break;

            case R.id.textViewForgotPass:
                startActivity(new Intent(this, ResetPassword.class));
                break;
        }
    }

    public void userLogin() {
        String emailInputted = email.getText().toString().trim();
        String passInputted = password.getText().toString().trim();

        if(TextUtils.isEmpty(emailInputted)) {
            email.setError(getString(R.string.email_isEmpty));
            email.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(emailInputted).matches()) {
            email.setError(getString(R.string.email_invalid));
        }
        if(TextUtils.isEmpty(passInputted)) {
            password.setError(getString(R.string.password_isEmpty));
            password.requestFocus();
            return;
        }

        progressBarLogin.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(emailInputted, passInputted).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBarLogin.setVisibility(View.GONE);
                if(task.isSuccessful()) {
                    finish();
                    Intent intent = new Intent(Login.this, MainHome.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.validation_failed), Toast.LENGTH_SHORT).show();
                }
            }
        });
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
