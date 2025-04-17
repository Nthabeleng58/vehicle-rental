package com.example.demo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Vehicle;

import java.sql.*;

public class VehicleController {

    // TableView and TableColumn definitions for displaying vehicle data
    @FXML private TableView<Vehicle> vehicleTable;
    @FXML private TableColumn<Vehicle, Integer> idCol;
    @FXML private TableColumn<Vehicle, String> brandCol;
    @FXML private TableColumn<Vehicle, String> modelCol;
    @FXML private TableColumn<Vehicle, String> categoryCol;
    @FXML private TableColumn<Vehicle, Double> rentalPriceCol;
    @FXML private TableColumn<Vehicle, String> availabilityStatusCol;

    // Text fields for adding/editing vehicle details
    @FXML private TextField idField, brandField, modelField, categoryField, priceField;
    @FXML private ComboBox<String> availabilityBox;

    private final ObservableList<Vehicle> vehicleList = FXCollections.observableArrayList(); // List to store vehicle data

    // Initializes the controller, sets up the table columns, and loads data from the database
    public void initialize() {

        idCol.setCellValueFactory(cell -> cell.getValue().idProperty().asObject());
        brandCol.setCellValueFactory(cell -> cell.getValue().brandProperty());
        modelCol.setCellValueFactory(cell -> cell.getValue().modelProperty());
        categoryCol.setCellValueFactory(cell -> cell.getValue().categoryProperty());
        rentalPriceCol.setCellValueFactory(cell -> cell.getValue().rentalPricePerDayProperty().asObject());
        availabilityStatusCol.setCellValueFactory(cell -> cell.getValue().availabilityStatusProperty());

        // Setting items for the availabilityComboBox (Available or Unavailable)
        availabilityBox.setItems(FXCollections.observableArrayList("Available", "Unavailable"));

        vehicleTable.setItems(vehicleList);
        vehicleTable.setOnMouseClicked(e -> populateFieldsFromTable());

        loadVehiclesFromDatabase();
    }

    // Loads vehicle data from the database and populates the vehicleList
    private void loadVehiclesFromDatabase() {
        vehicleList.clear();
        try (Connection conn = DatabaseConnection.getConnection()) {

            String query = "SELECT * FROM vehicles";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            // Iterate through the ResultSet and create Vehicle objects
            while (rs.next()) {
                int id = rs.getInt("id");
                String brand = rs.getString("brand");
                String model = rs.getString("model");
                String category = rs.getString("category");
                double price = rs.getDouble("price_per_day");
                String availability = rs.getBoolean("available") ? "Available" : "Unavailable";

                // Add each vehicle to the ObservableList (vehicleList)
                vehicleList.add(new Vehicle(id, brand, model, category, price, availability));
            }
        } catch (Exception e) {
            showAlert("Error loading data: " + e.getMessage());
        }
    }

    // Handles adding a new vehicle to the database
    @FXML
    private void handleAdd() {
        try {
            // Get data from text fields and combo box
            String brand = brandField.getText();
            String model = modelField.getText();
            String category = categoryField.getText();
            double price = Double.parseDouble(priceField.getText());
            String availability = availabilityBox.getValue();
            boolean isAvailable = availability.equalsIgnoreCase("Unavailable");

            Connection conn = DatabaseConnection.getConnection(); // Get database connection
            if (conn != null) {
                // SQL query to insert a new vehicle into the database
                String sql = "INSERT INTO vehicles (brand, model, category, price_per_day, available) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS); // Prepare statement for insertion
                stmt.setString(1, brand);
                stmt.setString(2, model);
                stmt.setString(3, category);
                stmt.setDouble(4, price);
                stmt.setBoolean(5, isAvailable);
                stmt.executeUpdate(); // Execute the query

                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int newId = generatedKeys.getInt(1);
                    vehicleList.add(new Vehicle(newId, brand, model, category, price, availability));
                }

                conn.close();
                clearFields(); // Clear input fields after adding
            }
        } catch (Exception e) {
            showAlert("Error saving to database: " + e.getMessage());
        }
    }

    // Handles updating an existing vehicle's details
    @FXML
    private void handleUpdate() {
        try {
            int id = Integer.parseInt(idField.getText());
            String brand = brandField.getText();
            String model = modelField.getText();
            String category = categoryField.getText();
            double price = Double.parseDouble(priceField.getText());
            String availability = availabilityBox.getValue();
            boolean isAvailable = availability.equalsIgnoreCase("Available");

            Connection conn = DatabaseConnection.getConnection();
            if (conn != null) {
                // SQL query to update an existing vehicle
                String sql = "UPDATE vehicles SET brand=?, model=?, category=?, price_per_day=?, available=? WHERE id=?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, brand);
                stmt.setString(2, model);
                stmt.setString(3, category);
                stmt.setDouble(4, price);
                stmt.setBoolean(5, isAvailable);
                stmt.setInt(6, id);
                stmt.executeUpdate(); // Execute the update query
                conn.close();

                loadVehiclesFromDatabase();
                clearFields();
            }
        } catch (Exception e) {
            showAlert("Error updating vehicle: " + e.getMessage()); // Show alert if an error occurs
        }
    }

    // Handles deleting a vehicle from the database
    @FXML
    private void handleDelete() {
        try {
            int id = Integer.parseInt(idField.getText());

            Connection conn = DatabaseConnection.getConnection();
            if (conn != null) {
                // SQL query to delete a vehicle by ID
                String sql = "DELETE FROM vehicles WHERE id=?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, id);
                stmt.executeUpdate();
                conn.close();

                loadVehiclesFromDatabase();
                clearFields();
            }
        } catch (Exception e) {
            showAlert("Error deleting vehicle: " + e.getMessage());}
    }


    @FXML
    private void handleSearch() {
        try {
            int id = Integer.parseInt(idField.getText());

            Connection conn = DatabaseConnection.getConnection();
            if (conn != null) {

                String sql = "SELECT * FROM vehicles WHERE id=?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, id);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    // Populate fields with vehicle data if found
                    brandField.setText(rs.getString("brand"));
                    modelField.setText(rs.getString("model"));
                    categoryField.setText(rs.getString("category"));
                    priceField.setText(String.valueOf(rs.getDouble("price_per_day")));
                    availabilityBox.setValue(rs.getBoolean("available") ? "Available" : "Unavailable");
                } else {
                    showAlert("Vehicle not found.");
                }
                conn.close();
            }
        } catch (Exception e) {
            showAlert("Error searching vehicle: " + e.getMessage());
        }
    }

    // Fills the input fields when a vehicle is selected from the table
    private void populateFieldsFromTable() {
        Vehicle selected = vehicleTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            idField.setText(String.valueOf(selected.getId()));
            brandField.setText(selected.getBrand());
            modelField.setText(selected.getModel());
            categoryField.setText(selected.getCategory());
            priceField.setText(String.valueOf(selected.getRentalPricePerDay()));
            availabilityBox.setValue(selected.getAvailabilityStatus());
        }
    }

    // Clears the input fields
    private void clearFields() {
        idField.clear();
        brandField.clear();
        modelField.clear();
        categoryField.clear();
        priceField.clear();
        availabilityBox.setValue(null);
    }

    // Shows an alert with a given message
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Vehicle Manager");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
