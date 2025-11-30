package com.example.skillocal_final;

public class JobApplicationInterfaceWorker {
    private Integer job_application_id;
    private Integer user_id;
    private Integer job_vacancy_id;

    private String firstName;
    private String middleName;
    private String lastName;
    private String suffix;
    private String applicationStatus;




    public JobApplicationInterfaceWorker(
            Integer user_id,
            Integer job_vacancy_id,
            String firstName,
            String middleName,
            String lastName,
            String suffix
//            String applicationStatus
            )
    {

//        this.user_id = user_id;
        this.user_id = user_id;
        this.job_vacancy_id = job_vacancy_id;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.suffix = suffix;
        this.applicationStatus = applicationStatus;
    }

    public Integer getUserId(){return user_id;}

    public Integer getJobVacancyId(){return job_vacancy_id;}
    public String getFirstName(){return firstName;}

    public String getMiddleName(){ return middleName;}
    public String getLastName(){return lastName;}
    public String getSuffix(){return  suffix;}
    public String getApplicationStatus(){return applicationStatus;}


}
