package com.example.skillocal_final;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JobVacancyActivity extends AppCompatActivity {

    private LinearLayout layoutJobs;
    private ArrayList<JobVacancy> jobs;
    private List<Establishment> establishments;
    private List<Industry> industry;
    private List<String> empType;
    private SharedPreferences sharedPreferences;
    private String currentUserEmail;

    private Integer currentId;

    private static final String PREFS_NAME = "SkillocalPrefs";

    ApiServiceJobVacancy api = ApiInstance.getApiJobVacancy();
    ApiService apiExt = ApiInstance.getApi();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentId = getSharedPreferences("MyRole", MODE_PRIVATE)
                .getInt("userId", 0);
        setContentView(R.layout.activity_job_vacancy);

        empType = new ArrayList<>(List.of("Full-Time", "Part-Time", "Contract", "Internship", "Others"));

        Toolbar toolbar = findViewById(R.id.toolbar_job);
        setSupportActionBar(toolbar);

        ImageView backIcon = findViewById(R.id.icon_back_job);
        backIcon.setOnClickListener(v -> finish());

        layoutJobs = findViewById(R.id.layout_jobs);
        FloatingActionButton fabAdd = findViewById(R.id.fab_add_job);

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        currentUserEmail = getSharedPreferences("UserSession", MODE_PRIVATE)
                .getString("email", "guest@user.com");

        jobs = new ArrayList<>();
        loadJobs();

        fabAdd.setOnClickListener(v -> showJobDialog(null));

        // Search
        EditText etSearch = findViewById(R.id.et_search_job);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                filterJobs(s.toString().toLowerCase());
            }
        });

        ImageView searchIcon = findViewById(R.id.search_icon_job);
        searchIcon.setOnClickListener(v -> filterJobs(etSearch.getText().toString().toLowerCase()));
    }

    private void loadJobs() {
        // Load Establishments
        apiExt.getEstablishmentByUserId("*", "eq." + currentId, "not.eq.Deleted", "establishment_id.asc")
                .enqueue(new Callback<List<Establishment>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<Establishment>> call, @NonNull Response<List<Establishment>> response) {
                        if (response.isSuccessful()) establishments = response.body();
                    }
                    @Override
                    public void onFailure(@NonNull Call<List<Establishment>> call, @NonNull Throwable t) {
                        Log.e("API", "Failed: " + t.getMessage());
                    }
                });

        // Load Industries
        apiExt.getAllIndustry("*").enqueue(new Callback<List<Industry>>() {
            @Override
            public void onResponse(@NonNull Call<List<Industry>> call, @NonNull Response<List<Industry>> response) {
                if (response.isSuccessful()) industry = response.body();
            }
            @Override
            public void onFailure(@NonNull Call<List<Industry>> call, @NonNull Throwable t) {
                Log.e("API", "Failed: " + t.getMessage());
            }
        });

        // Load Jobs
        api.getAllJobVacancyByUserId("*", "eq." + currentId, "vacancy_id.asc")
                .enqueue(new Callback<List<JobVacancy>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<JobVacancy>> call, @NonNull Response<List<JobVacancy>> response) {
                        if (response.isSuccessful()) {
                            List<JobVacancy> resList = response.body();
                            layoutJobs.removeAllViews();
                            jobs.clear();
                            assert resList != null;
                            for (JobVacancy e : resList) {
                                jobs.add(e);
                                addJobToLayout(e);
                            }
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<List<JobVacancy>> call, @NonNull Throwable t) {
                        Log.e("API", "Failed: " + t.getMessage());
                    }
                });
    }

    private void addJobToLayout(JobVacancy job) {
        View itemView = LayoutInflater.from(this).inflate(R.layout.item_job_vacancy, layoutJobs, false);
        TextView tvJobInfo = itemView.findViewById(R.id.tv_job_name);
        ImageView btnEdit = itemView.findViewById(R.id.btn_edit_job);
        ImageView btnDelete = itemView.findViewById(R.id.btn_delete_job);

        String estName = Objects.requireNonNull(findEstablishmentById(establishments, job.getEstablishment_id()))
                .getEstablishmentName();
        String info = job.getJob_title() + " at " + estName + " - " + job.getStatus()
                + "\nLocation: " + job.getLocation()
                + "\nEmployment Type: " + job.getEmployment_type()
                + "\nRemarks: " + (job.getRemarks() != null ? job.getRemarks() : "")
                + "\nCreated: " + job.getCreated_date()
                + " | Reviewed: " + job.getReviewed_date()
                + " by " + job.getReviewed_by();

        tvJobInfo.setText(info);

        btnEdit.setOnClickListener(v -> showJobDialog(job));
        btnDelete.setOnClickListener(v -> {
            deleteJobVacancy(job.getVacancy_id());
            layoutJobs.removeView(itemView);
            jobs.remove(job);
        });

        layoutJobs.addView(itemView);
    }

    private void showJobDialog(JobVacancy jobObj) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(jobObj == null ? "Add Job Vacancy" : "Edit Job Vacancy");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_job_new, null);

        EditText etJobTitle = view.findViewById(R.id.et_job_title);
        Spinner spnEstablishment = view.findViewById(R.id.spinner_establishment);
        Spinner spnIndustry = view.findViewById(R.id.spinner_industry);
        EditText etLocation = view.findViewById(R.id.et_location);
        Spinner spnEmpType = view.findViewById(R.id.spinner_employment_type);
        Spinner spnStatus = view.findViewById(R.id.spinner_status);
        EditText etRemarks = view.findViewById(R.id.et_remarks);
        EditText etCreatedDate = view.findViewById(R.id.et_created_date);
        EditText etReviewedDate = view.findViewById(R.id.et_reviewed_date);
        EditText etReviewedBy = view.findViewById(R.id.et_reviewed_by);

        // Populate spinners
        ArrayAdapter<String> empAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, empType);
        empAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnEmpType.setAdapter(empAdapter);

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                new String[]{"Active", "Closed"});
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnStatus.setAdapter(statusAdapter);

        ArrayAdapter<String> indAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                industry.stream().map(Industry::getIndustry_name).collect(Collectors.toList()));
        indAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnIndustry.setAdapter(indAdapter);

        ArrayAdapter<String> estAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                establishments.stream().map(Establishment::getEstablishmentName).collect(Collectors.toList()));
        estAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnEstablishment.setAdapter(estAdapter);

        if (jobObj != null) {
            etJobTitle.setText(jobObj.getJob_title());
            etLocation.setText(jobObj.getLocation());
            etRemarks.setText(jobObj.getRemarks());
            etCreatedDate.setText(jobObj.getCreated_date());
            etReviewedDate.setText(jobObj.getReviewed_date());
            etReviewedBy.setText(jobObj.getReviewed_by());
            spnStatus.setSelection(jobObj.getStatus().equalsIgnoreCase("Active") ? 0 : 1);
            spnEmpType.setSelection(empType.indexOf(jobObj.getEmployment_type()));
            spnIndustry.setSelection(findIndexInd(industry, jobObj.getIndustry_id()));
            spnEstablishment.setSelection(findIndexEst(establishments, jobObj.getEstablishment_id()));
        }

        etCreatedDate.setOnClickListener(v -> showDatePicker(etCreatedDate));
        etReviewedDate.setOnClickListener(v -> showDatePicker(etReviewedDate));

        builder.setView(view);

        builder.setPositiveButton(jobObj == null ? "Add" : "Save", (dialog, which) -> {
            String title = etJobTitle.getText().toString().trim();
            String loc = etLocation.getText().toString().trim();
            String remark = etRemarks.getText().toString().trim();
            String created = etCreatedDate.getText().toString().trim();
            String reviewed = etReviewedDate.getText().toString().trim();
            String reviewedBy = etReviewedBy.getText().toString().trim();
            String status = spnStatus.getSelectedItem().toString();
            String empTypeName = spnEmpType.getSelectedItem().toString();
            int indID = findIndIdByName(industry, spnIndustry.getSelectedItem().toString());
            int estID = findEstIdByName(establishments, spnEstablishment.getSelectedItem().toString());

            if (title.isEmpty() || loc.isEmpty() || created.isEmpty() || reviewed.isEmpty()) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            JobVacancy job = new JobVacancy(
                     estID, status, remark, created, reviewed, reviewedBy, title, indID, loc,
                    empTypeName, currentId);

            if (jobObj == null) saveJobs(job);
            else updateJobVacancy(job, jobObj.getVacancy_id());
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void showDatePicker(EditText editText) {
        final Calendar calendar = Calendar.getInstance();
        int y = calendar.get(Calendar.YEAR);
        int m = calendar.get(Calendar.MONTH);
        int d = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, (view, year, month, dayOfMonth) ->
                editText.setText((month + 1) + "/" + dayOfMonth + "/" + year), y, m, d).show();
    }

    private void filterJobs(String query) {
        layoutJobs.removeAllViews();
        for (JobVacancy job : jobs) {
            if (job.getJob_title().toLowerCase().contains(query)) addJobToLayout(job);
        }
    }

    private void saveJobs(JobVacancy job) {
        api.insertJobVacancy(job).enqueue(new Callback<JobVacancy>() {
            @Override
            public void onResponse(@NonNull Call<JobVacancy> call, @NonNull Response<JobVacancy> response) {
                if (response.isSuccessful()) loadJobs();
            }
            @Override
            public void onFailure(@NonNull Call<JobVacancy> call, @NonNull Throwable t) {
                Log.e("API", "Network error: " + t.getMessage());
                loadJobs();
            }
        });
    }

    private void updateJobVacancy(JobVacancy job, int id) {
        api.updateJobVacancy("eq." + id, job).enqueue(new Callback<JobVacancy>() {
            @Override
            public void onResponse(@NonNull Call<JobVacancy> call, @NonNull Response<JobVacancy> response) {
                if (response.isSuccessful()) loadJobs();
            }
            @Override
            public void onFailure(@NonNull Call<JobVacancy> call, @NonNull Throwable t) {
                Log.e("API", "Network error: " + t.getMessage());
            }
        });
    }

    private void deleteJobVacancy(int id) {
        api.deleteJobVacancy("eq." + id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                loadJobs();
            }
            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                loadJobs();
            }
        });
    }

    private Establishment findEstablishmentById(List<Establishment> list, Integer id) {
        for (Establishment e : list) if (Objects.equals(e.getEstablishment_id(), id)) return e;
        return null;
    }

    private int findIndexEst(List<Establishment> list, Integer id) {
        for (int i = 0; i < list.size(); i++) if (Objects.equals(list.get(i).getEstablishment_id(), id)) return i;
        return 0;
    }

    private int findEstIdByName(List<Establishment> list, String name) {
        for (Establishment e : list) if (Objects.equals(e.getEstablishmentName(), name)) return e.getEstablishment_id();
        return 0;
    }

    private int findIndexInd(List<Industry> list, Integer id) {
        for (int i = 0; i < list.size(); i++) if (Objects.equals(list.get(i).getIndustry_id(), id)) return i;
        return 0;
    }

    private int findIndIdByName(List<Industry> list, String name) {
        for (Industry e : list) if (Objects.equals(e.getIndustry_name(), name)) return e.getIndustry_id();
        return 0;
    }
}
