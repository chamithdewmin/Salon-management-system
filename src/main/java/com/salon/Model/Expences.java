package com.salon.Model;

import java.sql.Date;

public class Expences {
    private int id;
    private Date date;
    private String description;
    private double value;
    private String category; // Needs or Wants

    // Constructor, Getters & Setters


    public Expences(int id, Date date, String description, double value, String category) {
        this.id = id;
        this.date = date;
        this.description = description;
        this.value = value;
        this.category = category;
    }

    public Expences() {

    }

    public int getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public double getValue() {
        return value;
    }

    public String getCategory() {
        return category;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
