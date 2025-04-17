package com.example.demo;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import model.UserManager;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Label loginMessageLabel;
    @FXML
    private Hyperlink registerLink;
    @FXML
    private AnchorPane rootPane; // Reference to the AnchorPane for background color change

    @FXML
    private void initialize() {
        // Create a PauseTransition to change the background color after 5 seconds
        PauseTransition pause = new PauseTransition(Duration.seconds(5));
        pause.setOnFinished(event -> {
            // Change the background color after the delay
            rootPane.setStyle("-fx-background-color: linear-gradient(to bottom, #ffeb3b, #ffee58);");
        });
        pause.play();
    }

    @FXML
    private void login(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            loginMessageLabel.setText("Please fill in both username and password.");
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");

                // ✅ Register user in UserManager (track logged-in users)
                UserManager.registerUser(username, role);  // This should be the correct method

                // Load dashboard.fxml
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/dashboard.fxml"));
                Scene scene = new Scene(loader.load());

                DashboardController controller = loader.getController();
                controller.setUserRole(role); // Pass user role to dashboard

                Stage stage = (Stage) loginButton.getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Vehicle Rental Dashboard");
                stage.show();

                System.out.println("✅ Logged in: " + username + " [" + role + "]");

            } else {
                loginMessageLabel.setText("Invalid username or password.");
            }

        } catch (Exception e) {
            loginMessageLabel.setText("Database error occurred.");
            e.printStackTrace();
        }
    }

    @FXML
    private void switchToRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/register.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) registerLink.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Register Account");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            loginMessageLabel.setText("Unable to open registration page.");
        }
    }
}
