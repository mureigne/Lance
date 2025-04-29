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

        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        textUsername.setText("Username: " + prefs.getString("username", ""));
        textName.setText("Name: " + prefs.getString("name", ""));
        textPhone.setText("Phone: " + prefs.getString("phone", ""));
        textEmail.setText("Email: " + prefs.getString("email", ""));

        btnSignOut.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();
            startActivity(new Intent(ProfileActivity.this, SignUpActivity.class));
            finish();
        });

        ImageView iconDashboard = findViewById(R.id.iconDashboard);
        ImageView iconProfile = findViewById(R.id.iconProfile);

        iconDashboard.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            finish();
        });

        iconProfile.setOnClickListener(v -> {
            // already in profile
        });
    }
}
