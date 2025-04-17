package com.example.demo;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DashboardController {

    @FXML private AnchorPane contentArea;
    @FXML private Button vehiclesButton;
    @FXML private Button customersButton;
    @FXML private Button bookingsButton;
    @FXML private Button paymentsButton;
    @FXML private Button reportsButton;
    @FXML private Button viewUsersButton;
    @FXML private Label welcomeLabel;

    private String userRole;

    public void setUserRole(String role) {
        this.userRole = role;
        welcomeLabel.setText("Welcome, " + role);
        applyRoleBasedAccess();
        loadDashboardTiles(); // load tiles on login
    }

    private void applyRoleBasedAccess() {
        if ("Admin".equalsIgnoreCase(userRole)) {
            vehiclesButton.setDisable(false);
            customersButton.setDisable(false);
            bookingsButton.setDisable(true);
            paymentsButton.setDisable(false);
            reportsButton.setDisable(false);
        } else if ("Employee".equalsIgnoreCase(userRole)) {
            vehiclesButton.setDisable(true);
            customersButton.setDisable(true);
            bookingsButton.setDisable(false);
            paymentsButton.setDisable(false);
            reportsButton.setDisable(true);
        }
    }

    public void openVehicles() {
        slideTo("/views/vehicles.fxml");
    }

    public void openCustomers() {
        slideTo("/views/customers.fxml");
    }

    public void openBookings() {
        if (!"Admin".equalsIgnoreCase(userRole)) {
            slideTo("/views/bookings.fxml");
        } else {
            showError("Access Denied", "Admins cannot access bookings.");
        }
    }

    public void openPayments() {
        slideTo("/views/payments.fxml");
    }

    public void openReports() {
        slideTo("/views/report.fxml");
    }

    public void openUserManagement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/UserManagement.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Logged-In Users");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showError("Error", "Unable to open user management screen.");
            e.printStackTrace();
        }
    }

    public void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Login");
            stage.setScene(new Scene(root));
            stage.show();
            Stage currentStage = (Stage) contentArea.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            showError("Logout Failed", "Unable to load login screen.");
            e.printStackTrace();
        }
    }

    private void slideTo(String resourcePath) {
        try {
            Parent newContent = FXMLLoader.load(getClass().getResource(resourcePath));
            newContent.translateXProperty().set(contentArea.getWidth());

            contentArea.getChildren().clear();
            contentArea.getChildren().add(newContent);

            TranslateTransition slide = new TranslateTransition(Duration.millis(400), newContent);
            slide.setFromX(contentArea.getWidth());
            slide.setToX(0);
            slide.play();
        } catch (IOException e) {
            showError("Load Error", "Unable to load: " + resourcePath);
            e.printStackTrace();
        }
    }

    private void loadDashboardTiles() {
        try {
            VBox tileBox = new VBox(20);
            tileBox.setStyle("-fx-padding: 30;");
            tileBox.getChildren().addAll(
                    createTile("Cars", "vehicles", "#FFA500"),
                    createTile("Customers", "customers", "#32CD32"),
                    createTile("Bookings", "bookings", "#9370DB")
            );

            contentArea.getChildren().clear();
            contentArea.getChildren().add(tileBox);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private VBox createTile(String title, String tableName, String color) {
        VBox tile = new VBox(10);
        tile.setStyle("-fx-background-color: " + color + "; -fx-padding: 20; -fx-background-radius: 15;");
        tile.setPrefWidth(200);
        tile.setPrefHeight(100);
        tile.setLayoutX(30);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");

        Label countLabel = new Label();
        countLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: white;");

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/vehicle_rental", "root", "56568537");
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + tableName);
            if (rs.next()) {
                countLabel.setText(String.valueOf(rs.getInt(1)));
            }

        } catch (Exception e) {
            countLabel.setText("Error");
        }

        tile.getChildren().addAll(titleLabel, countLabel);
        return tile;
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
