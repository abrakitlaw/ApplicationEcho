package com.thesis.application.echo.post_module;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.thesis.application.echo.R;
import com.thesis.application.echo.main_home_module.MainHome;
import com.thesis.application.echo.models.*;
import com.thesis.application.echo.models.Post;

import org.w3c.dom.Text;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Abra Kitlaw on 10-Jul-18.
 */

public class GalleryPost extends Fragment{

    StorageReference storageReference;
    FirebaseAuth mAuth;
    DatabaseReference userRef, postRef, pushPostRef;

    View view;
    Uri imageUri;

    ProgressBar progressBarPost;
    Spinner spinnerPostCategory;
    ImageButton selectYourImage;
    EditText editTextShareYourStory, editTextTitle, editTextSource;
    Button btnPostGallery;

    private String saveCurrentDate, saveCurrentTime, randomName, downloadUrl, currentUserId, postId;

    private static final int GALLERY_PICK = 1001;
    private String description;
    private String title;
    private String source;
    private String category;
    private String postDate;
    private int totalPoints;
    private int totalReport;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_gallery, container, false);

        userRef = FirebaseDatabase.getInstance().getReference().child("users");
        postRef = FirebaseDatabase.getInstance().getReference().child("posts");
        //pushPostRef = postRef.push();
        //postId = pushPostRef.getKey();

        storageReference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        currentUserId = mAuth.getCurrentUser().getUid();

        progressBarPost = view.findViewById(R.id.progressBarPostByGallery);
        selectYourImage = view.findViewById(R.id.btnImageSelect);
        spinnerPostCategory = view.findViewById(R.id.spinnerCategoryPost);
        editTextTitle = view.findViewById(R.id.editTextTitlePost);
        editTextSource = view.findViewById(R.id.editTextSourceGalleryPost);
        editTextShareYourStory = view.findViewById(R.id.editTextShareYourStory);
        btnPostGallery = view.findViewById(R.id.btnPostGallery);
        
        selectYourImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               openGallery();
            }
        });

        btnPostGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePostFromGallery();
            }
        });
        
        return view;
    }

    private void savePostFromGallery() {
        title = editTextTitle.getText().toString();
        description = editTextShareYourStory.getText().toString();
        source = editTextSource.getText().toString();
        category = spinnerPostCategory.getSelectedItem().toString();

        if(validationPostInfo(imageUri, title, description, source, category)) {
            storingPostByGalleryToFirebaseStorage();
        }
    }

    private void storingPostByGalleryToFirebaseStorage() {
        Calendar calendarDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calendarDate.getTime());

        Calendar calendarTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(calendarTime.getTime());

        postDate = saveCurrentDate + "/" + saveCurrentTime;

        totalPoints = 0;
        totalReport = 0;
        postId = currentUserId + randomName;
        randomName = saveCurrentDate + saveCurrentTime;

        progressBarPost.setVisibility(View.VISIBLE);

        StorageReference filePath = storageReference.child("PostActivity").child(imageUri.getLastPathSegment() + randomName + ".jpg");

        filePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                progressBarPost.setVisibility(View.GONE);
                if(task.isSuccessful()) {
                    downloadUrl = task.getResult().getDownloadUrl().toString();
                    postId = currentUserId + randomName;

                    savingPostInformationToDatabase(postId, currentUserId, title, description, category, source, downloadUrl, postDate, totalPoints, totalReport);

                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Error occurred: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void goToHome() {
        Intent intent = new Intent(getActivity().getApplicationContext(), MainHome.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }

    private void savingPostInformationToDatabase(final String postId, final String userId, final String title, final String description, final String category, final String source, final String downloadUrl, final String postDate, final int totalPoints, final int totalReports) {

        Map<String, Object> postMap = new HashMap<>();
        postMap.put("postId", postId);
        postMap.put("userId", userId);
        postMap.put("description", description);
        postMap.put("imageVideoUrl", downloadUrl);
        postMap.put("postCategory", category);
        postMap.put("postDate", postDate);
        postMap.put("postSource", source);
        postMap.put("postTitle", title);
        postMap.put("totalPoints", totalPoints);
        postMap.put("totalReports", totalReports);

        postRef.child(postId).updateChildren(postMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(getActivity().getApplicationContext(), "Successfully uploaded the post..", Toast.LENGTH_SHORT).show();
                    goToHome();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Error occurred: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean validationPostInfo(Uri imageUri, String title, String description, String source, String category) {

        if(imageUri == null) {
            Toast.makeText(getActivity().getApplicationContext(), "Please select your image", Toast.LENGTH_LONG).show();
            return false;
        } else if(TextUtils.isEmpty(title)) {
            editTextTitle.setError(getString(R.string.title_required));
            editTextTitle.requestFocus();
            return false;
        } else if(title.length() > 140) {
            editTextTitle.setError(getString(R.string.title_maximum_chars));
            editTextTitle.requestFocus();
            return false;
        } else if(TextUtils.isEmpty(description)) {
            editTextShareYourStory.setError(getString(R.string.description_is_empty));
            editTextShareYourStory.requestFocus();
            return false;
        } else if (description.length() < 6) {
            editTextShareYourStory.setError(getString(R.string.description_minimum_chars));
            editTextShareYourStory.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(source)) {
            editTextSource.setError(getString(R.string.source_is_empty));
            editTextSource.requestFocus();
            return false;
        } else if(source.length() < 10) {
            editTextSource.setError(getString(R.string.source_minimum_chars));
            editTextSource.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(category)) {
            Toast.makeText(getActivity().getApplicationContext(), "Please select the category", Toast.LENGTH_LONG).show();
        }
        return true;
    }

    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galleryIntent, "Select an Image"), GALLERY_PICK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            selectYourImage.setImageURI(imageUri);

        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_post, menu);
    }
}
