package com.salon.Model;

import java.time.LocalDate;

public class InvoiceItem {
    private LocalDate date;
    private String description;
    private double value;
    private boolean fromReminder;

    public InvoiceItem(LocalDate date, String description, double value, boolean fromReminder) {
        this.date = date;
        this.description = description;
        this.value = value;
        this.fromReminder = fromReminder;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public double getValue() {
        return value;
    }

    public boolean isFromReminder() {
        return fromReminder;
    }

    // ✅ For showing "Pending" or "Complete"
    public String getType() {
        return fromReminder ? "Pending" : "Complete";
    }
}
