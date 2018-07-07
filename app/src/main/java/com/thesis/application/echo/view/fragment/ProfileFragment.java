package com.thesis.application.echo.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thesis.application.echo.R;
import com.thesis.application.echo.models.User;
import com.thesis.application.echo.view.Profile;

/**
 * Created by Abra Kitlaw on 05-Jul-18.
 */

public class ProfileFragment extends android.support.v4.app.Fragment {

    public static final String USER_ID = "userId";

    View view;

    FirebaseAuth mAuth;
    DatabaseReference dbRef;
    FirebaseDatabase mFireBaseDatabase;
    FirebaseUser firebaseUser;

    String email;
    TextView txtUsername, txtEmail;

    User user;
    private String userId;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        mFireBaseDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        dbRef = mFireBaseDatabase.getReference();
        firebaseUser = mAuth.getCurrentUser();

        txtUsername = view.findViewById(R.id.textViewUsernameProfile);
        txtEmail = view.findViewById(R.id.textViewEmailProfile);
        email = firebaseUser.getEmail();

        mFireBaseDatabase.getReference("users").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    if(postSnapshot.exists()) {
                        user = postSnapshot.getValue(User.class);
                        String email = user.getEmail();
                        String password = user.getPassword();
                        userId = user.getUserId();
                        String username = user.getUsername();

                        Toast.makeText(getActivity().getApplicationContext(), email, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent();
                        intent.putExtra(USER_ID, userId);
                        startActivity(intent);

                        txtUsername.setText(username);
                        txtEmail.setText(email);

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Button btnUpdate = view.findViewById(R.id.btnEditProfile);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity().getApplicationContext(), Profile.class);
                getActivity().startActivity(intent);
            }
        });

        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Profile");

    }
}
