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
        String sql = "SELECT u.id, u.username, c.full_name, c.email, c.phone " +
                "FROM users u JOIN customers c ON u.id = c.user_id ";

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
                customers.add(c);
            }
        }
        return customers;
    }

    public Customer findById(Long userId) throws SQLException {
        String sql = "SELECT u.id, u.username, c.full_name, c.email, c.phone " +
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
                    if (rs.next())
                        userId = rs.getLong(1);
                }
            }

            // Insert into customers table
            if (userId != null) {
                String customerSql = "INSERT INTO customers (user_id, full_name, email, phone) " +
                        "VALUES (?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(customerSql)) {
                    pstmt.setLong(1, userId);
                    pstmt.setString(2, customer.getFullName());
                    pstmt.setString(3, customer.getEmail());
                    pstmt.setString(4, customer.getPhone());
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
        String userSql = "UPDATE users SET username = ? WHERE id = ?";
        String customerSql = "UPDATE customers SET full_name = ?, email = ?, phone = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 1. Update users table
                try (PreparedStatement pstmt = conn.prepareStatement(userSql)) {
                    pstmt.setString(1, customer.getUsername());
                    pstmt.setLong(2, customer.getUserId());
                    pstmt.executeUpdate();
                }
                
                // 2. Update customers table
                try (PreparedStatement pstmt = conn.prepareStatement(customerSql)) {
                    pstmt.setString(1, customer.getFullName());
                    pstmt.setString(2, customer.getEmail());
                    pstmt.setString(3, customer.getPhone());
                    pstmt.setLong(4, customer.getUserId());
                    pstmt.executeUpdate();
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public void delete(Long userId) throws SQLException {
        // Because of database relationships, deleting the User will automatically 
        // cascade and delete the Customer record if you set up 'ON DELETE CASCADE' in SQL.
        // If not, we explicitly delete from customers first, then users.
        String sqlCustomer = "DELETE FROM customers WHERE user_id = ?";
        String sqlUser = "DELETE FROM users WHERE id = ?";

        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // 1. Delete from child table (customers)
            try (PreparedStatement pstmt = conn.prepareStatement(sqlCustomer)) {
                pstmt.setLong(1, userId);
                pstmt.executeUpdate();
            }

            // 2. Delete from parent table (users)
            try (PreparedStatement pstmt = conn.prepareStatement(sqlUser)) {
                pstmt.setLong(1, userId);
                pstmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
            }
        }
    }
}