package com.example.demo;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookingController {

    @FXML private ComboBox<String> vehicleComboBox;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TextField customerIdField;
    @FXML private Button createBookingButton;
    @FXML private Button cancelBookingButton;

    private Connection connection;

    public void initialize() {
        // Initialize connection to database
        connection = DatabaseConnection.getConnection();
        loadAvailableVehicles();
    }

    private void loadAvailableVehicles() {
        try {
            String query = "SELECT id, model FROM vehicles WHERE available = 1";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            List<String> vehicleList = new ArrayList<>();
            while (rs.next()) {
                vehicleList.add(rs.getString("model"));
            }
            vehicleComboBox.getItems().setAll(vehicleList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void createBooking() {
        // Get user inputs
        String selectedVehicle = vehicleComboBox.getValue();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        String customerId = customerIdField.getText();

        // Check if inputs are valid
        if (selectedVehicle == null || startDate == null || endDate == null || customerId.isEmpty()) {
            showAlert(AlertType.ERROR, "Input Error", "Please fill in all fields.");
            return;
        }

        // Create a new booking in the database
        try {
            String query = "INSERT INTO bookings (customer_id, vehicle_id, start_date, end_date, status) " +
                    "VALUES (?, (SELECT id FROM vehicles WHERE model = ?), ?, ?, 'Pending')";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, Integer.parseInt(customerId));
            stmt.setString(2, selectedVehicle);
            stmt.setDate(3, Date.valueOf(startDate));
            stmt.setDate(4, Date.valueOf(endDate));
            int result = stmt.executeUpdate();

            if (result > 0) {
                showAlert(AlertType.INFORMATION, "Booking Created", "Your booking has been created successfully.");
            } else {
                showAlert(AlertType.ERROR, "Booking Error", "There was an error creating the booking.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Database Error", "Unable to connect to the database.");
        }
    }

    @FXML
    private void cancelBooking() {
        // Get user input
        String customerId = customerIdField.getText();
        String selectedVehicle = vehicleComboBox.getValue();

        // Check if inputs are valid
        if (selectedVehicle == null || customerId.isEmpty()) {
            showAlert(AlertType.ERROR, "Input Error", "Please fill in all fields.");
            return;
        }

        try {
            // Find the booking ID for the selected vehicle and customer
            String query = "SELECT id FROM bookings WHERE customer_id = ? AND vehicle_id = (SELECT id FROM vehicles WHERE model = ?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, Integer.parseInt(customerId));
            stmt.setString(2, selectedVehicle);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int bookingId = rs.getInt("id");

                // Delete the booking from the database
                String deleteQuery = "DELETE FROM bookings WHERE id = ?";
                PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery);
                deleteStmt.setInt(1, bookingId);
                int result = deleteStmt.executeUpdate();

                if (result > 0) {
                    showAlert(AlertType.INFORMATION, "Booking Canceled", "Your booking has been canceled successfully.");
                } else {
                    showAlert(AlertType.ERROR, "Cancellation Error", "There was an error canceling the booking.");
                }
            } else {
                showAlert(AlertType.WARNING, "No Booking Found", "No booking found for the provided details.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Database Error", "Unable to connect to the database.");
        }
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
