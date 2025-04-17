package com.example.demo;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;
import java.time.LocalDate;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PaymentsController {

    @FXML private TextField customerNameField;
    @FXML private TextField vehicleModelField;
    @FXML private TextField daysField;
    @FXML private TextField totalField;
    @FXML private TextField additionalChargesField;
    @FXML private ComboBox<String> paymentMethodCombo;

    private final double dailyRate = 500.0; // Example rate per day

    @FXML
    public void initialize() {
        paymentMethodCombo.getItems().addAll("Cash", "Credit Card", "Online");
        paymentMethodCombo.setValue("Cash");
    }

    @FXML
    private void generateBill() {
        try {
            int days = Integer.parseInt(daysField.getText());
            double extra = additionalChargesField.getText().isEmpty() ? 0 : Double.parseDouble(additionalChargesField.getText());

            double total = (dailyRate * days) + extra;
            totalField.setText(String.valueOf(total));

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Please enter valid numbers for days and additional charges.");
        }
    }

    @FXML
    private void payNow() {
        String name = customerNameField.getText();
        String model = vehicleModelField.getText();
        String daysStr = daysField.getText();
        String totalStr = totalField.getText();
        String additionalStr = additionalChargesField.getText();
        String method = paymentMethodCombo.getValue();

        if (name.isEmpty() || model.isEmpty() || daysStr.isEmpty() || totalStr.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Missing Fields", "Fill in all required fields.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO payments (customer_name, vehicle_model, days, total, payment_date, additional_charges, payment_method) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, name);
            stmt.setString(2, model);
            stmt.setInt(3, Integer.parseInt(daysStr));
            stmt.setDouble(4, Double.parseDouble(totalStr));
            stmt.setString(5, LocalDate.now().toString());
            stmt.setDouble(6, additionalStr.isEmpty() ? 0 : Double.parseDouble(additionalStr));
            stmt.setString(7, method);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Payment recorded successfully!");
                clearForm();
            } else {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Could not save payment.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save payment.");
        }
    }

    @FXML
    private void exportPayment() {
        String name = customerNameField.getText();
        String model = vehicleModelField.getText();
        String daysStr = daysField.getText();
        String totalStr = totalField.getText();
        String additionalStr = additionalChargesField.getText();
        String method = paymentMethodCombo.getValue();

        if (name.isEmpty() || model.isEmpty() || daysStr.isEmpty() || totalStr.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Missing Fields", "Fill in all required fields.");
            return;
        }

        try {
            File file = new File("payment_details.csv");
            FileWriter writer = new FileWriter(file, true);

            // Write CSV header if file is empty
            if (file.length() == 0) {
                writer.append("Customer Name, Vehicle Model, Days, Total, Additional Charges, Payment Method, Payment Date\n");
            }

            // Write the payment data to CSV
            writer.append(name).append(", ")
                    .append(model).append(", ")
                    .append(daysStr).append(", ")
                    .append(totalStr).append(", ")
                    .append(additionalStr.isEmpty() ? "0" : additionalStr).append(", ")
                    .append(method).append(", ")
                    .append(LocalDate.now().toString()).append("\n");

            writer.flush();
            writer.close();

            showAlert(Alert.AlertType.INFORMATION, "Export Success", "Payment details exported successfully to payment_details.csv");

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Export Error", "Failed to export payment details.");
            e.printStackTrace();
        }
    }

    private void clearForm() {
        customerNameField.clear();
        vehicleModelField.clear();
        daysField.clear();
        totalField.clear();
        additionalChargesField.clear();
        paymentMethodCombo.setValue("Cash");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
