package com.example.lance;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class TasksActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        ImageView iconDashboard = findViewById(R.id.iconDashboard);
        ImageView iconPortfolio = findViewById(R.id.iconPortfolio);
        ImageView iconTask = findViewById(R.id.iconTask);
        ImageView iconProfile = findViewById(R.id.iconProfile);

        iconDashboard.setOnClickListener(v -> {
            startActivity(new Intent(TasksActivity.this, MainActivity.class));
            finish();
        });

        iconPortfolio.setOnClickListener(v -> {
            startActivity(new Intent(TasksActivity.this, PortfolioActivity.class));
            finish();
        });

        iconTask.setOnClickListener(v -> {
            // Already in TasksActivity
        });

        // Profile icon for navigating to ProfileActivity
        iconProfile.setOnClickListener(v -> {
            startActivity(new Intent(TasksActivity.this, ProfileActivity.class));
            finish();
        });
    }
}
