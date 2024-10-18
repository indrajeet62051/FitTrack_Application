package com.example.gymesticapp.Model;

public class UserModel {
    String fullName,mobileNo,email,birthDate,gender,weight,height;

    public UserModel() {

    }

    public UserModel(String fullName, String mobileNo, String email, String birthDate, String gender, String weight, String height) {
        this.fullName = fullName;
        this.mobileNo = mobileNo;
        this.email = email;
        this.birthDate = birthDate;
        this.gender = gender;
        this.weight = weight;
        this.height = height;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }
}
