package com.example.skillocal_final;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiServiceWorker {

    //This is for WorkExperience Table
    @GET("WorkExperience")
    Call<List<WorkExperience>> getAllWorkExperience(
            @Query("select") String select
    );

    @GET("WorkExperience")
    Call<List<WorkExperience>> getWorkExperienceByUserId(
            @Query("select") String select,
            @Query("user_id") String userIdFilter,
            @Query("order") String order
    );



    @POST("WorkExperience")
    Call<Void> insertWorkExperience(
            @Query("select") String select,
            @Body WorkExperience workExperience
    );

    // UPDATE WorkExperience - Use PATCH for partial updates
    @PATCH("WorkExperience")
    Call<Void> updateWorkExperience(
            @Query("work_experience_id") String eqFilter,  // example: "eq.123"
            @Body WorkExperience workExperience
    );
    //Delete WorkExperience
    @DELETE("WorkExperience")
    Call<Void> deleteWorkExperience(
            @Query("work_experience_id") String workExperienceIdEq  // e.g. "eq.45"
    );





    //This is for Eligibility Table
    @GET("Eligibility")
    Call<List<Eligibility>> getAllEligibility(
            @Query("select") String select
    );

    @GET("Eligibility")
    Call<List<Eligibility>> getEligibilityByUserId(
            @Query("select") String select,
            @Query("user_id") String userIdFilter,
            @Query("order") String order
    );

    @POST("Eligibility")
    Call<Void> insertEligibility(
            @Query("select") String select,
            @Body Eligibility eligibility
    );

    // UPDATE Eligibility - Use PATCH for partial updates
    @PATCH("Eligibility")
    Call<Void> updateEligibility(
            @Query("eligibility_id") String eqFilter,  // example: "eq.123"
            @Body Eligibility eligibility
    );
    //Delete Eligibility
    @DELETE("Eligibility")
    Call<Void> deleteEligibility(
            @Query("eligibility_id") String eligibilityIdEq  // e.g. "eq.45"
    );



    //This is for Trainings Table
    @GET("Trainings")
    Call<List<Training>> getAllTraining(
            @Query("select") String select
    );

    @GET("Trainings")
    Call<List<Training>> getTrainingByUserId(
            @Query("select") String select,
            @Query("user_id") String userIdFilter,
            @Query("order") String order
    );


    @POST("Trainings")
    Call<Void> insertTraining(
            @Query("select") String select,
            @Body Training training
    );

    // UPDATE Training - Use PATCH for partial updates
    @PATCH("Trainings")
    Call<Void> updateTraining(
            @Query("training_id") String eqFilter,  // example: "eq.123"
            @Body Training training
    );
    //Delete Training
    @DELETE("Trainings")
    Call<Void> deleteTraining(
            @Query("training_id") String trainingIdEq  // e.g. "eq.45"
    );



//    FOR USERS TABLE - Manage Employee Profile
    @GET("Users")
    Call<List<User>> getUserByUserId(
            @Query("select") String select,
            @Query("user_id") String userIdFilter
    );


    // UPDATE Training - Use PATCH for partial updates
    @PATCH("Users")
    Call<Void> updateUser(
            @Query("user_id") String eqFilter,  // example: "eq.123"
            @Body User user
    );

    //    FOR JOB APPLICATION DETAILS TABLE - Job Application/Job Seeker Information
    @GET("JobApplicationDetails")
    Call<List<JobApplicationDetails>> getJobApplicationDetailsByUserId(
            @Query("select") String select,
            @Query("user_id") String userIdFilter
    );

//    @PATCH("JobApplicationDetails")
//    Call<Void> updateJobApplicationDetailsByJobApplicationId(
//            @Query("job_application_details_id") String eqFilter,  // example: "eq.123"
//            @Body JobApplicationDetails jobApplicationDetails
//    );

    @PATCH("JobApplicationDetails")
    Call<Void> updateJobApplicationDetailsByJobApplicationId(
            @Query("job_application_details_id") String filter,
            @Body JobApplicationDetails details
    );

    //worker's list of job vacancies coming from employers
    @GET("JobVacancy")
    Call<List<JobVacancy>> getAllJobVacancy(
            @Query("select") String select,
            @Query("status") String statusFilter,
            @Query("order") String order
    );

    @GET("Establishment")
    Call<List<Establishment>> getEstablishmentByEstablishment(
            @Query("select") String select

    );

    //checking for duplicate applications
    @GET("JobApplication")
    Call<List<JobApplicationInterfaceWorker>> getJobApplicationDuplicates(
            @Query("select") String select,
            @Query("user_id") String userIdFilter,
            @Query("job_vacancy_id") String vacancyIdFilter
//            @Query("order") String order


    );

    @Headers("Prefer: return=representation")
    @POST("JobApplication")
    Call<List<JobApplicationInterfaceWorker>> insertJobApplication(
            @Body JobApplicationInterfaceWorker body
    );


    //get list of jobs applied by Id
    @GET("JobApplication")
    Call<List<JobApplicationInterfaceWorker>> getJobApplicationsById(
            @Query("select") String select,
            @Query("user_id") String userIdFilter,
            @Query("order") String order
    );

    @GET("Establishment")
    Call<List<Establishment>> getEstablishmentByEstablishmentId(
            @Query("select") String select,
            @Query("establishment_id") String establishmentIdFilter
    );

    @GET("JobVacancy")
    Call<List<JobVacancy>> getJobVacancyByVacancyId(
            @Query("select") String select,
            @Query("vacancy_id") String vacancyIdFilter
    );

}
