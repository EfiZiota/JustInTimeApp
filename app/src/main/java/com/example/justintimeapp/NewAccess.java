package com.example.justintimeapp;

//Δημιουργία ενός αντικειμένου πρόσβασης με τα απαραίτητα στοιχεία
public class NewAccess {

    String username;
    String password;


    public NewAccess(String username, String password) {
        this.username = username;
        this.password = password;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
