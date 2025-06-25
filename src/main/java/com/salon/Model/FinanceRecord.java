package com.salon.Model;

public class FinanceRecord {
    private double income;
    private double expenses;
    private String date;
    private String description;

    public FinanceRecord(double income, double expenses) {
        this.income = income;
        this.expenses = expenses;
    }

    public FinanceRecord(String date, String description, double income) {
        this.date = date;
        this.description = description;
        this.income = income;
    }

    public double getIncome() {
        return income;
    }

    public double getExpenses() {
        return expenses;
    }

    public double getSaved() {
        return income - expenses;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }
}
