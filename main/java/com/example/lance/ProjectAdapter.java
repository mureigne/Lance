package com.example.lance;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder> {

    private ArrayList<Project> projectList;

    public ProjectAdapter(ArrayList<Project> projectList) {
        this.projectList = projectList;
    }

    @Override
    public ProjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the layout for individual project items
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_project, parent, false);
        return new ProjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProjectViewHolder holder, int position) {
        // Get the project data for this position
        Project project = projectList.get(position);

        // Bind the data to the respective views
        holder.projectTitle.setText(project.getTitle());
        holder.projectDueDate.setText(project.getDueDate());
        holder.projectDescription.setText(project.getDescription());
    }

    @Override
    public int getItemCount() {
        // Return the size of the project list
        return projectList.size();
    }

    public static class ProjectViewHolder extends RecyclerView.ViewHolder {

        // Declare the TextViews for project details
        TextView projectTitle, projectDueDate, projectDescription;

        public ProjectViewHolder(View itemView) {
            super(itemView);
            // Initialize the TextViews based on the item_project layout
            projectTitle = itemView.findViewById(R.id.projectTitle);
            projectDueDate = itemView.findViewById(R.id.projectDueDate);
            projectDescription = itemView.findViewById(R.id.projectDescription);
        }
    }
}
