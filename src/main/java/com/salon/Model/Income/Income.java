package com.salon.Model.Income;

public class Income {
    private String recipient;
    private double amount;
    private String date;
    private String service;
    private String customerName;

    private String description;
    private String staffName;
    private String visitCount;

    private double totalIncome;
    private double totalExpenses;
    private double totalSaved;

    // ✅ Constructor for report row (used in ReportController)
    public Income(String date, String description, double amount, String customerName, String staffName, String visitCount) {
        this.date = date;
        this.description = description;
        this.amount = amount;
        this.customerName = customerName;
        this.staffName = staffName;
        this.visitCount = visitCount;
    }

    // ✅ Constructor for inserting into DB (used in CashierController)
    public Income(String recipient, double amount, String date, String service, String customerName) {
        this.recipient = recipient;
        this.amount = amount;
        this.date = date;
        this.service = service;
        this.customerName = customerName;
    }

    // ✅ Constructor for summary values (used in ReportController)
    public Income(double totalIncome, double totalExpenses, double totalSaved) {
        this.totalIncome = totalIncome;
        this.totalExpenses = totalExpenses;
        this.totalSaved = totalSaved;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description != null ? description : service;
    }

    public double getAmount() {
        return amount;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getStaffName() {
        return staffName != null ? staffName : recipient;
    }

    public String getVisitCount() {
        return visitCount;
    }

    public double getIncome() {
        return totalIncome;
    }

    public double getExpenses() {
        return totalExpenses;
    }

    public double getSaved() {
        return totalSaved;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getService() {
        return service;
    }
}
