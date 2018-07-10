package com.thesis.application.echo.profile_module;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thesis.application.echo.R;
import com.thesis.application.echo.profile_module.Profile;

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

    TextView txtUsername, txtEmail, txtFullName, txtGender;

    private String currentUserId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        currentUserId = mAuth.getCurrentUser().getUid();

        dbRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserId);

        txtUsername = view.findViewById(R.id.textViewUsernameProfile);
        txtFullName = view.findViewById(R.id.textViewFullnameProfile);
        txtGender = view.findViewById(R.id.textViewGenderProfile);
        txtEmail = view.findViewById(R.id.textViewEmailProfile);

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    String username = dataSnapshot.child("username").getValue().toString();
                    String fullname = dataSnapshot.child("fullname").getValue().toString();
                    String gender = dataSnapshot.child("gender").getValue().toString();
                    String email = mAuth.getCurrentUser().getEmail().trim();

                    txtUsername.setText(username);
                    txtFullName.setText(fullname);
                    txtGender.setText(gender);
                    txtEmail.setText(email);
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
