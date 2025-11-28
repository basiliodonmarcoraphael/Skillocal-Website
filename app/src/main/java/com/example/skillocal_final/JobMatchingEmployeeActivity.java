package com.example.skillocal_final;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class JobMatchingEmployeeActivity extends AppCompatActivity {

    private LinearLayout layoutJobs;
    private List<JobVacancy> jobList;

    // Static list to store applied jobs
    public static List<JobVacancy> appliedJobs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_matching_employee);

        layoutJobs = findViewById(R.id.layout_matching_employee);

        // Setup back button
        ImageView iconBack = findViewById(R.id.icon_back_matching_employee);
        iconBack.setOnClickListener(v -> onBackPressed());

        // Sample data (replace with API call)
        jobList = new ArrayList<>();
        jobList.add(new JobVacancy("Barista", "Coffee Shop", "General Santos City", "Full-Time", "Active", "None"));
        jobList.add(new JobVacancy("Cashier", "Retail Store", "General Santos City", "Part-Time", "Active", "No experience required"));

        // Add jobs to layout
        for (JobVacancy job : jobList) {
            addJobCard(job);
        }
    }

    private void addJobCard(JobVacancy job) {
        View card = LayoutInflater.from(this).inflate(R.layout.item_job_card, layoutJobs, false);

        LinearLayout root = card.findViewById(R.id.job_card_root);
        LinearLayout details = card.findViewById(R.id.layout_job_details);
        TextView tvJobInfo = card.findViewById(R.id.tv_job_info);
        Button btnApply = card.findViewById(R.id.btn_apply_job);

        // Set job info
        String info = "Location: " + job.getLocation() +
                "\nEmployment Type: " + job.getEmploymentType() +
                "\nStatus: " + job.getStatus() +
                "\nRemarks: " + job.getRemarks();
        tvJobInfo.setText(info);

        // Expand/Collapse details when clicking on card
        root.setOnClickListener(v -> {
            if (details.getVisibility() == View.GONE) {
                details.setVisibility(View.VISIBLE);
            } else {
                details.setVisibility(View.GONE);
            }
        });

        // Apply button
        btnApply.setOnClickListener(v -> showApplyConfirmation(job));

        layoutJobs.addView(card);
    }

    private void showApplyConfirmation(JobVacancy job) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Application")
                .setMessage("Are you sure you want to apply for \"" + job.getJobTitle() + "\"?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Add job to applied jobs list
                    if (!appliedJobs.contains(job)) {
                        appliedJobs.add(job);
                    }
                    Toast.makeText(this, "Applied to " + job.getJobTitle(), Toast.LENGTH_SHORT).show();

                    // Open Jobs Applied screen
                    startActivity(new Intent(this, JobsAppliedActivity.class));
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // Sample JobVacancy class
    public static class JobVacancy {
        private final String jobTitle;
        private final String establishment;
        private final String location;
        private final String employmentType;
        private final String status;
        private final String remarks;

        public JobVacancy(String jobTitle, String establishment, String location,
                          String employmentType, String status, String remarks) {
            this.jobTitle = jobTitle;
            this.establishment = establishment;
            this.location = location;
            this.employmentType = employmentType;
            this.status = status;
            this.remarks = remarks;
        }

        public String getJobTitle() { return jobTitle; }
        public String getEstablishment() { return establishment; }
        public String getLocation() { return location; }
        public String getEmploymentType() { return employmentType; }
        public String getStatus() { return status; }
        public String getRemarks() { return remarks; }
    }
}
