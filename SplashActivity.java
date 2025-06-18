package com.example.lance;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT = 2000; // 2 seconds
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();

        new Handler().postDelayed(() -> {
            FirebaseUser currentUser = mAuth.getCurrentUser();

            if (currentUser != null) {
                // ✅ User is logged in → Go to Dashboard
                startActivity(new Intent(SplashActivity.this, DashboardActivity.class));
            } else {
                // 🚪 User is not logged in → Go to Login or Sign Up
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }

            finish(); // close splash so it can't be returned to
        }, SPLASH_TIME_OUT);
    }
}
