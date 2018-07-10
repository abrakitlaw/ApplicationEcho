package com.thesis.application.echo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.thesis.application.echo.login_module.Login;
import com.thesis.application.echo.main_home_module.MainHome;
import com.thesis.application.echo.login_module.RegisterUser;

public class WelcomePage extends AppCompatActivity implements View.OnClickListener{

    private Button btnSignUp, btnLogIn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);

        mAuth = FirebaseAuth.getInstance();

        btnSignUp = findViewById(R.id.btnSignUp);
        btnLogIn = findViewById(R.id.btnLogin);

        btnSignUp.setOnClickListener(this);
        btnLogIn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSignUp:
                startActivity(new Intent(this, RegisterUser.class));
                break;

            case R.id.btnLogin:
                startActivity(new Intent(this, Login.class));
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mAuth.getCurrentUser() != null) {
            finish();
            Intent intent = new Intent(this, MainHome.class);
            startActivity(intent);
        }
    }
}
