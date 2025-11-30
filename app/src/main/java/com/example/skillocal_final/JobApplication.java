package com.example.skillocal_final;
import java.io.Serializable;

public class JobApplication implements Serializable {
    private int application_id;
    private int user_id;
    private String createdDate;
    private String firstName;
    private String middleName;
    private String lastName;
    private String suffix;
    private Integer job_vacancy_id;
    private String applicationStatus;

    public JobApplication(){}

    public JobApplication(int user_id, String createdDate, String firstName, String middleName,
                          String lastName, String suffix, Integer job_vacancy_id,
                          String applicationStatus) {

        this.user_id = user_id;
        this.createdDate = createdDate;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.suffix = suffix;
        this.job_vacancy_id = job_vacancy_id;
        this.applicationStatus = applicationStatus;
    }

    // ======= GETTERS =======

    public int getApplication_id() {
        return application_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getSuffix() {
        return suffix;
    }

    public Integer getJob_vacancy_id() {
        return job_vacancy_id;
    }

    public String getApplicationStatus() {
        return applicationStatus;
    }
}
