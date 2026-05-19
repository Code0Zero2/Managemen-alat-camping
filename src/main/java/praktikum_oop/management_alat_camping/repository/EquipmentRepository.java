/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package praktikum_oop.management_alat_camping.repository;

/**
 *
 * @author morxidia
 */

import praktikum_oop.management_alat_camping.config.DatabaseConfig;
import praktikum_oop.management_alat_camping.model.Equipment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipmentRepository {
    
    public List<Equipment> findAll() throws SQLException {
        List<Equipment> list = new ArrayList<>();
        String sql = "SELECT e.*, c.name as category_name FROM equipments e " +
                     "LEFT JOIN categories c ON e.category_id = c.id ORDER BY e.name";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Equipment eq = new Equipment();
                eq.setId(rs.getLong("id"));
                eq.setName(rs.getString("name"));
                eq.setBrand(rs.getString("brand"));
                eq.setAvailableStock(rs.getInt("available_stock"));
                eq.setPricePerDay(rs.getLong("price_per_day"));
                eq.setCondition(rs.getString("condition"));
                eq.setAddedAt(rs.getTimestamp("added_at"));
                eq.setCategoryId(rs.getLong("category_id"));
                eq.setCategoryName(rs.getString("category_name"));
                list.add(eq);
            }
        }
        return list;
    }
    
    public List<Equipment> findAvailable() throws SQLException {
        List<Equipment> list = new ArrayList<>();
        String sql = "SELECT e.*, c.name as category_name FROM equipments e " +
                     "LEFT JOIN categories c ON e.category_id = c.id " +
                     "WHERE e.available_stock > 0 AND e.condition = 'GOOD'";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Equipment eq = new Equipment();
                eq.setId(rs.getLong("id"));
                eq.setName(rs.getString("name"));
                eq.setBrand(rs.getString("brand"));
                eq.setAvailableStock(rs.getInt("available_stock"));
                eq.setPricePerDay(rs.getLong("price_per_day"));
                eq.setCondition(rs.getString("condition"));
                eq.setCategoryName(rs.getString("category_name"));
                list.add(eq);
            }
        }
        return list;
    }
    
    public void save(Equipment equipment) throws SQLException {
        String sql = "INSERT INTO equipments (name, brand, available_stock, price_per_day, condition, category_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, equipment.getName());
            pstmt.setString(2, equipment.getBrand());
            pstmt.setInt(3, equipment.getAvailableStock());
            pstmt.setLong(4, equipment.getPricePerDay());
            pstmt.setString(5, equipment.getCondition());
            pstmt.setObject(6, equipment.getCategoryId());
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) equipment.setId(rs.getLong(1));
            }
            DatabaseConfig.commit();
        }
    }
    
    public void update(Equipment equipment) throws SQLException {
        String sql = "UPDATE equipments SET name = ?, brand = ?, available_stock = ?, " +
                     "price_per_day = ?, condition = ?, category_id = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, equipment.getName());
            pstmt.setString(2, equipment.getBrand());
            pstmt.setInt(3, equipment.getAvailableStock());
            pstmt.setLong(4, equipment.getPricePerDay());
            pstmt.setString(5, equipment.getCondition());
            pstmt.setObject(6, equipment.getCategoryId());
            pstmt.setLong(7, equipment.getId());
            pstmt.executeUpdate();
            DatabaseConfig.commit();
        }
    }
    
    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM equipments WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
            DatabaseConfig.commit();
        }
    }
    
    public void updateStock(Long equipmentId, int quantityChange) throws SQLException {
        String sql = "UPDATE equipments SET available_stock = available_stock + ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, quantityChange);
            pstmt.setLong(2, equipmentId);
            pstmt.executeUpdate();
        }
    }
}
