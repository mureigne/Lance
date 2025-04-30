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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ProjectActivity extends AppCompatActivity {

    private ArrayList<Project> projectList = new ArrayList<>();
    private ProjectAdapter adapter;

    ImageView iconDashboard, iconPortfolio, iconAdd, iconTask, iconProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        // Initialize bottom navigation icons
        iconDashboard = findViewById(R.id.iconDashboard);
        iconPortfolio = findViewById(R.id.iconPortfolio);
        iconAdd = findViewById(R.id.iconAdd);
        iconTask = findViewById(R.id.iconTask);
        iconProfile = findViewById(R.id.iconProfile);

        // Set up RecyclerView for displaying projects
        RecyclerView recyclerView = findViewById(R.id.recyclerProjects);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProjectAdapter(projectList);
        recyclerView.setAdapter(adapter);

        // Load stored project data from SharedPreferences
        loadProjectData();

        // Navigation behavior
        iconDashboard.setOnClickListener(v -> startActivity(new Intent(ProjectActivity.this, MainActivity.class)));
        iconPortfolio.setOnClickListener(v -> startActivity(new Intent(ProjectActivity.this, PortfolioActivity.class)));
        iconTask.setOnClickListener(v -> { /* already here */ });
        iconProfile.setOnClickListener(v -> startActivity(new Intent(ProjectActivity.this, ProfileActivity.class)));

        // Show "Add Project" popup when clicking on "Add" icon
        iconAdd.setOnClickListener(v -> showAddProjectPopup());
    }

    private void loadProjectData() {
        // Load project data from SharedPreferences
        SharedPreferences preferences = getSharedPreferences("ProjectData", MODE_PRIVATE);
        int projectCount = preferences.getInt("projectCount", 0);

        for (int i = 0; i < projectCount; i++) {
            String projectName = preferences.getString("projectName_" + i, null);
            String dueDate = preferences.getString("dueDate_" + i, null);
            String description = preferences.getString("description_" + i, null);

            if (projectName != null && dueDate != null && description != null) {
                projectList.add(new Project(projectName, dueDate, description));
            }
        }

        adapter.notifyDataSetChanged(); // Update the RecyclerView after loading the data
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

            DatePickerDialog datePickerDialog = new DatePickerDialog(ProjectActivity.this,
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
                    Toast.makeText(ProjectActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                } else {
                    // Save project data to SharedPreferences
                    SharedPreferences preferences = getSharedPreferences("ProjectData", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    int projectCount = preferences.getInt("projectCount", 0);
                    editor.putString("projectName_" + projectCount, title);
                    editor.putString("dueDate_" + projectCount, dueDate);
                    editor.putString("description_" + projectCount, description);
                    editor.putInt("projectCount", projectCount + 1); // Increment project count
                    editor.apply();

                    // Add the new project to the list and update the RecyclerView
                    projectList.add(new Project(title, dueDate, description));
                    adapter.notifyItemInserted(projectList.size() - 1);

                    dialog.dismiss();
                }
            });
        });

        dialog.show();
    }
}
