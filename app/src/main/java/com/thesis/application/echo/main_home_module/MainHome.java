package com.thesis.application.echo.main_home_module;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.thesis.application.echo.profile_module.Profile;
import com.thesis.application.echo.profile_module.ProfileFragment;
import com.thesis.application.echo.post_module.PostActivity;


public class MainHome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainHome.class.getName();

    FirebaseAuth mAuth;
    DatabaseReference dbRef, postRef;
    FirebaseDatabase mFireBaseDatabase;
    FirebaseUser firebaseUser;
    private RecyclerView recyclerViewPostList;


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
                viewHolder.setPostTitle(model.getPostTitle());
                viewHolder.setImageVideoUrl(model.getImageVideoUrl());
                viewHolder.setPostDate(model.getPostDate());
                viewHolder.setDescription(model.getDescription());
                viewHolder.setTotalPoints(model.getTotalPoints());

            }
        };
        recyclerViewPostList.setAdapter(fireBaseRecyclerAdapter);
    }

    public static class ViewHolderPost extends RecyclerView.ViewHolder {

        View mView;

        public ViewHolderPost(View itemView) {
            super(itemView);
            mView = itemView;
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
