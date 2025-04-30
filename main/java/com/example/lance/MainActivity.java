package com.example.lance;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    ImageView iconDashboard, iconPortfolio, iconAdd, iconTask, iconProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize bottom navigation icons
        iconDashboard = findViewById(R.id.iconDashboard);
        iconPortfolio = findViewById(R.id.iconPortfolio);
        iconAdd = findViewById(R.id.iconAdd);
        iconTask = findViewById(R.id.iconTask);
        iconProfile = findViewById(R.id.iconProfile);

        // Navigate to current (Main) Activity
        iconDashboard.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Already on Dashboard", Toast.LENGTH_SHORT).show();
        });

        iconPortfolio.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, PortfolioActivity.class));
        });

        iconTask.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ProjectActivity.class));
        });

        iconProfile.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        });

        iconAdd.setOnClickListener(v -> showAddProjectPopup());
    }

    private void showAddProjectPopup() {
        // Inflate the popup layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View popupView = inflater.inflate(R.layout.popup_add_project, null);

        // Reference input fields
        EditText inputTitle = popupView.findViewById(R.id.inputProjectName);
        EditText inputDueDate = popupView.findViewById(R.id.inputDueDate);
        EditText inputDescription = popupView.findViewById(R.id.inputDescription);

        inputDueDate.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String selectedDate = selectedYear + "-" + String.format("%02d", selectedMonth + 1) + "-" + String.format("%02d", selectedDay);
                        inputDueDate.setText(selectedDate);
                    }, year, month, day);

            datePickerDialog.show();
        });

        // Build AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(popupView)
                .setTitle("Add New Project")
                .setPositiveButton("Submit", null)  // We override later to control dismiss
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();

        // Override Submit button behavior
        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String title = inputTitle.getText().toString().trim();
                String dueDate = inputDueDate.getText().toString().trim();
                String description = inputDescription.getText().toString().trim();

                if (title.isEmpty() || dueDate.isEmpty() || description.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                } else {
                    // Save project data to SharedPreferences
                    SharedPreferences preferences = getSharedPreferences("ProjectData", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("projectName", title);
                    editor.putString("dueDate", dueDate);
                    editor.putString("description", description);
                    editor.apply();

                    // Pass data to ProjectActivity
                    Intent intent = new Intent(MainActivity.this, ProjectActivity.class);
                    startActivity(intent);
                    dialog.dismiss();
                }
            });
        });

        dialog.show();
    }
}
