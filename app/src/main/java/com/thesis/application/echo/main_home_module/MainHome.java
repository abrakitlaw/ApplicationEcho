package com.thesis.application.echo.main_home_module;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thesis.application.echo.R;

import com.thesis.application.echo.login_module.Login;
import com.thesis.application.echo.login_module.SaveUserInformation;
import com.thesis.application.echo.profile_module.ProfileFragment;
import com.thesis.application.echo.post_module.Post;


public class MainHome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseAuth mAuth;
    DatabaseReference dbRef;
    FirebaseDatabase mFireBaseDatabase;
    FirebaseUser firebaseUser;

    NavigationView navigationView;

    private static final int gallery_pick = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mFireBaseDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        dbRef = mFireBaseDatabase.getReference().child("users");

        navigationView = findViewById(R.id.nav_view);
        //View navView = navigationView.inflateHeaderView(R.layout.nav_header_main_home);

        navigationView.setNavigationItemSelectedListener(this);
        checkUserExistence();

    }

    private void goToProfile() {
        Intent intent = new Intent(MainHome.this, ProfileFragment.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser curUser = mAuth.getCurrentUser();

        if(curUser == null) {
            goToLogin();
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
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
            Intent intent = new Intent(MainHome.this, Post.class);
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
        Context context = this.getApplicationContext();

        switch (itemId) {
            case R.id.nav_profile:
                fragment = new ProfileFragment();
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
            fragmentTransaction.replace(R.id.frameProfile, fragment);
            fragmentTransaction.commit();
        }

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void goToHome() {
        Intent intent = new Intent(getApplicationContext(), MainHome.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void chooseFromGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galleryIntent, "Select Profile Image"), gallery_pick);
    }
}
