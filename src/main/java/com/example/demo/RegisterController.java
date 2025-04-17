package com.example.demo;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader; // <-- Add this import
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;


import java.sql.*;

public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ChoiceBox<String> roleChoiceBox;
    @FXML private Label registerMessage;
    @FXML private VBox formVBox; // Reference to the VBox that contains the form

    @FXML
    public void initialize() {
        // Create a Fade Transition effect when the form loads
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), formVBox);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();

        // Populate role choice box
        roleChoiceBox.getItems().addAll("Admin", "Employee");
        roleChoiceBox.setValue("Employee");
    }

    @FXML
    private void registerUser() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String role = roleChoiceBox.getValue();

        if (username.isEmpty() || password.isEmpty()) {
            registerMessage.setText("Please fill in all fields.");
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role);
            stmt.executeUpdate();

            registerMessage.setText("Registration successful!");
            clearForm();

        } catch (SQLIntegrityConstraintViolationException e) {
            registerMessage.setText("Username already exists.");
        } catch (Exception e) {
            registerMessage.setText("Error during registration.");
            e.printStackTrace();
        }
    }

    private void clearForm() {
        usernameField.clear();
        passwordField.clear();
        roleChoiceBox.setValue("Employee");
    }

    @FXML
    private void backToLogin() {
        try {
            Stage stage = (Stage) registerMessage.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
            stage.setScene(new Scene(loader.load()));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
