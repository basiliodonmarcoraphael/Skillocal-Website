package com.example.skillocal_final;

public class UserRegistration {

    private Integer user_id;

    private String email;

    private  String password;
    private String role;
    private String firstName;
    private String middleName;
    private  String lastName;
    private  String suffix;
    private  String birthDate;
    private  String sex;
    private  String address;
    private  String contact_number;





    public UserRegistration(
            String email,
            String password,
            String role,
            String firstName,
            String middleName, String lastName,
            String birthDate, String sex, String address, String contact_number)
    {

//        this.user_id = user_id;
        this.email = email;
        this.password = password;
        this.role = role;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;

        this.birthDate = birthDate;
        this.sex = sex;
        this.address = address;
        this.contact_number = contact_number;

    }

    public Integer getUserId(){return user_id;}
    public String getEmail(){return email;}
    public String getPassword(){return password;}
    public String getRole(){return role;}
    public String getFName(){ return firstName;}
    public String getMName(){return middleName;}
    public String getLName(){return  lastName;}



    public String getBirthDate(){return birthDate;}

    public String getSex(){return sex;}


    public String getAddress(){return address;}
    public String getContactNumber(){return contact_number;}



}
