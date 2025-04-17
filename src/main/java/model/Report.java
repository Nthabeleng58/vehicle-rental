package model;

import com.example.demo.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public class Report {

    public Map<String, Double> getRevenueData() {
        String sql = "SELECT payment_method, SUM(amount) AS total FROM payments GROUP BY payment_method";
        return fetchDoubleMap(sql);
    }

    public Map<String, Integer> getAvailableVehicles() {
        Map<String, Integer> data = new LinkedHashMap<>();
        String sql = "SELECT category, COUNT(*) AS count FROM vehicles WHERE available = 'Available' GROUP BY category";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                data.put(rs.getString("category"), rs.getInt("count"));
            }
        } catch (Exception e) {
            System.err.println("Error fetching available vehicles:");
            e.printStackTrace();
        }
        return data;
    }

    public Map<String, Integer> getVehicleCategoryDistribution() {
        Map<String, Integer> data = new LinkedHashMap<>();
        String sql = "SELECT category, COUNT(*) AS count FROM vehicles GROUP BY category";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                data.put(rs.getString("category"), rs.getInt("count"));
            }
        } catch (Exception e) {
            System.err.println("Error fetching vehicle category distribution:");
            e.printStackTrace();
        }
        return data;
    }

    public Map<String, Double> getRevenueByDateRange(LocalDate startDate, LocalDate endDate) {
        Map<String, Double> data = new LinkedHashMap<>();
        String sql = "SELECT payment_method, SUM(amount) AS total FROM payments " +
                "WHERE payment_date BETWEEN ? AND ? GROUP BY payment_method";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(startDate));
            ps.setDate(2, java.sql.Date.valueOf(endDate));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                data.put(rs.getString("payment_method"), rs.getDouble("total"));
            }
        } catch (Exception e) {
            System.err.println("Error fetching revenue by date range:");
            e.printStackTrace();
        }
        return data;
    }

    private Map<String, Double> fetchDoubleMap(String sql) {
        Map<String, Double> data = new LinkedHashMap<>();
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                data.put(rs.getString(1), rs.getDouble(2));
            }
        } catch (Exception e) {
            System.err.println("Error executing SQL: " + sql);
            e.printStackTrace();
        }
        return data;
    }
}
