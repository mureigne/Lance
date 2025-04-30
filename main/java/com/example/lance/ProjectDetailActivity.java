package com.example.lance;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

public class ProjectDetailActivity extends AppCompatActivity {

    private TextView projectTitle;
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter; // You'll create this similar to ProjectAdapter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_detail);

        projectTitle = findViewById(R.id.projectDetailTitle);
        recyclerView = findViewById(R.id.recyclerViewTasks);

        String title = getIntent().getStringExtra("title");
        if (title != null) {
            projectTitle.setText(title);
            loadProjectTasks(title);
        } else {
            Toast.makeText(this, "No project data", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadProjectTasks(String projectTitle) {
        // Dummy data – Replace with actual filtering by project in real implementation
        List<String> taskList = Arrays.asList("UI Design", "API Integration", "Testing", "Deployment");

        taskAdapter = new TaskAdapter(taskList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(taskAdapter);
    }
}
