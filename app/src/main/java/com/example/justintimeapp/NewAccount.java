package com.example.justintimeapp;

//Δημιουργία ενός αντικειμένου λογαριασμού με τα απαραίτητα στοιχεία
public class NewAccount {

    private String name;
    private String surname;
    private String phone;
    private String mobile;
    private String email;

    public NewAccount() {
    }

    public NewAccount(String name, String surname, String phone, String mobile, String email) {
        this.name = name;
        this.surname = surname;
        this.phone = phone;
        this.mobile = mobile;
        this.email = email;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
