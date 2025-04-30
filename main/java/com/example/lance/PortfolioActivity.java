package com.example.lance;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class PortfolioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio);

        ImageView iconDashboard = findViewById(R.id.iconDashboard);
        ImageView iconPortfolio = findViewById(R.id.iconPortfolio);
        ImageView iconTask = findViewById(R.id.iconTask);
        ImageView iconProfile = findViewById(R.id.iconProfile);

        iconDashboard.setOnClickListener(v -> {
            startActivity(new Intent(PortfolioActivity.this, MainActivity.class));
            finish();
        });

        iconPortfolio.setOnClickListener(v -> {
            // Already in PortfolioActivity
        });

        iconTask.setOnClickListener(v -> {
            startActivity(new Intent(PortfolioActivity.this, ProjectActivity.class));
            finish();
        });

        // Profile icon for navigating to ProfileActivity
        iconProfile.setOnClickListener(v -> {
            startActivity(new Intent(PortfolioActivity.this, ProfileActivity.class));
            finish();
        });
    }
}
