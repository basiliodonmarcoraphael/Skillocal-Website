package com.example.skillocal_final;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApplicantsListActivity extends AppCompatActivity {

    private LinearLayout layoutApplicants;
    private List<JobApplication> jobApplicants;
    private List<User> userList;

    private List<WorkExperience> workExpList;

    ApiService apiExt = ApiInstance.getApi();
    ApiServiceWorker apiWorker = ApiInstance.getApiWorker();

    public User findUserById(List<User> users, int userId) {
        if (users == null) return null;

        for (User u : users) {
            if (u.getUserId() != null && u.getUserId() == userId) {
                return u; // found the unique match
            }
        }
        return null; // not found
    }

    private String getWorkExpList(List<WorkExperience> wkList, int id){
        Log.d("Test", String.valueOf(id));
        if(wkList.isEmpty()){
            return "NO EXPERIENCED";
        }
        String workList = "";
        for(WorkExperience e: wkList){
            if(e.getUserId() == id){
                if(TextUtils.isEmpty(workList)){
                    workList = e.getPosition();
                }
                workList = workList+ ", "+ e.getPosition();
            }
        }
        if(TextUtils.isEmpty(workList)){
            return "NO EXPERIENCED";
        }
        return workList;
    }

    private void getAllWorkExperience(){
        // Load Jobs
        apiWorker.getAllWorkExperience("*")
                .enqueue(new Callback<List<WorkExperience>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<WorkExperience>> call, @NonNull Response<List<WorkExperience>> response) {
                        if (response.isSuccessful()) {
                            workExpList = response.body();
                            getAllUsers();
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<List<WorkExperience>> call, @NonNull Throwable t) {
                        Log.e("API", "Failed: " + t.getMessage());
                    }
                });
    }

    private void getAllUsers(){
        // Load Jobs
        apiExt.getAllUsers("*")
                .enqueue(new Callback<List<User>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<User>> call, @NonNull Response<List<User>> response) {
                        if (response.isSuccessful()) {
                            userList = response.body();

                            // Sample applicants data (replace with real API data)
                            List<Applicant> applicants = new ArrayList<>();
                            for(JobApplication e: jobApplicants){
                                String myEmail = findUserById(userList, e.getUser_id()).getEmail();
                                String expWork = getWorkExpList(workExpList, e.getUser_id());
                                applicants.add(new Applicant(e.getFirstName()+ " "+ e.getMiddleName()+ " "+ e.getLastName(), myEmail, "Resume: "+ expWork));
                            }

                            // Add applicants to layout
                            for (Applicant applicant : applicants) {
                                addApplicantCard(applicant);
                            }
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<List<User>> call, @NonNull Throwable t) {
                        Log.e("API", "Failed: " + t.getMessage());
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JobApplicationList wrapper =
                (JobApplicationList) getIntent().getSerializableExtra("applications_list");

        assert wrapper != null;
        jobApplicants = wrapper.getList();
        setContentView(R.layout.activity_applicants_list);

        layoutApplicants = findViewById(R.id.layout_applicants_list);

        // Back button functionality
        ImageView iconBack = findViewById(R.id.icon_back_applicants);
        iconBack.setOnClickListener(v -> finish());

        getAllWorkExperience();
    }

    private void addApplicantCard(Applicant applicant) {
        View card = LayoutInflater.from(this).inflate(R.layout.item_applicant_card, layoutApplicants, false);

        TextView tvName = card.findViewById(R.id.tv_applicant_name);
        TextView tvEmail = card.findViewById(R.id.tv_applicant_email);
        TextView tvResume = card.findViewById(R.id.tv_applicant_resume);
        LinearLayout detailsLayout = card.findViewById(R.id.layout_applicant_details);
        TextView btnViewDetails = card.findViewById(R.id.btn_view_details);

        tvName.setText(applicant.getName());
        tvEmail.setText("Email: " + applicant.getEmail());
        tvResume.setText(applicant.getResume());

        btnViewDetails.setOnClickListener(v -> {
            if (detailsLayout.getVisibility() == View.GONE) {
                detailsLayout.setVisibility(View.VISIBLE);
            } else {
                detailsLayout.setVisibility(View.GONE);
            }
        });

        layoutApplicants.addView(card);
    }

    // Sample Applicant class
    private static class Applicant {
        private final String name;
        private final String email;
        private final String resume;

        public Applicant(String name, String email, String resume) {
            this.name = name;
            this.email = email;
            this.resume = resume;
        }

        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getResume() { return resume; }
    }
}
