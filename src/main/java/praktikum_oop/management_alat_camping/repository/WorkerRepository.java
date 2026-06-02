/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package praktikum_oop.management_alat_camping.repository;

/**
 *
 * @author morxidia
 */
import praktikum_oop.management_alat_camping.model.Worker;
import praktikum_oop.management_alat_camping.config.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WorkerRepository {
    private Worker mapResultSetToWorker(ResultSet rs) throws SQLException {
        Worker worker = new Worker();
        
        // From users table
        worker.setId(rs.getLong("id"));
        worker.setUsername(rs.getString("username"));
        worker.setPassword(rs.getString("password"));
        
        // From workers table
        worker.setName(rs.getString("name"));
        worker.setPhone(rs.getString("phone"));
        worker.setShift(rs.getString("shift"));
        worker.setDivisionId(rs.getLong("division_id"));
        worker.setActive(rs.getBoolean("active"));
        
        // From divisions table (Left Join)
        worker.setDivisionName(rs.getString("division_name"));
        
        return worker;
    }

    /**
     * Retrieves all workers from the database.
     */
    public List<Worker> getAllWorkers() throws SQLException {
        List<Worker> workers = new ArrayList<>();
        String sql = "SELECT u.id, u.username, u.password, w.name, w.phone, w.shift, w.division_id, w.active, d.name AS division_name " +
                     "FROM workers w " +
                     "JOIN users u ON w.user_id = u.id " +
                     "LEFT JOIN divisions d ON w.division_id = d.id";

        // Replace with your actual connection getter: Connection conn = DatabaseConnection.getConnection();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                workers.add(mapResultSetToWorker(rs));
            }
        }
        return workers;
    }

    /**
     * Finds a specific worker by their username (Useful for Login).
     */
    public Worker findByUsername(String username) throws SQLException {
        String sql = "SELECT u.id, u.username, u.password, w.name, w.phone, w.shift, w.division_id, w.active, d.name AS division_name " +
                     "FROM workers w " +
                     "JOIN users u ON w.user_id = u.id " +
                     "LEFT JOIN divisions d ON w.division_id = d.id " +
                     "WHERE u.username = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToWorker(rs);
                }
            }
        }
        return null;
    }

    /**
     * Creates a new worker. Requires a transaction because it inserts into two tables.
     */
    public boolean createWorker(Worker worker) throws SQLException {
        Connection conn = null;
        try {
            // conn = DatabaseConnection.getConnection();
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // 1. Insert into users table
            String insertUser = "INSERT INTO users (username, password) VALUES (?, ?)";
            try (PreparedStatement psUser = conn.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS)) {
                psUser.setString(1, worker.getUsername());
                psUser.setString(2, worker.getPassword()); // In production, this should be hashed!
                psUser.executeUpdate();

                // Get the generated user ID
                try (ResultSet rs = psUser.getGeneratedKeys()) {
                    if (rs.next()) {
                        worker.setId(rs.getLong(1));
                    } else {
                        throw new SQLException("Creating user failed, no ID obtained.");
                    }
                }
            }

            // 2. Insert into workers table using the new User ID
            String insertWorker = "INSERT INTO workers (user_id, name, phone, shift, division_id, active) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement psWorker = conn.prepareStatement(insertWorker)) {
                psWorker.setLong(1, worker.getId());
                psWorker.setString(2, worker.getName());
                psWorker.setString(3, worker.getPhone());
                psWorker.setString(4, worker.getShift());
                psWorker.setLong(5, worker.getDivisionId());
                psWorker.setString(6, worker.getActive() ? "true" : "false"); // Convert boolean back to string
                psWorker.executeUpdate();
            }

            conn.commit(); // Save everything
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback(); // Undo everything if it fails
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    /**
     * Updates an existing worker's details.
     */
    public boolean updateWorker(Worker worker) throws SQLException {
        String sql = "UPDATE workers SET name = ?, phone = ?, shift = ?, division_id = ?, active = ? WHERE user_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, worker.getName());
            ps.setString(2, worker.getPhone());
            ps.setString(3, worker.getShift());
            ps.setLong(4, worker.getDivisionId());
            ps.setString(5, worker.getActive() ? "true" : "false");
            ps.setLong(6, worker.getId());
            
            return ps.executeUpdate() > 0;
        }
    }
}