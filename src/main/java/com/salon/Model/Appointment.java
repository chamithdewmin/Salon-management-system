package com.salon.Model;

import java.sql.Date;
import java.sql.Time;

public class Appointment {
    private int id;
    private String clientName;
    private String clientContact;
    private Date date;
    private Time time;
    private String serviceType;
    private String staffName;
    private boolean reminderSent; // ✅ New field

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public String getClientContact() { return clientContact; }
    public void setClientContact(String clientContact) { this.clientContact = clientContact; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public Time getTime() { return time; }
    public void setTime(Time time) { this.time = time; }

    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }

    public String getStaffName() { return staffName; }
    public void setStaffName(String staffName) { this.staffName = staffName; }

    public boolean isReminderSent() { return reminderSent; }  // ✅ New getter
    public void setReminderSent(boolean reminderSent) { this.reminderSent = reminderSent; } // ✅ New setter
}
