package com.example.skillocal_final;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Response;

public class JobMatchingEmployerActivity extends AppCompatActivity {

    private LinearLayout layoutContainer;  // layout_employer from XML
    private EditText searchBar;
    ApiServiceJobVacancy apiJobVacancy = ApiInstance.getApiJobVacancy();
    ApiService apiExt = ApiInstance.getApi(); // for establishments, industries, etc
    ApiServiceWorker apiWorker = ApiInstance.getApiWorker(); // worker-related tables
    private Integer currentId;

    // UI controls
    private Button btnRunMatching;
    private ProgressBar progressMatching;

    // background executor
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private static final String TAG = "JobMatchActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_matching_employer);

        currentId = getSharedPreferences("MyRole", MODE_PRIVATE)
                .getInt("userId", 0);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_employer);
        setSupportActionBar(toolbar);

        // Back button
        findViewById(R.id.icon_back_employer).setOnClickListener(v -> finish());

        // Initialize search + list container
        searchBar = findViewById(R.id.et_search_employer);
        layoutContainer = findViewById(R.id.layout_employer);

        // Run matching UI
        btnRunMatching = findViewById(R.id.btnRunMatching);
        progressMatching = findViewById(R.id.progressMatching);

        btnRunMatching.setOnClickListener(v -> startMatchingAllVacancies());

        // Keep existing behavior: load employer's vacancies list in UI (not matching)
