package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private TextView textOngoing, textInProcess, textCompleted, textDraft;
    private int ongoingCount = 0, inProcessCount = 0, completedCount = 0, draftCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textOngoing = findViewById(R.id.textOngoingCount);
        textInProcess = findViewById(R.id.textInProcessCount);
        textCompleted = findViewById(R.id.textCompletedCount);
        textDraft = findViewById(R.id.textDraftCount);

        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Simulate adding a project to Ongoing
                ongoingCount++;
                textOngoing.setText(ongoingCount + " Tasks");
            }
        });
    }
}
