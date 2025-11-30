package com.example.skillocal_final;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JobMatchingEmployeeActivity extends AppCompatActivity {
    ApiServiceWorker api = ApiInstance.getApiWorker();
    private LinearLayout layoutJobs;
//    private List<JobVacancy> jobList;

    private int currentId;

    String firstName;
    String middleName;
    String lastName;
    String suffix;

    List<Establishment> establistmentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_matching_employee);
        currentId = getSharedPreferences("MyRole", MODE_PRIVATE)
                .getInt("userId", 0 );

        layoutJobs = findViewById(R.id.layout_matching_employee);

        // Setup back button
        ImageView iconBack = findViewById(R.id.icon_back_matching_employee);
        iconBack.setOnClickListener(v -> finish());

        // Sample data (replace with API call)
//        jobList = new ArrayList<>();
//        jobList.add(new JobVacancy("Barista", "Coffee Shop", "General Santos City", "Full-Time", "Active", "None"));
//        jobList.add(new JobVacancy("Cashier", "Retail Store", "General Santos City", "Part-Time", "Active", "No experience required"));
//
//        // Add jobs to layout
//        for (JobVacancy job : jobList) {
//            addJobCard(job);
//        }
        loadEstablishments(() ->
                loadUser(this::loadJobs)
        );

    }

    private void addJobCard(JobVacancy job, String establishmentName) {
        View card = LayoutInflater.from(this).inflate(R.layout.item_job_card, layoutJobs, false);

        TextView tvJobTitle = card.findViewById(R.id.tv_job_card_title);
        LinearLayout root = card.findViewById(R.id.job_card_root);
        LinearLayout details = card.findViewById(R.id.layout_job_details);
        TextView tvJobInfo = card.findViewById(R.id.tv_job_info);
        Button btnApply = card.findViewById(R.id.btn_apply_job);

        // Set Job Title and Establishment
//        Log.e("API", "Card: " + job.getJob_title() + establishmentName);
        String jobTitle = job.getJob_title() + " at " + establishmentName;
        // Set job info
        String info = "Location: " + job.getLocation() +
                "\nEmployment Type: " + job.getEmployment_type() +
                "\nStatus: " + job.getStatus() +
                "\nRemarks: " + job.getRemarks();
        tvJobInfo.setText(info);
        tvJobTitle.setText(jobTitle);

        // Expand/Collapse details when clicking on card
        root.setOnClickListener(v -> {
            if (details.getVisibility() == View.GONE) {
                details.setVisibility(View.VISIBLE);
            } else {
                details.setVisibility(View.GONE);
            }
        });

        // Apply button
        btnApply.setOnClickListener(v -> showApplyConfirmation(job, establishmentName));

        layoutJobs.addView(card);
    }

    private void showApplyConfirmation(JobVacancy job, String establishmentName) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Application")
                .setMessage("Are you sure you want to apply for " + job.getJob_title() + " at " + establishmentName + "?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Add job to applied jobs list
//                    if (!appliedJobs.contains(job)) {
//                        appliedJobs.add(job);
//                    }

                    applyForJob(job.getVacancy_id(), currentId, firstName, middleName, lastName, suffix);

//                    Toast.makeText(this, "Applied to " + job.getJob_title(), Toast.LENGTH_SHORT).show();

                    // Open Jobs Applied screen
//                    startActivity(new Intent(this, JobsAppliedActivity.class));
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // Sample JobVacancy class
//    public static class JobVacancy {
//        private final String jobTitle;
//        private final String establishment;
//        private final String location;
//        private final String employmentType;
//        private final String status;
//        private final String remarks;
//
//        public JobVacancy(String jobTitle, String establishment, String location,
//                          String employmentType, String status, String remarks) {
//            this.jobTitle = jobTitle;
//            this.establishment = establishment;
//            this.location = location;
//            this.employmentType = employmentType;
//            this.status = status;
//            this.remarks = remarks;
//        }
//
//        public String getJobTitle() { return jobTitle; }
//        public String getEstablishment() { return establishment; }
//        public String getLocation() { return location; }
//        public String getEmploymentType() { return employmentType; }
//        public String getStatus() { return status; }
//        public String getRemarks() { return remarks; }
//    }


    private void loadEstablishments(Runnable onComplete) {
        api.getEstablishmentByEstablishment("*")
                .enqueue(new Callback<List<Establishment>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<Establishment>> call, @NonNull Response<List<Establishment>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            establistmentList.clear(); // avoid duplicates on reload

                            for (Establishment establishmentDetails : response.body()) {
                                establistmentList.add(establishmentDetails);
//                                Log.e("API", "GOT: " + establishmentDetails.getEstablishmentName());
                            }

                            onComplete.run(); // 👉 move to loadUser()
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<Establishment>> call, @NonNull Throwable t) {
                        Log.e("API", "Failed (Est): " + t.getMessage());
                    }
                });
    }


    private void loadUser(Runnable onComplete) {
        api.getUserByUserId("*", "eq." + currentId)
                .enqueue(new Callback<List<User>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<User>> call, @NonNull Response<List<User>> response) {
                        if (response.isSuccessful() && response.body() != null) {

                            for (User e : response.body()) {
                                if (e.getFName() != null) firstName = e.getFName();
                                if (e.getMName() != null) middleName = e.getMName();
                                if (e.getLName() != null) lastName = e.getLName();
                                if (e.getSuffix() != null) suffix = e.getSuffix();
                            }

                            onComplete.run(); // 👉 move to loadJobs()
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<User>> call, @NonNull Throwable t) {
                        Log.e("API", "Failed (User): " + t.getMessage());
                    }
                });
    }

    private void loadJobs() {
        api.getAllJobVacancy("*", "not.eq.Deleted", "vacancy_id.asc")
                .enqueue(new Callback<List<JobVacancy>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<JobVacancy>> call, @NonNull Response<List<JobVacancy>> response) {
                        if (response.isSuccessful() && response.body() != null) {

                            layoutJobs.removeAllViews();

                            for (JobVacancy e : response.body()) {
                                Establishment foundEstablishment = establistmentList.stream()
                                        .filter(u -> Objects.equals(u.getEstablishment_id(), e.getEstablishment_id()))
                                        .findFirst()
                                        .orElse(null);

                                String estabName = (foundEstablishment != null)
                                        ? foundEstablishment.getEstablishmentName()
                                        : "Unknown Establishment";

                                addJobCard(e, estabName);

//                                Log.e("API", "FOUND ESTAB: " + estabName);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<JobVacancy>> call, @NonNull Throwable t) {
                        Log.e("API", "Failed (Jobs): " + t.getMessage());
                    }
                });
    }








    //confirming job application
    private void applyForJob(Integer vacancyId, Integer userId, String firstName, String middleName, String lastName, String suffix) {
        // Check first for duplicate applications
        Log.e("API", "SELECTED JOB: " + userId + "/" + vacancyId);
        api.getJobApplicationDuplicates("*",
                        "eq." + userId,
                        "eq." + vacancyId)
                .enqueue(new Callback<List<JobApplicationInterfaceWorker>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<JobApplicationInterfaceWorker>> call,
                                           @NonNull Response<List<JobApplicationInterfaceWorker>> response) {

                        if (response.isSuccessful()) {

                                List<JobApplicationInterfaceWorker> resList = response.body();

//                                assert resList != null;
                            if (resList == null || resList.isEmpty()) {
                                Log.e("API", "PWDE MAG APPLY");
                                JobApplicationInterfaceWorker application = new JobApplicationInterfaceWorker(
                                        userId, vacancyId, firstName, middleName, lastName, suffix
                                );
                                insertApplication(application);
                            } else {
                                //already applied for this job
                                for (JobApplicationInterfaceWorker e : resList) {
                                    Log.e("API", "Already Applied. Found: " + e.getFirstName() + e.getLastName());
                                }
                                Toast.makeText(JobMatchingEmployeeActivity.this,
                                        "You already applied for this.", Toast.LENGTH_SHORT).show();
                            }




//                            List<JobApplicationInterfaceWorker> resList = response.body();
//
//                            if (resList == null || resList.isEmpty()) {
//                                // No duplicates found → proceed with insert
//                                Log.e("API", "NO DUPLICATE FOUND. Proceeding with insert.");
//
//                                JobApplicationInterfaceWorker application = new JobApplicationInterfaceWorker(
//                                        userId, vacancyId, firstName, middleName, lastName, suffix
//                                );
//                                insertApplication(application);
//
//                            } else {
//                                // Duplicate found → DO NOT INSERT
//                                Log.e("API", "ALREADY APPLIED");
//                                Toast.makeText(JobMatchingEmployeeActivity.this,
//                                        "You already applied for this.", Toast.LENGTH_SHORT).show();
//                            }

                        } else {
                            Log.e("API", "Response not successful: " + response.errorBody());

                        }

                    }

                    @Override
                    public void onFailure(@NonNull Call<List<JobApplicationInterfaceWorker>> call,
                                          @NonNull Throwable t) {
                        Log.e("API", "Failed: " + t.getMessage());
                    }
                });
    }

    private void insertApplication(JobApplicationInterfaceWorker application) {

        api.insertJobApplication(application)
                .enqueue(new Callback<List<JobApplicationInterfaceWorker>>() {

                    @Override
                    public void onResponse(Call<List<JobApplicationInterfaceWorker>> call,
                                           Response<List<JobApplicationInterfaceWorker>> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            JobApplicationInterfaceWorker created = response.body().get(0);

                            Toast.makeText(JobMatchingEmployeeActivity.this,
                                    "Job Application Successfully Sent!",
                                    Toast.LENGTH_SHORT).show();
                        } else {

                            Log.e("API", "Insert failed: " + response.code());
                            Log.e("API", "Error body: " + response.errorBody());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<JobApplicationInterfaceWorker>> call, Throwable t) {
                        Log.e("API", "Network error: " + t.getMessage());
                    }
                });
    }
}
