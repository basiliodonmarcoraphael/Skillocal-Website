package com.example.skillocal_final;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ApplicantsListActivity extends AppCompatActivity {

    private LinearLayout layoutApplicants;
    private List<JobApplication> jobApplicants;

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

        // Sample applicants data (replace with real API data)
        List<Applicant> applicants = new ArrayList<>();
        for(JobApplication e: jobApplicants){
            applicants.add(new Applicant(e.getFirstName()+ " "+ e.getMiddleName()+ " "+ e.getLastName(), e.getFirstName()+"@example.com", "Resume: Experienced Barista"));
        }

        // Add applicants to layout
        for (Applicant applicant : applicants) {
            addApplicantCard(applicant);
        }
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
