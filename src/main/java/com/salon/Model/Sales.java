package com.salon.Model;

public class Sales {
    private int id;
    private String customerName;
    private String phoneNumber;
    private String services;
    private String staff;
    private double amount;
    private String description;
    private String date;

    public Sales(int id, String customerName, String phoneNumber, String services, String staff, double amount, String description, String date) {
        this.id = id;
        this.customerName = customerName;
        this.phoneNumber = phoneNumber;
        this.services = services;
        this.staff = staff;
        this.amount = amount;
        this.description = description;
        this.date = date;
    }

    // Constructor without ID (for inserting new records)
    public Sales(String customerName, String phoneNumber, String services, String staff, double amount, String description, String date) {
        this.customerName = customerName;
        this.phoneNumber = phoneNumber;
        this.services = services;
        this.staff = staff;
        this.amount = amount;
        this.description = description;
        this.date = date;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getServices() {
        return services;
    }

    public String getStaff() {
        return staff;
    }

    public double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    // Setters (optional, if needed)
    public void setId(int id) {
        this.id = id;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setServices(String services) {
        this.services = services;
    }

    public void setStaff(String staff) {
        this.staff = staff;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
