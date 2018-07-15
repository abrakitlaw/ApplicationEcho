package com.thesis.application.echo.post_module;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.thesis.application.echo.R;
import com.thesis.application.echo.main_home_module.MainHome;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContentActivity extends AppCompatActivity {

    private DatabaseReference contentRef, userRef;
    private FirebaseAuth mAuth;

    private TextView txtViewPostTitle, txtViewCategory, txtViewDate,
            txtViewSource, txtViewDescription, txtViewTotalPoints, txtViewUsername;
    private ImageButton thumbUp, thumbDown;
    private ImageView postImage;
    private CircleImageView profilePict;
    private Button btnDelete, btnEdit;

    private String postKey, currentUserId, userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        postKey = getIntent().getExtras().get("postKey").toString();

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        userRef = FirebaseDatabase.getInstance().getReference().child("users");
        contentRef = FirebaseDatabase.getInstance().getReference().child("posts").child(postKey);

        txtViewUsername = findViewById(R.id.textViewUsernameContent);
        profilePict = findViewById(R.id.profileImageContent);
        txtViewPostTitle = findViewById(R.id.titlePostContent);
        txtViewCategory = findViewById(R.id.textViewCategoryContent);
        txtViewDate = findViewById(R.id.textViewDatePostContent);
        txtViewSource = findViewById(R.id.textViewSourcePostContent);
        txtViewDescription = findViewById(R.id.textViewPostDescriptionContent);
        txtViewTotalPoints = findViewById(R.id.textViewTotalPointsContent);
        postImage = findViewById(R.id.imageViewPostedContent);
        thumbUp = findViewById(R.id.imageButtonThumbUpContent);
        thumbDown = findViewById(R.id.imageButtonThumbDownContent);
        btnDelete = findViewById(R.id.btnDeleteContent);
        btnEdit = findViewById(R.id.btnEditContent);

        btnDelete.setVisibility(View.INVISIBLE);
        btnEdit.setVisibility(View.INVISIBLE);

        contentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               if(dataSnapshot.exists()) {
                   userId = dataSnapshot.child("userId").getValue().toString();
                   final String title = dataSnapshot.child("postTitle").getValue().toString();
                   txtViewPostTitle.setText(title);
                   final String category = dataSnapshot.child("postCategory").getValue().toString();
                   txtViewCategory.setText(category);
                   String date = dataSnapshot.child("postDate").getValue().toString();
                   txtViewDate.setText(date);
                   final String source = dataSnapshot.child("postSource").getValue().toString();
                   txtViewSource.setText(source);
                   final String description = dataSnapshot.child("description").getValue().toString();
                   txtViewDescription.setText(description);
                   String totalPoints = dataSnapshot.child("totalPoints").getValue().toString();
                   txtViewTotalPoints.setText(totalPoints);
                   String imageUrl = dataSnapshot.child("imageVideoUrl").getValue().toString();
                   Picasso.get().load(imageUrl).into(postImage);

                   if(currentUserId.equals(userId)) {
                       btnEdit.setVisibility(View.VISIBLE);
                       btnDelete.setVisibility(View.VISIBLE);
                   }

                   btnEdit.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           Intent intent = new Intent(ContentActivity.this, EditContent.class);
                           intent.putExtra("postKey", postKey);
                           startActivity(intent);
                       }
                   });
               }

                userRef.child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String username = dataSnapshot.child("username").getValue().toString();
                        txtViewUsername.setText(username);
                        String downloadUrl = dataSnapshot.child("profilePictUrl").getValue().toString();
                        Picasso.get().load(downloadUrl).into(profilePict);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePost();
            }
        });
    }

    private void deletePost() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ContentActivity.this);
        builder.setTitle("Are you sure want to delete this post");

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                contentRef.removeValue();
                goToHome();
                Toast.makeText(getApplicationContext(), "Post has been deleted", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        Dialog dialog = builder.create();
        dialog.show();
    }

    private void goToHome() {
        Intent intent = new Intent(ContentActivity.this, MainHome.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainHome.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
