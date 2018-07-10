package com.thesis.application.echo.login_module;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.thesis.application.echo.R;

public class ResetPassword extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextEmail;
    private FirebaseAuth mAuth;
    private ProgressBar progressBarResetPass;

    public static final String EMAIL_USER = "email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.editTextEmailResetPass);
        Button btnSubmit = findViewById(R.id.btnSubmit);
        progressBarResetPass = findViewById(R.id.progressBarResetPassword);

        btnSubmit.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSubmit:
                resetPassword();
                break;
        }
    }

    public void resetPassword() {
        final String email = editTextEmail.getText().toString().trim();

        if(TextUtils.isEmpty(email)) {
            editTextEmail.setError(getString(R.string.email_isEmpty));
            editTextEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError(getString(R.string.email_invalid));
            editTextEmail.requestFocus();
            return;
        }

        progressBarResetPass.setVisibility(View.VISIBLE);

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBarResetPass.setVisibility(View.GONE);
                if(task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.reset_pass_success), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ResetPassword.this, ResetPasswordConfirmation.class);
                    intent.putExtra(EMAIL_USER, email);

                    startActivity(intent);

                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.reset_pass_failed), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
