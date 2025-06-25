package com.salon.Model.Customers;

import java.sql.Date;

public class Customer {
    private int id;
    private String fullName;
    private String phone;
    private String email;
    private String gender;
    private Date joinDate;

    public Customer(int id, String fullName, String phone, String email, String gender, Date joinDate) {
        this.id = id;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.gender = gender;
        this.joinDate = joinDate;
    }

    // Getters
    public int getId() { return id; }
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getGender() { return gender; }
    public Date getJoinDate() { return joinDate; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    public void setGender(String gender) { this.gender = gender; }
    public void setJoinDate(Date joinDate) { this.joinDate = joinDate; }
}
