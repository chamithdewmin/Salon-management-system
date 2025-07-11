package com.salon.Model;

public class PaymentSummary {
    private String customerName;
    private String service;
    private String staff;
    private double amount;
    private String description;

    // Constructor without description
    public PaymentSummary(String customerName, String service, String staff, double amount) {
        this.customerName = customerName;
        this.service = service;
        this.staff = staff;
        this.amount = amount;
        this.description = "";
    }

    // Constructor with description
    public PaymentSummary(String customerName, String service, String staff, double amount, String description) {
        this.customerName = customerName;
        this.service = service;
        this.staff = staff;
        this.amount = amount;
        this.description = description;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getService() {
        return service;
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

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setService(String service) {
        this.service = service;
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
}
