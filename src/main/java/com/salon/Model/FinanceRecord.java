package com.salon.Model;

public class FinanceRecord {
    private String date;
    private String description;
    private double amount; // renamed from income/expenses to common 'amount'

    private double incomeTotal;
    private double expenseTotal;

    // For inserting income/expense
    public FinanceRecord(String date, String description, double amount) {
        this.date = date;
        this.description = description;
        this.amount = amount;
    }

    // For summary
    public FinanceRecord(double incomeTotal, double expenseTotal) {
        this.incomeTotal = incomeTotal;
        this.expenseTotal = expenseTotal;
    }

    public String getDate() { return date; }
    public String getDescription() { return description; }
    public double getAmount() { return amount; }

    public double getIncome() { return incomeTotal; }
    public double getExpenses() { return expenseTotal; }
    public double getSaved() {
        return incomeTotal - expenseTotal;
    }
}
