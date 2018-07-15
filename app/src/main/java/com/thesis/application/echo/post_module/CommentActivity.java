package com.thesis.application.echo.post_module;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thesis.application.echo.R;
import com.thesis.application.echo.models.Comment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CommentActivity extends AppCompatActivity {

    private RecyclerView commentList;
    private ImageButton postCommentButton;
    private EditText commentInputText;
    private ProgressBar progressBarComment;

    private DatabaseReference userRef, postRef;
    private FirebaseAuth mAuth;

    private String postKey;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        postKey = getIntent().getExtras().get("postKey").toString();

        commentList = findViewById(R.id.recyclerViewComment);
        commentList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        commentList.setLayoutManager(linearLayoutManager);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("users");
        postRef = FirebaseDatabase.getInstance().getReference().child("posts").child(postKey).child("comments");

        progressBarComment = findViewById(R.id.progressBarComment);
        commentInputText = findViewById(R.id.editTextCommentPost);
        postCommentButton = findViewById(R.id.btnPostComment);

        postCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String username = dataSnapshot.child("username").getValue().toString();
                            validateComment(username);
                            commentInputText.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Comment, CommentsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Comment, CommentsViewHolder>(
                Comment.class, R.layout.all_comments_layout, CommentsViewHolder.class, postRef
        ) {
            @Override
            protected void populateViewHolder(CommentsViewHolder viewHolder, Comment model, int position) {
                viewHolder.setUsername(model.getUsername());
                viewHolder.setComment(model.getComment());
                viewHolder.setDate(model.getDate());
                viewHolder.setTime(model.getTime());
            }
        };
        commentList.setAdapter(firebaseRecyclerAdapter);

    }

    public static class CommentsViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public CommentsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setUsername(String username) {
            TextView myUsername = mView.findViewById(R.id.textViewUsernameComment);
            myUsername.setText("@" + username);
        }

        public void setComment(String comment) {
            TextView myComment = mView.findViewById(R.id.textViewCommentText);
            myComment.setText(comment);
        }

        public void setDate(String date) {
            TextView myDate = mView.findViewById(R.id.textViewDatePostComment);
            myDate.setText(date);
        }
        public void setTime(String time){
            TextView myTime = mView.findViewById(R.id.textViewTimePostComment);
            myTime.setText(time);
        }
    }

    private void validateComment(String username) {
        String commentText = commentInputText.getText().toString();

        if(TextUtils.isEmpty(commentText)) {
            Toast.makeText(this, "Please write text to comment", Toast.LENGTH_SHORT).show();
        } else {
            Calendar calendarDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd MMMM yyyy");
            final String saveCurrentDate = currentDate.format(calendarDate.getTime());

            Calendar calendarTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
            final String saveCurrentTime = currentTime.format(calendarTime.getTime());

            String dateAndTime = saveCurrentDate + saveCurrentTime;
            final String randomKey = currentUserId + saveCurrentDate + saveCurrentTime;

            Map<String, Object> commentMap = new HashMap<>();
            commentMap.put("username", username);
            commentMap.put("userId", currentUserId);
            commentMap.put("date", saveCurrentDate);
            commentMap.put("time", saveCurrentTime);
            commentMap.put("comment", commentText);
            commentMap.put("commentId", randomKey);

            progressBarComment.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            postRef.child(randomKey).updateChildren(commentMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    progressBarComment.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    if(task.isSuccessful()) {
                        Toast.makeText(CommentActivity.this, "Comment successfully posted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CommentActivity.this, "Error occurred: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
