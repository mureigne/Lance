package com.example.lance;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    ImageView iconDashboard, iconProfile, iconPortfolio, iconTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iconDashboard = findViewById(R.id.iconDashboard);
        iconProfile = findViewById(R.id.iconProfile);
        iconPortfolio = findViewById(R.id.iconPortfolio);
        iconTask = findViewById(R.id.iconTask);

        iconProfile.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        });

        iconPortfolio.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, PortfolioActivity.class));
        });
        iconTask.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, TasksActivity.class));
        });
    }
}
