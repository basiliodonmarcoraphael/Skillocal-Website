package com.example.skillocal_final;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JobApplicationActivity extends AppCompatActivity {
    ApiServiceWorker api = ApiInstance.getApiWorker();
    private EditText etFullName, etBirthDate, etBirthPlace,
            etHeight, etWeight, etGraduateInstitution, etGraduateCourse,
            etTertiaryInstitution, etTertiaryCourse, etSecondaryInstitution,etPrimaryInstitution, etSex, etCivilStatus;
//    private Spinner spinnerSex, spinnerCivilStatus;
    private Button btnSubmit;
    private ImageView iconBack;

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "JobApplicationPrefs";
    private int currentId;

    int jobApplicationDetailsId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentId = getSharedPreferences("MyRole", MODE_PRIVATE)
                .getInt("userId", 0);
        setContentView(R.layout.activity_job_application);

        // Initialize fields
        etFullName = findViewById(R.id.et_full_name);
        etBirthDate = findViewById(R.id.et_birth_date);
        etBirthPlace = findViewById(R.id.et_birth_place);
        etSex = findViewById(R.id.et_sex);  // previously EditText, now Spinner
        etCivilStatus = findViewById(R.id.et_civil_status); // previously EditText, now Spinner
        etHeight = findViewById(R.id.et_height);
        etWeight = findViewById(R.id.et_weight);
        etGraduateInstitution = findViewById(R.id.et_graduate_institution);
        etGraduateCourse = findViewById(R.id.et_graduate_course);
        etTertiaryInstitution = findViewById(R.id.et_tertiary_institution);
        etTertiaryCourse = findViewById(R.id.et_tertiary_course);
        etSecondaryInstitution = findViewById(R.id.et_secondary_institution);
        etPrimaryInstitution = findViewById(R.id.et_primary_institution);
        btnSubmit = findViewById(R.id.btn_submit);
        iconBack = findViewById(R.id.icon_back_job_seeker);

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Back button
        iconBack.setOnClickListener(v -> finish());

        // Setup Spinners
//        setupSpinners();

        // Birth date picker
//        etBirthDate.setOnClickListener(v -> {
//            final Calendar calendar = Calendar.getInstance();
//            int year = calendar.get(Calendar.YEAR);
//            int month = calendar.get(Calendar.MONTH);
//            int day = calendar.get(Calendar.DAY_OF_MONTH);
//
//            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
//                    (view, selectedYear, selectedMonth, selectedDay) -> {
//                        String date = (selectedMonth + 1) + "/" + selectedDay + "/" + selectedYear;
//                        etBirthDate.setText(date);
//                    }, year, month, day);
//            datePickerDialog.show();
//        });

        // Submit button saves temporarily in SharedPreferences
        btnSubmit.setOnClickListener(v -> saveJobApplication());

        loadJobApplicationDetails(); //load job application details/job seeker info
        loadUser(); //load user details
    }

    private void setupSpinners() {
        // Sex options
//        String[] sexOptions = {"Male", "Female"};
//        ArrayAdapter<String> sexAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sexOptions);
//        sexAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerSex.setAdapter(sexAdapter);
//
//        // Civil Status options
//        String[] civilStatusOptions = {"Single", "Married", "Widowed", "Separated", "Divorced"};
//        ArrayAdapter<String> civilStatusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, civilStatusOptions);
//        civilStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerCivilStatus.setAdapter(civilStatusAdapter);
    }

    private void saveJobApplication() {
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString("full_name", etFullName.getText().toString());
//        editor.putString("birth_date", etBirthDate.getText().toString());
//        editor.putString("birth_place", etBirthPlace.getText().toString());
//        editor.putString("sex", etSex.getText().toString());
//        editor.putString("civil_status", etCivilStatus.getText().toString());
//        editor.putString("height", etHeight.getText().toString());
//        editor.putString("weight", etWeight.getText().toString());
//        editor.putString("graduate_institution", etGraduateInstitution.getText().toString());
//        editor.putString("graduate_course", etGraduateCourse.getText().toString());
//        editor.putString("tertiary_institution", etTertiaryInstitution.getText().toString());
//        editor.putString("tertiary_course", etTertiaryCourse.getText().toString());
//        editor.putString("secondary_institution", etSecondaryInstitution.getText().toString());
//        editor.putString("primary_institution", etPrimaryInstitution.getText().toString());
//        editor.apply();

//        Toast.makeText(this, "Job application saved temporarily!", Toast.LENGTH_SHORT).show();

        String fullName = etFullName.getText().toString().trim();
        String birthDate = etBirthDate.getText().toString().trim();
        String birthPlace = etBirthPlace.getText().toString().trim();
        String sex = etSex.getText().toString().trim();
        String civilStatus = etCivilStatus.getText().toString().trim();
        String height = etHeight.getText().toString().trim();
        String weight = etWeight.getText().toString().trim();
        String graduateInstitution = etGraduateInstitution.getText().toString().trim();
        String graduateCourse = etGraduateCourse.getText().toString().trim();
        String tertiaryInstitution = etTertiaryInstitution.getText().toString().trim();
        String tertiaryCourse = etTertiaryCourse.getText().toString().trim();
        String secondaryInstitution = etSecondaryInstitution.getText().toString().trim();
        String primaryInstitution = etPrimaryInstitution.getText().toString().trim();



        // SUCCESS — now allow closing
        JobApplicationDetails jobDetails = new JobApplicationDetails(
                fullName,
                civilStatus,
                sex,
                birthPlace,
                birthDate,
                graduateInstitution,
                graduateCourse,
                tertiaryInstitution,
                tertiaryCourse,
                secondaryInstitution,
                primaryInstitution,
                height,
                weight
        );
        Log.e("API", "Deets: " + birthPlace);
        updateJobApplicationDetails(jobDetails, jobApplicationDetailsId);
    }

    private void loadJobApplicationDetails() {
        //fetch details of user based on user_id from db
        api.getJobApplicationDetailsByUserId(
                "*",                     // select all columns
                "eq." + currentId         // Supabase filter
        ).enqueue(new Callback<List<JobApplicationDetails>>() {
            @Override
            public void onResponse(@NonNull Call<List<JobApplicationDetails>> call, @NonNull Response<List<JobApplicationDetails>> response) {
                if (response.isSuccessful()) {
                    List<JobApplicationDetails> resList = response.body();

                    assert resList != null;
                    for (JobApplicationDetails e : resList) {

                        if (e.getJobApplicationDetailsId() != null) {
                            jobApplicationDetailsId = e.getJobApplicationDetailsId();
                        }
                        if (e.getBirthplace() != null) {
                            etBirthPlace.setText(e.getBirthplace());
                        }

                        // transferred to loadUser
//                        if (e.getBirthDate() != null) {
//                            etBirthDate.setText(e.getBirthDate());
//                        }


                        if (e.getHeight() != null) {
                            etHeight.setText(e.getHeight());
                        }
                        if (e.getWeight() != null) {
                            etWeight.setText(e.getWeight());
                        }
                        if (e.getGraduateInstitution() != null) {
                            etGraduateInstitution.setText(e.getGraduateInstitution());
                        }
                        if (e.getGraduateCourse() != null) {
                            etGraduateCourse.setText(e.getGraduateCourse());
                        }
                        if (e.getTertiaryInstitution() != null) {
                            etTertiaryInstitution.setText(e.getTertiaryInstitution());
                        }
                        if (e.getTertiaryCourse() != null) {
                            etTertiaryCourse.setText(e.getTertiaryCourse());
                        }
                        if (e.getSecondaryInstitution() != null) {
                            etSecondaryInstitution.setText(e.getSecondaryInstitution());
                        }
                        if (e.getPrimaryInstitution() != null) {
                            etPrimaryInstitution.setText(e.getPrimaryInstitution());
                        }

                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<JobApplicationDetails>> call, @NonNull Throwable t) {
                Log.e("API", "Failed: " + t.getMessage());
            }
        });
    }

    private void loadUser() {
        //fetch details of user based on user_id from db
        api.getUserByUserId(
                "*",                     // select all columns
                "eq." + currentId         // Supabase filter
        ).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(@NonNull Call<List<User>> call, @NonNull Response<List<User>> response) {
                if (response.isSuccessful()) {
                    List<User> resList = response.body();

                    assert resList != null;
                    for (User e : resList) {

                        String firstName = "";
                        String middleName = "";
                        String lastName = "";
                        String suffix = "";
                        String full_name = "";
                        if (e.getFName() != null) {
                            firstName =e.getFName();
                        }
                        if (e.getMName() != null) {
                            middleName =e.getMName();
                        }
                        if (e.getLName() != null) {
                            lastName =e.getLName();
                        }
                        if (e.getSuffix() != null) {
                            suffix =e.getSuffix();
                        }

                        full_name = firstName + " " + middleName + " " + lastName + " " + suffix ;

                        etFullName.setText(full_name);

                        if (e.getBirthDate() != null) {
                            etBirthDate.setText(e.getBirthDate());
                        }
                        if (e.getSex() != null) {
                            etSex.setText(e.getSex());
                        }
                        if (e.getCivilStatus() != null) {
                            etCivilStatus.setText(e.getCivilStatus());
                        }

                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<User>> call, @NonNull Throwable t) {
                Log.e("API", "Failed: " + t.getMessage());
            }
        });
    }

    private void updateJobApplicationDetails(JobApplicationDetails jobApplicationDetails, Integer jobApplicationDetailsId){

        String idFilter = "eq." + jobApplicationDetailsId;
        Log.d("API", "ID: " + idFilter);
        api.updateJobApplicationDetailsByJobApplicationId(idFilter, jobApplicationDetails).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(JobApplicationActivity.this, "Job Seeker Information Updated!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(JobApplicationActivity.this, "Update failed: " + response.message(), Toast.LENGTH_SHORT).show();
                    Log.e("API", "Update failed: " + response.message());
                    Log.e("API", "Update failed: " + response);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(JobApplicationActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("API", "Error: ", t);
            }
        });
    }
}



