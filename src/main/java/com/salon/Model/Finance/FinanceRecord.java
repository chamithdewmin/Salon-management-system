package com.salon.Model.Finance;

public class FinanceRecord {
    private String date;
    private String description;
    private double amount;

    private double incomeTotal;
    private double expenseTotal;

    private String customerName;

    // Constructor for individual records (income or expense)
    public FinanceRecord(String date, String description, double amount) {
        this.date = date;
        this.description = description;
        this.amount = amount;
    }

    // Constructor for individual income records with customer name
    public FinanceRecord(String date, String description, double amount, String customerName) {
        this.date = date;
        this.description = description;
        this.amount = amount;
        this.customerName = customerName;
    }

    // Constructor for summary
    public FinanceRecord(double incomeTotal, double expenseTotal) {
        this.incomeTotal = incomeTotal;
        this.expenseTotal = expenseTotal;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    public double getIncome() {
        return incomeTotal;
    }

    public double getExpenses() {
        return expenseTotal;
    }

    public double getSaved() {
        return incomeTotal - expenseTotal;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setService(String serviceDescription) {
        this.description = serviceDescription;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
