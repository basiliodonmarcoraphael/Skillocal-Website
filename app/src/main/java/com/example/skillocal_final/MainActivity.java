package com.example.skillocal_final;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    ApiService api = ApiInstance.getApi();
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView iconMenu, iconKebab;
    private TextView toolbarTitle, tvEstCount, tvJobCount;

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "SkillocalPrefs";
    private static final String KEY_ESTABLISHMENTS = "establishments_per_user";
    private static final String KEY_JOBS = "jobs_per_user";

    int establishmentCount;
    int vacancyCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String role = getSharedPreferences("MyRole", MODE_PRIVATE)
                .getString("role", "Worker"); // default employee
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        iconMenu = findViewById(R.id.icon_menu);
        iconKebab = findViewById(R.id.icon_kebab);
        toolbarTitle = findViewById(R.id.toolbar_title);
        tvEstCount = findViewById(R.id.tv_est_count);
        tvJobCount = findViewById(R.id.tv_job_count);

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        navigationView.setNavigationItemSelectedListener(this);

        // Open drawer when menu icon clicked
        iconMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        Menu menu = navigationView.getMenu();
        MenuItem estItem = menu.findItem(R.id.nav_establishments);
        MenuItem empProf = menu.findItem(R.id.nav_employee_profile);
        MenuItem jobVacant = menu.findItem(R.id.nav_job_vacancy);
        MenuItem jVacMatch = menu.findItem(R.id.nav_job_matching_employee);
        MenuItem carPortfolio = menu.findItem(R.id.nav_career_portfolio);
        MenuItem manageEmpProf = menu.findItem(R.id.nav_manage_employee_profile);
        MenuItem jApplication = menu.findItem(R.id.nav_job_application);
        MenuItem jMatch = menu.findItem(R.id.nav_job_matching);
        MenuItem jobsApplied = menu.findItem(R.id.nav_jobs_applied); // NEW
        MenuItem applicantList = menu.findItem(R.id.nav_applicants_list); // NEW

        //Item to Hide based on role
        if(role.equals("Employer")){
            carPortfolio.setVisible(false);
            manageEmpProf.setVisible(false);
            jApplication.setVisible(false);
            jVacMatch.setVisible(false);
            jobsApplied.setVisible(false); // hide new item for employers
        }else{
            jMatch.setVisible(false);
            estItem.setVisible(false);
            empProf.setVisible(false);
            jobVacant.setVisible(false);
            applicantList.setVisible(false);
        }

        // Kebab menu logout
        iconKebab.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(MainActivity.this, v);
            popup.getMenuInflater().inflate(R.menu.kebab_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_logout) {
                    SharedPreferences prefs = getSharedPreferences("UserAccount", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("isLoggedIn", false);
                    editor.apply();

                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    return true;
                }
                return false;
            });
            popup.show();
        });


        loadEstablishments(new CountCallback() {
            @Override
            public void onSuccess(int count) {
                Log.e("API", "Final count = " + count);
                // NOW YOU CAN USE THE VALUE HERE
                establishmentCount = count;

                loadJobVacancies(new CountCallback() {
                    @Override
                    public void onSuccess(int count) {
                        Log.e("API", "Final count = " + count);
                        // NOW YOU CAN USE THE VALUE HERE
                        vacancyCount = count;

                        // Load live counts
                        updateCounts();
                    }

                    @Override
                    public void onError(String msg) {
                        Log.e("API", "Error: " + msg);
                    }
                });


            }

            @Override
            public void onError(String msg) {
                Log.e("API", "Error: " + msg);
            }
        });



    }

    private void updateCounts() {
//        String userEmail = getSharedPreferences("UserSession", MODE_PRIVATE)
//                .getString("email", "guest@user.com");
//
//        int estCount = 0;
//        int jobCount = 0;

        // Count establishments
//        String estJson = sharedPreferences.getString(KEY_ESTABLISHMENTS, "{}");
//        try {
//            JSONObject json = new JSONObject(estJson);
//            if (json.has(userEmail)) {
//                JSONArray arr = json.getJSONArray(userEmail);
//                estCount = arr.length();
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        // Count jobs
//        String jobJson = sharedPreferences.getString(KEY_JOBS, "{}");
//        try {
//            JSONObject json = new JSONObject(jobJson);
//            if (json.has(userEmail)) {
//                JSONArray arr = json.getJSONArray(userEmail);
//                jobCount = arr.length();
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        tvEstCount.setText("Establishments\n" + establishmentCount);
        tvJobCount.setText("Job Postings\n" + vacancyCount);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            toolbarTitle.setText("Dashboard");
            updateCounts();
        } else if (id == R.id.nav_establishments) {
            startActivity(new Intent(this, EstablishmentsActivity.class));
        } else if (id == R.id.nav_employee_profile) {
            startActivity(new Intent(this, ManageEmployerProfileActivity.class));
        } else if (id == R.id.nav_job_vacancy) {
            startActivity(new Intent(this, JobVacancyActivity.class));
        } else if (id == R.id.nav_job_matching) {
            startActivity(new Intent(this, JobMatchingEmployerActivity.class));
        } else if (id == R.id.nav_applicants_list) {
            startActivity(new Intent(this, ApplicantsListActivity.class));
        } else if (id == R.id.nav_career_portfolio) {
            startActivity(new Intent(this, CareerPortfolioActivity.class));
        } else if (id == R.id.nav_manage_employee_profile) {
            startActivity(new Intent(this, ManageEmployeeProfileActivity.class));
        } else if (id == R.id.nav_job_application) {
            startActivity(new Intent(this, JobApplicationActivity.class));
        } else if (id == R.id.nav_job_matching_employee) {
            startActivity(new Intent(this, JobMatchingEmployeeActivity.class));
        } else if (id == R.id.nav_jobs_applied) {
            startActivity(new Intent(this, JobsAppliedActivity.class));
        }

        drawerLayout.closeDrawers();
        return true;
    }




    private void loadEstablishments(CountCallback callback) {
        api.getAllEstablishment("*", "establishment_id.asc")
                .enqueue(new Callback<List<Establishment>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<Establishment>> call, @NonNull Response<List<Establishment>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            int count = response.body().size();
                            establishmentCount = count;
                            callback.onSuccess(count);
                        } else {
                            callback.onError("Failed to load");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<Establishment>> call, @NonNull Throwable t) {
                        callback.onError(t.getMessage());
                    }
                });
    }


    private void loadJobVacancies(CountCallback callback) {
        api.getAllJobVacancy("*")
                .enqueue(new Callback<List<JobVacancy>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<JobVacancy>> call, @NonNull Response<List<JobVacancy>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            int count = response.body().size();
                            vacancyCount = count;
                            callback.onSuccess(count);
                        } else {
                            callback.onError("Failed to load");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<JobVacancy>> call, @NonNull Throwable t) {
                        callback.onError(t.getMessage());
                    }
                });
    }

    public interface CountCallback {
        void onSuccess(int count);
        void onError(String msg);
    }

}
