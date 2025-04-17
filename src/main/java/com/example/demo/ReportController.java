package com.example.demo;

import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Report;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.Map;

public class ReportController {

    @FXML private PieChart revenuePieChart;
    @FXML private PieChart vehicleCategoryPieChart;
    @FXML private BarChart<String, Number> vehicleUnavailabilityBarChart;
    @FXML private TabPane reportTabs;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;

    private final Report reportModel = new Report();

    // Initialize charts with default data
    public void initialize() {
        loadRevenueReport();
        loadAvailableVehiclesReport();
        loadVehicleCategoryDistribution();
    }

    // Load data for the revenue report pie chart
    private void loadRevenueReport() {
        revenuePieChart.getData().clear();
        Map<String, Double> revenueData = reportModel.getRevenueData();
        for (Map.Entry<String, Double> entry : revenueData.entrySet()) {
            revenuePieChart.getData().add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }
    }

    // Load data for the available vehicles report
    private void loadAvailableVehiclesReport() {
        vehicleUnavailabilityBarChart.getData().clear();
        Map<String, Integer> availability = reportModel.getAvailableVehicles();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Unavailable Vehicles");

        for (Map.Entry<String, Integer> entry : availability.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        vehicleUnavailabilityBarChart.getData().add(series);
    }

    // Load data for vehicle category distribution pie chart
    private void loadVehicleCategoryDistribution() {
        vehicleCategoryPieChart.getData().clear();
        Map<String, Integer> categoryData = reportModel.getVehicleCategoryDistribution();
        for (Map.Entry<String, Integer> entry : categoryData.entrySet()) {
            vehicleCategoryPieChart.getData().add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }
    }

    // Filter the revenue data based on the selected date range
    @FXML
    private void filterRevenueData() {
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();

        if (start == null || end == null) {
            showError("Invalid Input", "Please select both start and end dates.");
            return;
        }

        if (end.isBefore(start)) {
            showError("Invalid Range", "End date must be after or equal to start date.");
            return;
        }

        revenuePieChart.getData().clear();
        Map<String, Double> filteredData = reportModel.getRevenueByDateRange(start, end);
        for (Map.Entry<String, Double> entry : filteredData.entrySet()) {
            revenuePieChart.getData().add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }
    }

    // Export revenue data to CSV
    @FXML
    private void exportRevenueToCSV() {
        exportMapToCSV(reportModel.getRevenueData(), "Revenue Report", "revenue_report.csv", "Category,Revenue");
    }

    // Export vehicle category distribution data to CSV
    @FXML
    private void exportVehicleCategoryToCSV() {
        exportMapToCSV(reportModel.getVehicleCategoryDistribution(), "Vehicle Category Report", "vehicle_category_distribution.csv", "Category,Count");
    }

    // Export unavailable vehicles data to CSV
    @FXML
    private void exportUnavailableVehiclesToCSV() {
        exportMapToCSV(reportModel.getAvailableVehicles(), "Unavailable Vehicles Report", "unavailable_vehicles.csv", "Vehicle,Unavailable Count");
    }

    // Generic method to export a map of data to CSV
    private <K, V> void exportMapToCSV(Map<K, V> data, String title, String fileName, String header) {
        if (data.isEmpty()) {
            showError("No Data", "There is no data to export for " + title.toLowerCase() + ".");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save " + title);
        fileChooser.setInitialFileName(fileName);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));

        Stage stage = (Stage) reportTabs.getScene().getWindow();
        java.io.File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.println(header);
                for (Map.Entry<K, V> entry : data.entrySet()) {
                    writer.println(entry.getKey() + "," + entry.getValue());
                }
                showInfo("Export Successful", title + " exported successfully.");
            } catch (IOException e) {
                showError("Export Failed", "An error occurred while exporting " + title + ".");
            }
        }
    }

    // Show error dialog
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Show information dialog
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