//        getAllJobVacancy(this);

        // Search listener filters layoutContainer children (existing)
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterList(s.toString());
            }
        });
    }

    // --------------------------------------------------------------------
    // Filter list based on search text (keeps your previous logic)
    private void filterList(String query) {
        query = query.toLowerCase(Locale.getDefault());

        for (int i = 0; i < layoutContainer.getChildCount(); i++) {
            View child = layoutContainer.getChildAt(i);
            if (child instanceof TextView) {
                TextView item = (TextView) child;
                if (item.getText().toString().toLowerCase(Locale.getDefault()).contains(query)) {
                    item.setVisibility(View.VISIBLE);
                } else {
                    item.setVisibility(View.GONE);
                }
            } else {
                // If it's a complex card (LinearLayout with TextViews), handle generically
                String combinedText = "";
                if (child instanceof LinearLayout) {
                    LinearLayout ll = (LinearLayout) child;
                    for (int j = 0; j < ll.getChildCount(); j++) {
                        View gc = ll.getChildAt(j);
                        if (gc instanceof TextView) combinedText += ((TextView) gc).getText().toString() + " ";
                    }
                }
                boolean show = combinedText.toLowerCase(Locale.getDefault()).contains(query);
                child.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        }
    }

    // --------------------------------------------------------------------
    // Load vacancies for current employer (keeps your UI sample loader)
    private void getAllJobVacancy(Context cont){
        apiJobVacancy.getAllJobVacancyByUserId("*", "eq." + currentId, "vacancy_id.asc")
                .enqueue(new retrofit2.Callback<List<JobVacancy>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<JobVacancy>> call, @NonNull retrofit2.Response<List<JobVacancy>> response) {
                        if (response.isSuccessful()) {
                            List<JobVacancy> resList = response.body();
                            assert resList != null;
                            layoutContainer.removeAllViews();
                            for (JobVacancy e : resList) {
                                TextView tv = new TextView(cont);
                                tv.setText(e.getJob_title());
                                tv.setTextSize(16);
                                tv.setPadding(20, 20, 20, 20);
                                tv.setBackgroundColor(0xFFFFFFFF);
                                tv.setElevation(4);

                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                );
                                params.setMargins(0, 10, 0, 10);
                                tv.setLayoutParams(params);

                                layoutContainer.addView(tv);
                            }
                        } else {
                            Log.e(TAG, "getAllJobVacancy: not successful " + response.code());
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<List<JobVacancy>> call, @NonNull Throwable t) {
                        Log.e(TAG, "getAllJobVacancy Failed: " + t.getMessage());
                    }
                });
    }

    // --------------------------------------------------------------------
    // Trigger: run matching for ALL vacancies (your choice)
    private void startMatchingAllVacancies() {
        btnRunMatching.setEnabled(false);
        progressMatching.setVisibility(View.VISIBLE);
        layoutContainer.removeAllViews(); // clear before showing results

        executor.submit(() -> {
            try {
                // 1) Fetch all active vacancies (All Vacancies across system)
                Call<List<JobVacancy>> vacCall = apiWorker.getAllJobVacancyByUserId("*", "eq."+ String.valueOf(currentId),"eq.Active", "vacancy_id.asc");
                Response<List<JobVacancy>> vacResp = vacCall.execute();

                if (!vacResp.isSuccessful() || vacResp.body() == null) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Failed to fetch vacancies", Toast.LENGTH_LONG).show();
                        btnRunMatching.setEnabled(true);
                        progressMatching.setVisibility(View.GONE);
                    });
                    return;
                }

                List<JobVacancy> vacancies = vacResp.body();

                // Preload full tables to avoid repeated calls per vacancy (matches your JS approach)
                List<WorkExperience> allWorkExp = fetchAllWorkExperience();
                List<Eligibility> allElig = fetchAllEligibility();
                List<Training> allTrainings = fetchAllTraining();
                List<Industry> allIndustry = fetchAllIndustry();

                // For each vacancy compute top3
                for (JobVacancy vac : vacancies) {
                    int vacancyId = vac.getVacancy_id();
                    String jobTitle = vac.getJob_title();
                    String remarks = vac.getRemarks();
                    int establishmentId = vac.getEstablishment_id();
                    Integer industryId = vac.getIndustry_id();
                    String status = vac.getStatus();

                    // find industry name using preloaded list
                    String industryName = "";
                    if (industryId != null && allIndustry != null) {
                        for (Industry ind : allIndustry) {
                            if (ind != null && ind.getIndustry_id() == industryId) {
                                industryName = ind.getIndustry_name();
                                break;
                            }
                        }
                    }

                    // fetch establishment name by id (single call)
                    String establishmentName = "";
                    try {
                        Call<List<Establishment>> estCall = apiWorker.getEstablishmentByEstablishmentId("*", "eq." + establishmentId);
                        Response<List<Establishment>> estResp = estCall.execute();
                        if (estResp.isSuccessful() && estResp.body() != null && !estResp.body().isEmpty()) {
                            establishmentName = estResp.body().get(0).getEstablishmentName();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Establishment fetch failed: " + e.getMessage());
                    }

                    // Build search terms (same order as JS)
                    List<String> searchTerms = new ArrayList<>();
                    if (remarks != null && !remarks.trim().isEmpty()) searchTerms.add(remarks);
                    if (establishmentName != null && !establishmentName.trim().isEmpty()) searchTerms.add(establishmentName);
                    if (industryName != null && !industryName.trim().isEmpty()) searchTerms.add(industryName);
                    if (jobTitle != null && !jobTitle.trim().isEmpty()) searchTerms.add(jobTitle);

                    // If no search terms, create a "No Match Found" card quickly
                    if (searchTerms.isEmpty()) {
                        final View card = buildNoMatchCard(vacancyId, jobTitle, industryName, establishmentName, remarks, status);
                        runOnUiThread(() -> layoutContainer.addView(card));
                        continue;
                    }

                    // Score across tables
                    Map<Integer, Double> scores = new HashMap<>();

                    // WorkExperience (weights 2)
                    for (WorkExperience w : allWorkExp) {
                        double pts = 0;
                        pts += scoreField(w.getPosition(), searchTerms, 2.0);
                        pts += scoreField(w.getAddress(), searchTerms, 2.0);
                        pts += scoreField(w.getCompany(), searchTerms, 2.0);
                        if (pts > 0) {
                            Integer uid = w.getUserId();
                            scores.put(uid, scores.getOrDefault(uid, 0.0) + pts);
                        }
                    }

                    // Eligibility (weight 1)
                    for (Eligibility el : allElig) {
                        double pts = scoreField(el.getName(), searchTerms, 1.0);
                        if (pts > 0) {
                            Integer uid = el.getUserId();
                            scores.put(uid, scores.getOrDefault(uid, 0.0) + pts);
                        }
                    }

                    // Trainings (weight 1.5)
                    for (Training t : allTrainings) {
                        double pts = 0;
                        pts += scoreField(t.getName(), searchTerms, 1.5);
                        pts += scoreField(t.getSkillsAcquired(), searchTerms, 1.5);
                        if (pts > 0) {
                            Integer uid = t.getUserId();
                            scores.put(uid, scores.getOrDefault(uid, 0.0) + pts);
                        }
                    }

                    // Prepare top3 list
                    List<Map.Entry<Integer, Double>> ranked = new ArrayList<>(scores.entrySet());
                    // sort descending by points
                    Collections.sort(ranked, (a, b) -> Double.compare(b.getValue(), a.getValue()));

                    // If no matches
                    if (ranked.isEmpty()) {
                        final View card = buildNoMatchCard(vacancyId, jobTitle, industryName, establishmentName, remarks, status);
                        runOnUiThread(() -> layoutContainer.addView(card));
                    } else {
                        // get up to top 3 entries
                        int limit = Math.min(3, ranked.size());
                        List<Map.Entry<Integer, Double>> top3 = ranked.subList(0, limit);

                        // Fetch user details for top3 (one-by-one)
                        List<User> topUsers = new ArrayList<>();
                        for (Map.Entry<Integer, Double> entry : top3) {
                            Integer uid = entry.getKey();
                            try {
                                Call<List<User>> uCall = apiWorker.getUserByUserId("*", "eq." + uid);
                                Response<List<User>> uResp = uCall.execute();
                                if (uResp.isSuccessful() && uResp.body() != null && !uResp.body().isEmpty()) {
                                    topUsers.add(uResp.body().get(0));
                                } else {
                                    // create a placeholder User if not found
                                    User unknown = new User();
                                    unknown.setUser_id(uid);
                                    unknown.setFirstName("Unknown");
                                    topUsers.add(unknown);
                                }
                            } catch (IOException e) {
                                Log.e(TAG, "getUserByUserId failed: " + e.getMessage());
                            }
                        }

                        final View card = buildMatchCard(vacancyId, jobTitle, industryName, establishmentName, remarks, status, top3, topUsers);
                        runOnUiThread(() -> layoutContainer.addView(card));
                    }
                } // end for vacancies

                // Done
                runOnUiThread(() -> {
                    Toast.makeText(JobMatchingEmployerActivity.this, "Matching complete", Toast.LENGTH_SHORT).show();
                    btnRunMatching.setEnabled(true);
                    progressMatching.setVisibility(View.GONE);
                });

            } catch (Exception ex) {
                Log.e(TAG, "Matching error", ex);
                runOnUiThread(() -> {
                    Toast.makeText(JobMatchingEmployerActivity.this, "Matching failed: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                    btnRunMatching.setEnabled(true);
                    progressMatching.setVisibility(View.GONE);
                });
            }
        });
    }

    // --------------------------------------------------------------------
    // Helpers: Fetch full tables (synchronous calls executed on background thread)
    private List<WorkExperience> fetchAllWorkExperience() {
        try {
            Call<List<WorkExperience>> call = apiWorker.getAllWorkExperience("*");
            Response<List<WorkExperience>> resp = call.execute();
            if (resp.isSuccessful() && resp.body() != null) return resp.body();
        } catch (IOException e) {
            Log.e(TAG, "fetchAllWorkExperience error: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    private List<Eligibility> fetchAllEligibility() {
        try {
            Call<List<Eligibility>> call = apiWorker.getAllEligibility("*");
            Response<List<Eligibility>> resp = call.execute();
            if (resp.isSuccessful() && resp.body() != null) return resp.body();
        } catch (IOException e) {
            Log.e(TAG, "fetchAllEligibility error: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    private List<Training> fetchAllTraining() {
        try {
            Call<List<Training>> call = apiWorker.getAllTraining("*");
            Response<List<Training>> resp = call.execute();
            if (resp.isSuccessful() && resp.body() != null) return resp.body();
        } catch (IOException e) {
            Log.e(TAG, "fetchAllTraining error: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    private List<Industry> fetchAllIndustry() {
        try {
            Call<List<Industry>> call = apiExt.getAllIndustry("*");
            Response<List<Industry>> resp = call.execute();
            if (resp.isSuccessful() && resp.body() != null) return resp.body();
        } catch (IOException e) {
            Log.e(TAG, "fetchAllIndustry error: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    // --------------------------------------------------------------------
    // Build a simple "No Match Found" card view (TextView-based)
    private View buildNoMatchCard(int vacancyId, String jobTitle, String industryName, String establishmentName, String remarks, String status) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(24, 24, 24, 24);
        card.setBackgroundColor(0xFFFAFAFA);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 12, 0, 12);
        card.setLayoutParams(lp);

        TextView title = new TextView(this);
        title.setText(String.format(Locale.getDefault(), "%s", jobTitle));
        title.setTextSize(16);
        title.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView meta = new TextView(this);
        meta.setText(String.format("Establishment: %s • Industry: %s", establishmentName, industryName));

        TextView remarkTV = new TextView(this);
        remarkTV.setText("Remarks: " + (remarks == null ? "-" : remarks));

        TextView statusTV = new TextView(this);
        statusTV.setText("Status: " + status + " • No Match Found");
        statusTV.setGravity(Gravity.END);

        card.addView(title);
        card.addView(meta);
        card.addView(remarkTV);
        card.addView(statusTV);

        return card;
    }

    // --------------------------------------------------------------------
    // Build a match card showing top 3
    // topList: entries (user_id -> points)
    // topUsers: parallel list of User objects in same order (size matches topList.size())
    private View buildMatchCard(int vacancyId, String jobTitle, String industryName, String establishmentName, String remarks, String status,
                                List<Map.Entry<Integer, Double>> topList, List<User> topUsers) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(24, 24, 24, 24);
        card.setBackgroundColor(0xFFFFFFFF);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 12, 0, 12);
        card.setLayoutParams(lp);

        TextView title = new TextView(this);
        title.setText(jobTitle);
        title.setTextSize(16);
        title.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView meta = new TextView(this);
        meta.setText(String.format("Establishment: %s • Industry: %s", establishmentName, industryName));

        TextView remarkTV = new TextView(this);
        remarkTV.setText("Remarks: " + (remarks == null ? "-" : remarks));

        TextView statusTV = new TextView(this);
        statusTV.setText("Status: " + status);
        statusTV.setGravity(Gravity.END);

        card.addView(title);
        card.addView(meta);
        card.addView(remarkTV);

        // Top 3 lines
        for (int i = 0; i < topList.size(); i++) {
            Map.Entry<Integer, Double> entry = topList.get(i);
            User u = (i < topUsers.size() ? topUsers.get(i) : null);
            String name = (u != null ? formatUserFullName(u) : ("User ID: " + entry.getKey()));
            double pts = entry.getValue();

            TextView matchLine = new TextView(this);
            matchLine.setText(String.format(Locale.getDefault(), "Top %d: %s — %.1f pts", i+1, name, pts));
            matchLine.setPadding(0, 8, 0, 8);
            card.addView(matchLine);
        }

        card.addView(statusTV);
        return card;
    }

    private String formatUserFullName(User u) {
        StringBuilder b = new StringBuilder();
        if (u.getFName() != null) b.append(u.getFName()).append(" ");
        if (u.getMName() != null) b.append(u.getMName()).append(" ");
        if (u.getLName() != null) b.append(u.getLName()).append(" ");
        if (u.getSuffix() != null) b.append(u.getSuffix()).append(" ");
        String name = b.toString().trim().replaceAll("\\s+", " ");
        return name.isEmpty() ? ("User ID: " + u.getUserId()) : name;
    }

    // --------------------------------------------------------------------
    // Scoring helper: identical to your JS version
    private double scoreField(String text, List<String> terms, double weight) {
        if (text == null) return 0.0;
        String lower = text.toLowerCase(Locale.getDefault());
        double score = 0.0;
        for (String t : terms) {
            if (t == null) continue;
            String term = t.toLowerCase(Locale.getDefault());
            if (lower.equals(term)) score += 1.0 * weight;
            else if (lower.contains(term)) score += 1.0 * weight;
        }
        return score;
    }
}
