package com.example.skillocal_final;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JobsAppliedActivity extends AppCompatActivity {
    ApiServiceWorker api = ApiInstance.getApiWorker();
    private LinearLayout layoutAppliedJobs;
    private int currentId;

    public interface EstablishmentCallback {
        void onSuccess(Establishment establishment);
        void onNotFound();
        void onError(String error);
    }

    public interface JobVacancyCallback {
        void onSuccess(JobVacancy jobVacancy);
        void onNotFound();
        void onError(String error);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobs_applied);
        currentId = getSharedPreferences("MyRole", MODE_PRIVATE)
                .getInt("userId", 0 );
        layoutAppliedJobs = findViewById(R.id.layout_jobs_applied);

        // Setup toolbar back button
        ImageView iconBack = findViewById(R.id.icon_back_jobs_applied);
        iconBack.setOnClickListener(v -> finish());

        // Retrieve applied jobs from a static list in JobMatchingEmployeeActivity
//        List<JobMatchingEmployeeActivity.JobVacancy> appliedJobs = JobMatchingEmployeeActivity.appliedJobs;

//        for (JobMatchingEmployeeActivity.JobVacancy job : appliedJobs) {
//            addAppliedJobCard(job);
//        }
        loadJobApplications();
    }

//    private void addAppliedJobCard(JobMatchingEmployeeActivity. job) {
//        View card = LayoutInflater.from(this).inflate(R.layout.item_applied_job_card, layoutAppliedJobs, false);
//
//        TextView tvJobTitle = card.findViewById(R.id.tv_applied_job_title);
//        TextView tvJobDetails = card.findViewById(R.id.tv_applied_job_details);
//
//        tvJobTitle.setText(job.getJobTitle() + " at " + job.getEstablishment());
//        String details = "Location: " + job.getLocation() + "\nEmployment Type: " + job.getEmploymentType();
//        tvJobDetails.setText(details);
//
//        layoutAppliedJobs.addView(card);
//    }

    private void addJobApplicationToLayout(String jobTitle, String establishmentName, String location, String employment_type, String remarks, String vacancyStatus, String applicationStatus, String dateCreated) {
        View itemView = LayoutInflater.from(this).inflate(R.layout.item_job_vacancy, layoutAppliedJobs, false);
        TextView tvJobInfo = itemView.findViewById(R.id.tv_job_name);
        ImageView btnEdit = itemView.findViewById(R.id.btn_edit_job);
        ImageView btnDelete = itemView.findViewById(R.id.btn_delete_job);

        DateTimeFormatter inFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssxxx");
        DateTimeFormatter outFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        OffsetDateTime date = OffsetDateTime.parse(dateCreated, inFmt);
        String formattedDate = date.format(outFmt);

        String info = jobTitle + " at " + establishmentName + " - " + vacancyStatus
                + "\nLocation: " + location
                + "\nEmployment Type: " + employment_type
                + "\nRemarks: " + (remarks != null ? remarks : "N/A")
                + "\nPosted: " + formattedDate
                + "\nApplication Status: " + applicationStatus;


        tvJobInfo.setText(info);

//        btnEdit.setOnClickListener(v -> showJobDialog(job));
//        btnDelete.setOnClickListener(v -> {
//            deleteJobVacancy(job.getVacancy_id());
//            layoutJobs.removeView(itemView);
//            jobs.remove(job);
//        });

        layoutAppliedJobs.addView(itemView);
    }


    private void loadJobApplications() {
        api.getJobApplicationsById("*", "eq." + currentId, "application_id.asc")
                .enqueue(new Callback<List<JobApplicationInterfaceWorker>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<JobApplicationInterfaceWorker>> call, @NonNull Response<List<JobApplicationInterfaceWorker>> response) {
                        if (response.isSuccessful() && response.body() != null) {

                            for (JobApplicationInterfaceWorker e : response.body()) {


                                //get Job Vacancy Details
                                loadJobVacancyByVacancyId(e.getJobVacancyId(), new JobVacancyCallback() {
                                    @Override
                                    public void onSuccess(JobVacancy vacancy) {
                                        Log.e("API", "Job Vacancy Found: " + vacancy.getJob_title());

                                        //on success, search for establishment name next
                                        loadEstablishmentById(String.valueOf(vacancy.getEstablishment_id()), new EstablishmentCallback() {
                                            @Override
                                            public void onSuccess(Establishment estab) {
                                                Log.e("API", "Establishment Found: " + estab.getEstablishmentName());
                                                addJobApplicationToLayout(vacancy.getJob_title(), estab.getEstablishmentName(), vacancy.getLocation(), vacancy.getEmployment_type(), vacancy.getRemarks(), vacancy.getStatus(), e.getApplicationStatus(), vacancy.getCreated_date());

                                            }

                                            @Override
                                            public void onNotFound() {
                                                Log.e("API", "No establishment found for this ID.");
                                            }

                                            @Override
                                            public void onError(String error) {
                                                Log.e("API", "Error loading establishment: " + error);
                                            }
                                        });

                                    }

                                    @Override
                                    public void onNotFound() {
                                        Log.e("API", "No Job Vacancy found for this ID.");
                                    }

                                    @Override
                                    public void onError(String error) {
                                        Log.e("API", "Error loading job vacancy: " + error);
                                    }
                                });





//                                Log.e("API", "FOUND ESTAB: " + estabName);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<JobApplicationInterfaceWorker>> call, @NonNull Throwable t) {
                        Log.e("API", "Failed (Jobs): " + t.getMessage());
                    }
                });
    }





    private void loadEstablishmentById(String establishmentId, EstablishmentCallback callback) {

        api.getEstablishmentByEstablishmentId("*", "eq." + establishmentId)
                .enqueue(new Callback<List<Establishment>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<Establishment>> call,
                                           @NonNull Response<List<Establishment>> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            List<Establishment> result = response.body();

                            if (!result.isEmpty()) {
                                // Return the first object
                                callback.onSuccess(result.get(0));
                            } else {
                                callback.onNotFound();
                            }

                        } else {
                            callback.onError("Response failed");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<Establishment>> call, @NonNull Throwable t) {
                        callback.onError(t.getMessage());
                    }
                });
    }


    private void loadJobVacancyByVacancyId(Integer vacancyId, JobVacancyCallback callback) {

        api.getJobVacancyByVacancyId("*", "eq." + vacancyId)
                .enqueue(new Callback<List<JobVacancy>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<JobVacancy>> call,
                                           @NonNull Response<List<JobVacancy>> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            List<JobVacancy> result = response.body();

                            if (!result.isEmpty()) {
                                // Return the first object
                                callback.onSuccess(result.get(0));
                            } else {
                                callback.onNotFound();
                            }

                        } else {
                            callback.onError("Response failed");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<JobVacancy>> call, @NonNull Throwable t) {
                        callback.onError(t.getMessage());
                    }
                });
    }
}
