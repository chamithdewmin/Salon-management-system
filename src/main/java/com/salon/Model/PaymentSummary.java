package com.salon.Model;

public class PaymentSummary {
    private String customerName;
    private String service;
    private String staff;
    private double amount;
    private String description;

    // Constructor with description
    public PaymentSummary(String customerName, String service, String staff, double amount, String description) {
        this.customerName = customerName;
        this.service = service;
        this.staff = staff;
        this.amount = amount;
        this.description = description;
    }

    // Constructor without description (for backward compatibility)
    public PaymentSummary(String customerName, String service, String staff, double amount) {
        this.customerName = customerName;
        this.service = service;
        this.staff = staff;
        this.amount = amount;
        this.description = service; // Use service name as default description
    }

    // Getters and Setters
    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getStaff() {
        return staff;
    }

    public void setStaff(String staff) {
        this.staff = staff;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description != null ? description : service;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}