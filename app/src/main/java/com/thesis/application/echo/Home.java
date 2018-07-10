package com.thesis.application.echo;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.thesis.application.echo.R;

public class Home extends AppCompatActivity {

    FirebaseAuth mAuth;
    DatabaseReference dbRef;
    FirebaseDatabase mFireBaseDatabase;
    FirebaseUser firebaseUser;
    FirebaseAuth.AuthStateListener mAuthListener;

    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mFireBaseDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        dbRef = mFireBaseDatabase.getReference();
        firebaseUser = mAuth.getCurrentUser();
        uid = firebaseUser.getUid();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if(firebaseUser != null) {
                    Toast.makeText(getApplicationContext(), "sign in: " + firebaseUser.getEmail(), Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "no user", Toast.LENGTH_LONG).show();
                }
            }
        };
    }
}
