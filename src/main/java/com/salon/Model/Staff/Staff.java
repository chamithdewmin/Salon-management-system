package com.salon.Model.Staff;

import javafx.beans.property.*;

public class Staff {
    private final IntegerProperty id;
    private final StringProperty name;
    private final StringProperty phone;
    private final StringProperty nic;
    private final StringProperty address;

    public Staff(int id, String name, String phone, String nic, String address) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.phone = new SimpleStringProperty(phone);
        this.nic = new SimpleStringProperty(nic);
        this.address = new SimpleStringProperty(address);
    }

    public int getId() { return id.get(); }
    public String getName() { return name.get(); }
    public String getPhone() { return phone.get(); }
    public String getNic() { return nic.get(); }
    public String getAddress() { return address.get(); }

    public IntegerProperty idProperty() { return id; }
    public StringProperty nameProperty() { return name; }
    public StringProperty phoneProperty() { return phone; }
    public StringProperty nicProperty() { return nic; }
    public StringProperty addressProperty() { return address; }

    public void setId(int id) { this.id.set(id); }
    public void setName(String name) { this.name.set(name); }
    public void setPhone(String phone) { this.phone.set(phone); }
    public void setNic(String nic) { this.nic.set(nic); }
    public void setAddress(String address) { this.address.set(address); }
}
