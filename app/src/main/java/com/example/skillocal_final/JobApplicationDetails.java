package com.example.skillocal_final;

public class JobApplicationDetails {

    private Integer user_id;
    private Integer job_application_details_id;
    private String fullName;
    private String civil_status;
    private String sex;
    private String birthplace;
    private String birthdate;

    private  String graduate_institution;
    private  String graduate_course;
    private  String tertiary_institution;
    private  String tertiary_course;

    private  String secondary_institution;
    private  String primary_institution;
    private  String height;
    private  String weight;




    public JobApplicationDetails(

            String fullName,
            String civil_status,
            String sex, String birthplace, String birthdate,
            String graduate_institution, String graduate_course, String tertiary_institution,
            String tertiary_course, String secondary_institution,
            String primary_institution, String height, String weight )
    {

//        this.user_id = user_id;
        this.fullName = fullName;
        this.civil_status = civil_status;
        this.sex = sex;
        this.birthdate = birthdate;
        this.birthplace = birthplace;
        this.graduate_institution = graduate_institution;
        this.graduate_course = graduate_course;
        this.tertiary_institution = tertiary_institution;
        this.tertiary_course = tertiary_course;
        this.secondary_institution = secondary_institution;
        this.primary_institution = primary_institution;
        this.height = height;
        this.weight = weight;

    }

    public Integer getUserId(){return user_id;}

    public Integer getJobApplicationDetailsId(){return job_application_details_id;}
    public String getFullName(){return fullName;}

    public String getCivilStatus(){ return civil_status;}
    public String getSex(){return sex;}
    public String getBirthplace(){return  birthplace;}
    public String getBirthDate(){return birthdate;}

    public String getGraduateInstitution(){return graduate_institution;}
    public String getGraduateCourse(){return graduate_course;}

    public String getTertiaryInstitution(){return tertiary_institution;}
    public String getTertiaryCourse(){return tertiary_course;}

    public String getSecondaryInstitution(){return secondary_institution;}
    public String getPrimaryInstitution(){return primary_institution;}

    public String getHeight(){return height;}

    public String getWeight(){return weight;}


}
