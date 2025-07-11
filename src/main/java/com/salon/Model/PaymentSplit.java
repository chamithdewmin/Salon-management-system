package com.salon.Model;

public class PaymentSplit {
    private final String recipient;  // Owner or Staff Name
    private final double amount;

    public PaymentSplit(String recipient, double amount) {
        this.recipient = recipient;
        this.amount = amount;
    }

    public String getRecipient() {
        return recipient;
    }

    public double getAmount() {
        return amount;
    }
}
