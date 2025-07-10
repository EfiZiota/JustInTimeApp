package com.example.justintimeapp;

//Δημιουργία ενός αντικειμένου διεύθυνσης με τα απαραίτητα στοιχεία
public class NewAddress {

    String address;
    String code;
    String city;
    String county;
    public NewAddress() {
    }

    public NewAddress(String address, String code, String city, String county) {
        this.address = address;
        this.code = code;
        this.city = city;
        this.county = county;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
