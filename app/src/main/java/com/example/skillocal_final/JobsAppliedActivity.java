package com.example.skillocal_final;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class JobsAppliedActivity extends AppCompatActivity {

    private LinearLayout layoutAppliedJobs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobs_applied);

        layoutAppliedJobs = findViewById(R.id.layout_jobs_applied);

        // Setup toolbar back button
        ImageView iconBack = findViewById(R.id.icon_back_jobs_applied);
        iconBack.setOnClickListener(v -> onBackPressed());

        // Retrieve applied jobs from a static list in JobMatchingEmployeeActivity
        List<JobMatchingEmployeeActivity.JobVacancy> appliedJobs = JobMatchingEmployeeActivity.appliedJobs;

        for (JobMatchingEmployeeActivity.JobVacancy job : appliedJobs) {
            addAppliedJobCard(job);
        }
    }

    private void addAppliedJobCard(JobMatchingEmployeeActivity.JobVacancy job) {
        View card = LayoutInflater.from(this).inflate(R.layout.item_applied_job_card, layoutAppliedJobs, false);

        TextView tvJobTitle = card.findViewById(R.id.tv_applied_job_title);
        TextView tvJobDetails = card.findViewById(R.id.tv_applied_job_details);

        tvJobTitle.setText(job.getJobTitle() + " at " + job.getEstablishment());
        String details = "Location: " + job.getLocation() + "\nEmployment Type: " + job.getEmploymentType();
        tvJobDetails.setText(details);

        layoutAppliedJobs.addView(card);
    }
}
