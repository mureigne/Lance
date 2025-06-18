package com.example.lance;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    TextView textUsername, textName, textPhone, textEmail;
    Button btnSignOut;
    MyDataBaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        dbHelper = new MyDataBaseHelper(this);

        textUsername = findViewById(R.id.textUsername);
        textName = findViewById(R.id.textName);
        textPhone = findViewById(R.id.textPhone);
        textEmail = findViewById(R.id.textEmail);
        btnSignOut = findViewById(R.id.btnSignOut);

        // Get current username from in-memory session
        String currentUsername = CurrentUser.getUsername();
        if (currentUsername == null) {
            // No active session — go to login
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
            return;
        }

        // Load user data from SQLite
        Cursor cursor = dbHelper.getUserByUsername(currentUsername);
        if (cursor != null && cursor.moveToFirst()) {
            textUsername.setText("Username: " + cursor.getString(cursor.getColumnIndexOrThrow("username")));
            textName.setText("Name: " + cursor.getString(cursor.getColumnIndexOrThrow("name")));
            textPhone.setText("Phone: " + cursor.getString(cursor.getColumnIndexOrThrow("phone")));
            textEmail.setText("Email: " + cursor.getString(cursor.getColumnIndexOrThrow("email")));
        } else {
            Toast.makeText(this, "User not found!", Toast.LENGTH_SHORT).show();
        }

        if (cursor != null) {
            cursor.close();
        }

        // Sign out button clears session and navigates back to login
        btnSignOut.setOnClickListener(v -> {
            CurrentUser.clear();  // Clear global session
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
        });

        // Bottom navigation
        findViewById(R.id.iconDashboard).setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            finish();
        });

        findViewById(R.id.iconPortfolio).setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, PortfolioActivity.class));
            finish();
        });

        findViewById(R.id.iconTask).setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, ProjectActivity.class));
            finish();
        });
    }
}