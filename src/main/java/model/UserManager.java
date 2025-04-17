package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.List;

public class UserManager {

    private static final List<UserManager> registeredUsers = new ArrayList<>();
    private static final List<UserManager> loggedInUsers = new ArrayList<>();

    private final StringProperty username = new SimpleStringProperty();
    private final StringProperty role = new SimpleStringProperty();

    // Constructor to initialize a user
    public UserManager(String username, String role) {
        this.username.set(username);
        this.role.set(role);
    }

    // Getter for username
    public String getUsername() {
        return username.get();
    }

    // Setter for username
    public void setUsername(String username) {
        this.username.set(username);
    }

    // Getter for role
    public String getRole() {
        return role.get();
    }

    // Setter for role
    public void setRole(String role) {
        this.role.set(role);
    }

    // Add a user to the registered list
    public static void registerUser(String username, String role) {
        registeredUsers.add(new UserManager(username, role));
    }

    // Add a user to the logged-in list
    public static void loginUser(String username, String role) {
        loggedInUsers.add(new UserManager(username, role));
    }

    // Get all registered users
    public static List<UserManager> getRegisteredUsers() {
        return new ArrayList<>(registeredUsers);
    }

    // Get all logged-in users
    public static List<UserManager> getLoggedInUsers() {
        return new ArrayList<>(loggedInUsers);
    }

    // Override toString to return user information as "username (role)"
    @Override
    public String toString() {
        return username.get() + " (" + role.get() + ")";
    }

    // JavaFX Property Getter for username
    public StringProperty usernameProperty() {
        return username;
    }

    // JavaFX Property Getter for role
    public StringProperty roleProperty() {
        return role;
    }
}
