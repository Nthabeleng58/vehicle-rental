package model;

import com.example.demo.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;


public class Payment {
    private int id;
    private String customerName;
    private String vehicleModel;
    private int days;
    private double total;
    private String paymentDate;
    private double additionalCharges;
    private String paymentMethod;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public double getAdditionalCharges() {
        return additionalCharges;
    }

    public void setAdditionalCharges(double additionalCharges) {
        this.additionalCharges = additionalCharges;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    // Insert payment into database
    public void insertPayment() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO payments (customer_name, vehicle_model, days, total, payment_date, additional_charges, payment_method) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, this.customerName);
                stmt.setString(2, this.vehicleModel);
                stmt.setInt(3, this.days);
                stmt.setDouble(4, this.total);
                stmt.setString(5, this.paymentDate);
                stmt.setDouble(6, this.additionalCharges);
                stmt.setString(7, this.paymentMethod);

                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Retrieve payment by ID
    public static Payment getPaymentById(int paymentId) {
        Payment payment = new Payment();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM payments WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, paymentId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        payment.setId(rs.getInt("id"));
                        payment.setCustomerName(rs.getString("customer_name"));
                        payment.setVehicleModel(rs.getString("vehicle_model"));
                        payment.setDays(rs.getInt("days"));
                        payment.setTotal(rs.getDouble("total"));
                        payment.setPaymentDate(rs.getString("payment_date"));
                        payment.setAdditionalCharges(rs.getDouble("additional_charges"));
                        payment.setPaymentMethod(rs.getString("payment_method"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return payment;
    }
}
