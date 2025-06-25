package com.salon.Model;

public class Income {
    private int id;
    private String date;
    private String description;
    private double value;

    public Income() {}

    public Income(String date, String description, double value) {
        this.date = date;
        this.description = description;
        this.value = value;
    }

    public int getId() { return id; }
    public String getDate() { return date; }
    public String getDescription() { return description; }
    public double getValue() { return value; }

    public void setId(int id) { this.id = id; }
    public void setDate(String date) { this.date = date; }
    public void setDescription(String description) { this.description = description; }
    public void setValue(double value) { this.value = value; }
}