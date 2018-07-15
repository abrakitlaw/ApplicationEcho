package com.thesis.application.echo.post_module;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thesis.application.echo.R;

import java.util.HashMap;
import java.util.Map;

public class EditContent extends AppCompatActivity {

    FirebaseAuth mAuth;
    DatabaseReference contentRef;

    private EditText editTitle, editDescription, editSource;
    private Spinner editCategory;
    private String postKey, currentUserId;
    private Button btnEditContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_content);

        postKey = getIntent().getExtras().get("postKey").toString();

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        contentRef = FirebaseDatabase.getInstance().getReference().child("posts").child(postKey);

        editTitle = findViewById(R.id.editTextTitlePostEditContent);
        editDescription = findViewById(R.id.editTextShareYourStoryEditContent);
        editSource = findViewById(R.id.editTextSourceGalleryPostEditContent);
        editCategory = findViewById(R.id.spinnerCategoryPostEditContent);
        btnEditContent = findViewById(R.id.btnEditContentGallery);

        contentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String title = dataSnapshot.child("postTitle").getValue().toString();
                    editTitle.setText(title);
                    String description = dataSnapshot.child("description").getValue().toString();
                    editDescription.setText(description);
                    String source = dataSnapshot.child("postSource").getValue().toString();
                    editSource.setText(source);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnEditContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editContent();
            }
        });
    }

    private void editContent() {
        String title = editTitle.getText().toString();
        String description = editDescription.getText().toString();
        String source = editSource.getText().toString();
        String category = editCategory.getSelectedItem().toString();

        if(inputEditContentValidation(title, description, source, category)) {
            editTheDatabase(title, description, source, category);
        }
    }

    private void editTheDatabase(String title, String description, String source, String category) {
        Map<String, Object> editMap = new HashMap<>();
        editMap.put("postTitle", title);
        editMap.put("description", description);
        editMap.put("postSource", source);
        editMap.put("postCategory", category);

        contentRef.updateChildren(editMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    goToContentActivity();
                    Toast.makeText(getApplicationContext(), "Post has been updated", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void goToContentActivity() {
        Intent intent = new Intent(EditContent.this, ContentActivity.class);
        intent.putExtra("postKey", postKey);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private boolean inputEditContentValidation(String title, String description, String source, String category) {
        if(TextUtils.isEmpty(title)) {
            editTitle.setError(getString(R.string.title_required));
            editTitle.requestFocus();
            return false;
        } if(TextUtils.isEmpty(description)) {
            editDescription.setError(getString(R.string.description_is_empty));
            editDescription.requestFocus();
            return false;
        } if (TextUtils.isEmpty(source)) {
            editSource.setError(getString(R.string.source_is_empty));
            editSource.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(category)) {
            editCategory.requestFocus();
            Toast.makeText(getApplicationContext(), "Please select post category", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
