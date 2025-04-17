package model;

import javafx.beans.property.*;

public class Vehicle {
    private final IntegerProperty id;
    private final StringProperty brand;
    private final StringProperty model;
    private final StringProperty category;
    private final DoubleProperty rentalPricePerDay;
    private final StringProperty availabilityStatus;

    public Vehicle(int id, String brand, String model, String category, double rentalPricePerDay, String availabilityStatus) {
        this.id = new SimpleIntegerProperty(id);
        this.brand = new SimpleStringProperty(brand);
        this.model = new SimpleStringProperty(model);
        this.category = new SimpleStringProperty(category);
        this.rentalPricePerDay = new SimpleDoubleProperty(rentalPricePerDay);
        this.availabilityStatus = new SimpleStringProperty(availabilityStatus);
    }

    public int getId() { return id.get(); }
    public IntegerProperty idProperty() { return id; }

    public String getBrand() { return brand.get(); }
    public StringProperty brandProperty() { return brand; }

    public String getModel() { return model.get(); }
    public StringProperty modelProperty() { return model; }

    public String getCategory() { return category.get(); }
    public StringProperty categoryProperty() { return category; }

    public double getRentalPricePerDay() { return rentalPricePerDay.get(); }
    public DoubleProperty rentalPricePerDayProperty() { return rentalPricePerDay; }

    public String getAvailabilityStatus() { return availabilityStatus.get(); }
    public StringProperty availabilityStatusProperty() { return availabilityStatus; }
}
