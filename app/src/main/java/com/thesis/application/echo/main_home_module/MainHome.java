package com.thesis.application.echo.main_home_module;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.thesis.application.echo.R;

import com.thesis.application.echo.login_module.Login;
import com.thesis.application.echo.login_module.SaveUserInformation;
import com.thesis.application.echo.models.Post;
import com.thesis.application.echo.post_module.CommentActivity;
import com.thesis.application.echo.post_module.ContentActivity;
import com.thesis.application.echo.profile_module.Profile;
import com.thesis.application.echo.post_module.PostActivity;

public class MainHome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainHome.class.getName();

    FirebaseAuth mAuth;
    DatabaseReference dbRef, postRef, likeRef, dislikeRef, flagRef;
    FirebaseDatabase mFireBaseDatabase;
    FirebaseUser firebaseUser;
    private RecyclerView recyclerViewPostList;

    Boolean flagChecker;
    Boolean likeChecker;
    Boolean dislikeChecker;
    String currentUserId;
    NavigationView navigationView;
    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mFireBaseDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        currentUserId = mAuth.getCurrentUser().getUid();

        dbRef = mFireBaseDatabase.getReference().child("users");
        postRef = mFireBaseDatabase.getReference().child("posts");
        likeRef = mFireBaseDatabase.getReference().child("likes");
        dislikeRef = mFireBaseDatabase.getReference().child("dislikes");
        flagRef = mFireBaseDatabase.getReference().child("reports");

        //View navView = navigationView.inflateHeaderView(R.layout.nav_header_main_home);

        recyclerViewPostList = findViewById(R.id.recyclerViewMainHome);
        recyclerViewPostList.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerViewPostList.setLayoutManager(layoutManager);

        displayAllUsersPost();

    }

    private void displayAllUsersPost() {
        FirebaseRecyclerAdapter<Post, ViewHolderPost> fireBaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Post, ViewHolderPost>(Post.class, R.layout.all_posts, ViewHolderPost.class, postRef) {
            @Override
            protected void populateViewHolder(ViewHolderPost viewHolder, Post model, int position) {

                final String postKey = getRef(position).getKey();

                viewHolder.setPostTitle(model.getPostTitle());
                viewHolder.setImageVideoUrl(model.getImageVideoUrl());
                viewHolder.setPostDate(model.getPostDate());
                viewHolder.setDescription(model.getDescription());
                viewHolder.setTotalPoints(model.getTotalPoints());

                viewHolder.setLikeButtonStatus(postKey);
                viewHolder.setDislikeButtonStatus(postKey);
                viewHolder.setFlagStatus(postKey);


                viewHolder.btnFlagReport.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        flagChecker = true;

                        flagRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (flagChecker.equals(true)) {
                                    if (dataSnapshot.child(postKey).hasChild(currentUserId)) {
                                        flagRef.child(postKey).child(currentUserId).removeValue();
                                        flagChecker = false;
                                    } else {
                                        flagRef.child(postKey).child(currentUserId).setValue(true);
                                        flagChecker = false;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                });

                viewHolder.textViewPostTitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainHome.this, ContentActivity.class);
                        intent.putExtra("postKey", postKey);
                        startActivity(intent);
                    }
                });

                viewHolder.btnComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainHome.this, CommentActivity.class);
                        intent.putExtra("postKey", postKey);
                        startActivity(intent);
                    }
                });

                viewHolder.btnThumbUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        likeChecker = true;
                        dislikeChecker = true;

                        likeRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (likeChecker.equals(true)) {
                                    if (dataSnapshot.child(postKey).hasChild(currentUserId)) {
                                        likeRef.child(postKey).child(currentUserId).removeValue();
                                        likeChecker = false;
                                    } else {
                                        likeRef.child(postKey).child(currentUserId).setValue(true);

                                        dislikeRef.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dislikeChecker.equals(true)) {
                                                    if (dataSnapshot.child(postKey).hasChild(currentUserId)) {
                                                        dislikeRef.child(postKey).child(currentUserId).removeValue();
                                                        dislikeChecker = false;
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                        likeChecker = false;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });

                viewHolder.btnThumbDown.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dislikeChecker = true;
                        likeChecker = true;

                        dislikeRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dislikeChecker.equals(true)) {
                                    if (dataSnapshot.child(postKey).hasChild(currentUserId)) {
                                        dislikeRef.child(postKey).child(currentUserId).removeValue();
                                        dislikeChecker = false;
                                    } else {
                                        dislikeRef.child(postKey).child(currentUserId).setValue(true);

                                        likeRef.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (likeChecker.equals(true)) {
                                                    if (dataSnapshot.child(postKey).hasChild(currentUserId)) {
                                                        likeRef.child(postKey).child(currentUserId).removeValue();
                                                        likeChecker = false;
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                        dislikeChecker = false;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });
            }
        };
        recyclerViewPostList.setAdapter(fireBaseRecyclerAdapter);
    }

    public static class ViewHolderPost extends RecyclerView.ViewHolder {

        View mView;
        ImageButton btnThumbUp, btnThumbDown, btnComment, btnFlagReport;
        TextView textViewTotalPoints, textViewPostTitle, viewTotalLikes, viewTotalDislikes;
        int countLikes, countDislikes, countReports;
        String currentUserId;
        DatabaseReference likeRef, dislikeRef, flagRef;

        public ViewHolderPost(View itemView) {
            super(itemView);
            mView = itemView;

            btnFlagReport = mView.findViewById(R.id.imageButtonFlagReport);
            viewTotalLikes = mView.findViewById(R.id.textViewTotalLikes);
            viewTotalDislikes = mView.findViewById(R.id.textViewTotalDislikes);
            textViewPostTitle = mView.findViewById(R.id.titleOfPost);
            textViewTotalPoints = mView.findViewById(R.id.textViewTotalPoints);
            btnThumbDown = mView.findViewById(R.id.imageButtonThumbDown);
            btnThumbUp = mView.findViewById(R.id.imageButtonThumbUp);
            btnComment = mView.findViewById(R.id.imageButtonComment);

            flagRef = FirebaseDatabase.getInstance().getReference().child("reports");
            likeRef = FirebaseDatabase.getInstance().getReference().child("likes");
            dislikeRef = FirebaseDatabase.getInstance().getReference().child("dislikes");
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        public void setPostTitle(String postTitle) {
            TextView txtViewPostTitle = itemView.findViewById(R.id.titleOfPost);
            txtViewPostTitle.setText(postTitle);
        }

        public void setImageVideoUrl(String imageVideoUrl) {
            Uri imageUri = Uri.parse(imageVideoUrl);
            ImageView imageViewImageVideoUrl = itemView.findViewById(R.id.imageViewPosted);
            Picasso.get().load(imageUri).into(imageViewImageVideoUrl);
        }

        public void setPostDate(String postDate) {
            TextView txtViewPostDate = itemView.findViewById(R.id.textViewDatePost);
            txtViewPostDate.setText(postDate);
        }

        public void setDescription(String description) {
            TextView txtViewPostDescription = itemView.findViewById(R.id.textViewPostDescription);
            txtViewPostDescription.setText(description);
        }

        public void setTotalPoints(int totalPoints) {
            String ttlPts = String.valueOf(totalPoints);
            TextView txtViewTotalPoints = itemView.findViewById(R.id.textViewTotalPoints);
            txtViewTotalPoints.setText(ttlPts);
        }

        public void setFlagStatus(final String postKey) {
            flagRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(postKey).hasChild(currentUserId)) {
                        countReports = (int) dataSnapshot.child(postKey).getChildrenCount();
                        btnFlagReport.setImageResource(R.drawable.ic_flag_black_24dp);
                    } else {
                        countReports = (int) dataSnapshot.child(postKey).getChildrenCount();
                        btnFlagReport.setImageResource(R.drawable.ic_flag_white_24dp);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public void setLikeButtonStatus(final String postKey) {
            likeRef.addValueEventListener(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(postKey).hasChild(currentUserId)) {
                        countLikes = (int) dataSnapshot.child(postKey).getChildrenCount();
                        btnThumbUp.setImageResource(R.drawable.ic_thumb_up_green_24dp);
                        btnThumbDown.setImageResource(R.drawable.ic_thumb_down_white_24dp);
                        viewTotalLikes.setText(Integer.toString(countLikes));

                    } else {
                        countLikes = (int) dataSnapshot.child(postKey).getChildrenCount();
                        btnThumbUp.setImageResource(R.drawable.ic_thumb_up_white_24dp);
                        viewTotalLikes.setText(Integer.toString(countLikes));

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        @SuppressLint("SetTextI18n")
        public void setDislikeButtonStatus(final String postKey) {
            dislikeRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(postKey).hasChild(currentUserId)) {
                        countDislikes = (int) dataSnapshot.child(postKey).getChildrenCount();
                        btnThumbDown.setImageResource(R.drawable.ic_thumb_down_red_24dp);
                        btnThumbUp.setImageResource(R.drawable.ic_thumb_up_white_24dp);
                        viewTotalDislikes.setText(Integer.toString(countDislikes));

                    } else {
                        countDislikes = (int) dataSnapshot.child(postKey).getChildrenCount();
                        btnThumbDown.setImageResource(R.drawable.ic_thumb_down_white_24dp);
                        viewTotalDislikes.setText(Integer.toString(countDislikes));

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void goToProfile() {
        Intent intent = new Intent(MainHome.this, Profile.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser curUser = mAuth.getCurrentUser();

        if(curUser == null) {
            goToLogin();
        }
        if(curUser != null) {
            checkUserExistence();
            displayAllUsersPost();
        }
    }

    private void checkUserExistence() {
        final String currentUserId = mAuth.getCurrentUser().getUid();
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(currentUserId)) {
                    goToSaveUserInformation();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void goToSaveUserInformation() {
        Intent intent = new Intent(MainHome.this, SaveUserInformation.class);
        startActivity(intent);
        finish();
    }

    private void goToLogin() {
        Intent intent = new Intent(MainHome.this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_home, menu);
        getMenuInflater().inflate(R.menu.main_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sign_out) {
            mAuth.signOut();
            Intent intent = new Intent(MainHome.this, Login.class);
            startActivity(intent);
            finish();
            return true;
        }
        if (id == R.id.btnPost) {
            Intent intent = new Intent(MainHome.this, PostActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        displaySelectedScreen(id);

        return true;
    }

    private void displaySelectedScreen(int itemId) {
        android.support.v4.app.Fragment fragment = null;

        switch (itemId) {
            case R.id.nav_profile:
                goToProfile();
                break;
            case R.id.nav_home:
                goToHome();
                break;
            case R.id.nav_trending:
                fragment = new TrendingFragment();
                break;
            case R.id.nav_hoax:
                break;
        }

        if(fragment != null) {
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, fragment);
            fragmentTransaction.commit();
        }

        drawer.closeDrawer(GravityCompat.START);
    }

    private void goToHome() {
        Intent intent = new Intent(getApplicationContext(), MainHome.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
