package model;

import javafx.beans.property.*;

public class Customer {
    private final IntegerProperty id;
    private final StringProperty name;
    private final StringProperty contact;
    private final StringProperty drivingLicense; // Updated to match the database
    private final StringProperty rentalHistory;

    public Customer(int id, String name, String contact, String drivingLicense, String rentalHistory) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.contact = new SimpleStringProperty(contact);
        this.drivingLicense = new SimpleStringProperty(drivingLicense); // Updated
        this.rentalHistory = new SimpleStringProperty(rentalHistory);
    }

    public int getId() { return id.get(); }
    public IntegerProperty idProperty() { return id; }
    public void setId(int id) { this.id.set(id); }

    public String getName() { return name.get(); }
    public StringProperty nameProperty() { return name; }
    public void setName(String name) { this.name.set(name); }

    public String getContact() { return contact.get(); }
    public StringProperty contactProperty() { return contact; }
    public void setContact(String contact) { this.contact.set(contact); }

    public String getDrivingLicense() { return drivingLicense.get(); } // Updated
    public StringProperty drivingLicenseProperty() { return drivingLicense; } // Updated
    public void setDrivingLicense(String drivingLicense) { this.drivingLicense.set(drivingLicense); } // Updated

    public String getRentalHistory() { return rentalHistory.get(); }
    public StringProperty rentalHistoryProperty() { return rentalHistory; }
    public void setRentalHistory(String history) { this.rentalHistory.set(history); }
}
