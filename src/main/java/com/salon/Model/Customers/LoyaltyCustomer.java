package com.salon.Model.Customers;

public class LoyaltyCustomer {
    private int id;
    private String name;
    private String phone;
    private double totalSpent;
    private boolean hasDiscount;
    private String discountCategory;
    private String serviceType;

    public LoyaltyCustomer(int id, String name, String phone, double totalSpent, boolean hasDiscount, String discountCategory, String serviceType) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.totalSpent = totalSpent;
        this.hasDiscount = hasDiscount;
        this.discountCategory = discountCategory;
        this.serviceType = serviceType;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public double getTotalSpent() { return totalSpent; }
    public void setTotalSpent(double totalSpent) { this.totalSpent = totalSpent; }

    public boolean isHasDiscount() { return hasDiscount; }
    public void setHasDiscount(boolean hasDiscount) { this.hasDiscount = hasDiscount; }

    public String getDiscountCategory() { return discountCategory; }
    public void setDiscountCategory(String discountCategory) { this.discountCategory = discountCategory; }

    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }
}