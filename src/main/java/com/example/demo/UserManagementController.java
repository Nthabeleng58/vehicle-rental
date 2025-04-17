package com.example.demo;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.UserManager;

public class UserManagementController {

    @FXML
    private TableView<UserManager> userTableView;

    @FXML
    private TableColumn<UserManager, String> usernameColumn;

    @FXML
    private TableColumn<UserManager, String> roleColumn;

    // Initialize the controller by setting up the table columns and populating the table
    public void initialize() {
        // Set up the columns to display username and role
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        // Update the table with registered and logged-in users
        updateUserTable();
    }

    // Method to update the table view with all registered and logged-in users
    public void updateUserTable() {
        userTableView.getItems().clear();  // Clear existing items

        // Add all registered users
        userTableView.getItems().addAll(UserManager.getRegisteredUsers());

        // Add all logged-in users
        userTableView.getItems().addAll(UserManager.getLoggedInUsers());
    }

    // Method to refresh the table of users
    @FXML
    private void refreshUserTable() {
        updateUserTable();
    }
}
