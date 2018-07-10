package com.thesis.application.echo.login_module;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.thesis.application.echo.R;
import com.thesis.application.echo.main_home_module.MainHome;

public class ResetPasswordConfirmation extends AppCompatActivity implements View.OnClickListener{

    EditText editTextEmailResetPassConfirm;
    EditText editTextPassResetPassConfirm;
    Button btnConfirm;
    ProgressBar progressBarResetPassConfirm;
    FirebaseAuth mAuth;
    DatabaseReference databaseReferenceUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password_confirmation);

        mAuth = FirebaseAuth.getInstance();

        editTextEmailResetPassConfirm = findViewById(R.id.editTextEmailResetPassConfim);
        editTextEmailResetPassConfirm.setEnabled(false);

        editTextPassResetPassConfirm = findViewById(R.id.editTextNewPassResetPassConfim);

        progressBarResetPassConfirm = findViewById(R.id.progressBarResetPassConfirm);
        btnConfirm = findViewById(R.id.btnConfirm);

        loadUserEmail();

        btnConfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnConfirm:
                String email = editTextEmailResetPassConfirm.getText().toString().trim();
                String newPassword = editTextPassResetPassConfirm.getText().toString().trim();

                if(TextUtils.isEmpty(newPassword)) {
                    editTextPassResetPassConfirm.setError(getString(R.string.password_isEmpty));
                    editTextPassResetPassConfirm.requestFocus();
                    return;
                } else {
                    updateUserPassword(email, newPassword);
                }


                break;
        }
    }

    public void loadUserEmail() {
        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        if (bd != null) {
            String getEmail = bd.getString(ResetPassword.EMAIL_USER);
            editTextEmailResetPassConfirm.setText(getEmail);
        }
    }


    public void updateUserPassword(final String email, final String password) {

        progressBarResetPassConfirm.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBarResetPassConfirm.setVisibility(View.GONE);
                if(task.isSuccessful()) {
                    try {
                        databaseReferenceUser = FirebaseDatabase.getInstance().getReference("users");
                        Query query = databaseReferenceUser.orderByChild("email").equalTo(email);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot child : dataSnapshot.getChildren()) {
                                    child.getRef().child("password").setValue(password);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        Toast.makeText(getApplicationContext(), getString(R.string.reset_pass_confirm_success), Toast.LENGTH_LONG).show();
                        finish();
                        Intent intent = new Intent(ResetPasswordConfirmation.this, MainHome.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } catch (NullPointerException e) {
                        e.getMessage();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.reset_pass_confirm_failed), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
