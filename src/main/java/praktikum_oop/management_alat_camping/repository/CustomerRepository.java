/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package praktikum_oop.management_alat_camping.repository;

/**
 *
 * @author morxidia
 */
// repository/CustomerRepository.java

import praktikum_oop.management_alat_camping.config.DatabaseConfig;
import praktikum_oop.management_alat_camping.model.Customer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerRepository {
    
    public List<Customer> findAll() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT u.id, u.username, c.full_name, c.email, c.phone, c.shift, c.division_id, c.active, d.name as division_name " +
                     "FROM users u JOIN customers c ON u.id = c.user_id " +
                     "LEFT JOIN divisions d ON c.division_id = d.id " +
                     "WHERE u.active = true";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Customer c = new Customer();
                c.setUserId(rs.getLong("id"));
                c.setUsername(rs.getString("username"));
                c.setFullName(rs.getString("full_name"));
                c.setEmail(rs.getString("email"));
                c.setPhone(rs.getString("phone"));
                c.setShift(rs.getString("shift"));
                c.setDivisionId(rs.getLong("division_id"));
                c.setActive(rs.getBoolean("active"));
                c.setDivisionName(rs.getString("division_name"));
                customers.add(c);
            }
        }
        return customers;
    }
    
    public Customer findById(Long userId) throws SQLException {
        String sql = "SELECT u.id, u.username, c.full_name, c.email, c.phone, c.shift, c.division_id, c.active " +
                     "FROM users u JOIN customers c ON u.id = c.user_id WHERE u.id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Customer c = new Customer();
                    c.setUserId(rs.getLong("id"));
                    c.setUsername(rs.getString("username"));
                    c.setFullName(rs.getString("full_name"));
                    c.setEmail(rs.getString("email"));
                    c.setPhone(rs.getString("phone"));
                    c.setShift(rs.getString("shift"));
                    c.setDivisionId(rs.getLong("division_id"));
                    c.setActive(rs.getBoolean("active"));
                    return c;
                }
            }
        }
        return null;
    }
    
    public void save(Customer customer) throws SQLException {
        Connection conn = DatabaseConfig.getConnection();
        try {
            // Insert into users table
            String userSql = "INSERT INTO users (username, password) VALUES (?, ?)";
            Long userId = null;
            try (PreparedStatement pstmt = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, customer.getUsername());
                pstmt.setString(2, customer.getPassword());
                pstmt.executeUpdate();
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) userId = rs.getLong(1);
                }
            }
            
            // Insert into customers table
            if (userId != null) {
                String customerSql = "INSERT INTO customers (user_id, full_name, email, phone, shift, division_id, active) " +
                                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(customerSql)) {
                    pstmt.setLong(1, userId);
                    pstmt.setString(2, customer.getFullName());
                    pstmt.setString(3, customer.getEmail());
                    pstmt.setString(4, customer.getPhone());
                    pstmt.setString(5, customer.getShift());
                    pstmt.setObject(6, customer.getDivisionId());
                    pstmt.setBoolean(7, customer.getActive());
                    pstmt.executeUpdate();
                    customer.setUserId(userId);
                }
            }
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        }
    }
    
    public void update(Customer customer) throws SQLException {
        String sql = "UPDATE customers SET full_name = ?, email = ?, phone = ?, shift = ?, division_id = ?, active = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, customer.getFullName());
            pstmt.setString(2, customer.getEmail());
            pstmt.setString(3, customer.getPhone());
            pstmt.setString(4, customer.getShift());
            pstmt.setObject(5, customer.getDivisionId());
            pstmt.setBoolean(6, customer.getActive());
            pstmt.setLong(7, customer.getUserId());
            pstmt.executeUpdate();
            conn.commit();
        } catch (SQLException e){
            throw e;
        }
    }
}