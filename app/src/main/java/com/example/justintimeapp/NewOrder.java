package com.example.justintimeapp;

//Δημιουργία ενός αντικειμένου παραγγελίας με τα απαραίτητα στοιχεία
public class NewOrder {

    private long id;
    private String orderCode;
    private String Courier;
    private String date;
    private String flexibleDate;
    private String hours;
    private String address;


    public NewOrder(long id, String orderCode, String courier, String date, String flexibleDate, String hours, String address) {
        this.id = id;
        this.orderCode = orderCode;
        this.Courier = courier;
        this.date = date;
        this.flexibleDate = flexibleDate;
        this.hours = hours;
        this.address = address;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getCourier() {
        return Courier;
    }

    public void setCourier(String courier) {
        Courier = courier;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFlexibleDate() {
        return flexibleDate;
    }

    public void setFlexibleDate(String flexibleDate) {
        this.flexibleDate = flexibleDate;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
