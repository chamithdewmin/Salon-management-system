package com.salon.Model.Finance;

public class FinanceRecord {

    private String date;
    private String description;
    private double amount;

    private String customerName;
    private String staffName;
    private String visitCount;

    private double incomeTotal;
    private double expenseTotal;

    // --- Constructors ---

    // 1. For income or expense record with only date, description, and amount
    public FinanceRecord(String date, String description, double amount) {
        this.date = date;
        this.description = description;
        this.amount = amount;
    }

    // 2. For income record with customer and staff details
    public FinanceRecord(String date, String description, double amount, String customerName, String staffName) {
        this.date = date;
        this.description = description;
        this.amount = amount;
        this.customerName = customerName;
        this.staffName = staffName;
    }

    // 3. For inserting income with customer only (no staff)
    public FinanceRecord(String date, String description, double amount, String customerName) {
        this.date = date;
        this.description = description;
        this.amount = amount;
        this.customerName = customerName;
    }

    // 4. For financial summary (income + expense totals)
    public FinanceRecord(double incomeTotal, double expenseTotal) {
        this.incomeTotal = incomeTotal;
        this.expenseTotal = expenseTotal;
    }

    // 5. For loyalty customer view (serviceCount in description)
    public FinanceRecord(String date, String serviceCount, double totalAmount, String customerName, String staffName, String visitCount) {
        this.date = date;
        this.description = serviceCount;  // In this context, service count replaces description
        this.amount = totalAmount;
        this.customerName = customerName;
        this.staffName = staffName;
        this.visitCount = visitCount;
    }

    // --- Getters & Setters ---

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(String visitCount) {
        this.visitCount = visitCount;
    }

    public double getIncome() {
        return incomeTotal;
    }

    public void setIncome(double incomeTotal) {
        this.incomeTotal = incomeTotal;
    }

    public double getExpenses() {
        return expenseTotal;
    }

    public void setExpenses(double expenseTotal) {
        this.expenseTotal = expenseTotal;
    }

    public double getSaved() {
        return incomeTotal - expenseTotal;
    }
}
