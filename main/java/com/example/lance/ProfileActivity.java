package com.example.lance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    TextView textUsername, textName, textPhone, textEmail;
    Button btnSignOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        textUsername = findViewById(R.id.textUsername);
        textName = findViewById(R.id.textName);
        textPhone = findViewById(R.id.textPhone);
        textEmail = findViewById(R.id.textEmail);
        btnSignOut = findViewById(R.id.btnSignOut);

        // Retrieve user data from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        String username = prefs.getString("username", "");
        String name = prefs.getString("name", "");
        String phone = prefs.getString("phone", "");
        String email = prefs.getString("email", "");

        // Display user data on the profile screen
        textUsername.setText("Username: " + username);
        textName.setText("Name: " + name);
        textPhone.setText("Phone: " + phone);
        textEmail.setText("Email: " + email);

        // Sign out button functionality
        btnSignOut.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();  // Clear user data when signing out
            editor.apply();
            startActivity(new Intent(ProfileActivity.this, SignUpActivity.class));
            finish();
        });

        // Icon functionality for navigation
        ImageView iconDashboard = findViewById(R.id.iconDashboard);
        ImageView iconPortfolio = findViewById(R.id.iconPortfolio);
        ImageView iconTask = findViewById(R.id.iconTask);
        ImageView iconProfile = findViewById(R.id.iconProfile);

        iconDashboard.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            finish();
        });

        iconPortfolio.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, PortfolioActivity.class));
            finish();
        });

        iconTask.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, TasksActivity.class));
            finish();
        });

        // Profile icon is already in ProfileActivity, so no action needed.
        iconProfile.setOnClickListener(v -> {
            // Already in Profile Activity
        });
    }
}
