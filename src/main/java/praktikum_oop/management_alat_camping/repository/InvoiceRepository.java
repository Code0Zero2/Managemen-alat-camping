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
import praktikum_oop.management_alat_camping.model.Invoice;
import praktikum_oop.management_alat_camping.model.InvoiceDetail;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvoiceRepository {
    private EquipmentRepository equipmentRepo = new EquipmentRepository();
    
    public Long createInvoice(Invoice invoice) throws SQLException {
        Connection conn = DatabaseConfig.getConnection();
        Long invoiceId = null;
        
        String sql = "INSERT INTO invoices (user_id, worker_id, total_amount, payment_status, rent_date, expected_return_date, returned) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setObject(1, invoice.getUserId());
            pstmt.setObject(2, invoice.getWorkerId());
            pstmt.setLong(3, invoice.getTotalAmount());
            pstmt.setString(4, invoice.getPaymentStatus());
            pstmt.setDate(5, invoice.getRentDate());
            pstmt.setDate(6, invoice.getExpectedReturnDate());
            pstmt.setBoolean(7, invoice.getReturned());
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) invoiceId = rs.getLong(1);
            }
        }
        
        if (invoiceId != null) {
            String detailSql = "INSERT INTO invoice_details (invoice_id, equipments_id, quantity, time_period_in_day, amount) " +
                               "VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(detailSql)) {
                for (InvoiceDetail detail : invoice.getDetails()) {
                    pstmt.setLong(1, invoiceId);
                    pstmt.setLong(2, detail.getEquipmentId());
                    pstmt.setInt(3, detail.getQuantity());
                    pstmt.setInt(4, detail.getTimePeriodInDay());
                    pstmt.setLong(5, detail.getAmount());
                    pstmt.addBatch();
                    
                    // Update stock
                    equipmentRepo.updateStock(detail.getEquipmentId(), -detail.getQuantity());
                }
                pstmt.executeBatch();
            }
            conn.commit();
        }
        return invoiceId;
    }
    
    public List<Invoice> findAll() throws SQLException {
        List<Invoice> invoices = new ArrayList<>();
        String sql = "SELECT i.*, c.full_name as customer_name, w.name as worker_name " +
                     "FROM invoices i " +
                     "LEFT JOIN customers c ON i.user_id = c.user_id " +
                     "LEFT JOIN workers w ON i.worker_id = w.user_id " +
                     "ORDER BY i.id DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Invoice inv = new Invoice();
                inv.setId(rs.getLong("id"));
                inv.setUserId(rs.getLong("user_id"));
                inv.setWorkerId(rs.getLong("worker_id"));
                inv.setTotalAmount(rs.getLong("total_amount"));
                inv.setPaymentStatus(rs.getString("payment_status"));
                inv.setRentDate(rs.getDate("rent_date"));
                inv.setExpectedReturnDate(rs.getDate("expected_return_date"));
                inv.setReturned(rs.getBoolean("returned"));
                inv.setCustomerName(rs.getString("customer_name"));
                inv.setWorkerName(rs.getString("worker_name"));
                invoices.add(inv);
            }
        }
        return invoices;
    }
    
    public List<Invoice> findUnreturned() throws SQLException {
        List<Invoice> invoices = new ArrayList<>();
        String sql = "SELECT i.*, c.full_name as customer_name " +
                     "FROM invoices i " +
                     "LEFT JOIN customers c ON i.user_id = c.user_id " +
                     "WHERE i.returned = FALSE ORDER BY i.id DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Invoice inv = new Invoice();
                inv.setId(rs.getLong("id"));
                inv.setUserId(rs.getLong("user_id"));
                inv.setTotalAmount(rs.getLong("total_amount"));
                inv.setRentDate(rs.getDate("rent_date"));
                inv.setExpectedReturnDate(rs.getDate("expected_return_date"));
                inv.setReturned(rs.getBoolean("returned"));
                inv.setCustomerName(rs.getString("customer_name"));
                invoices.add(inv);
            }
        }
        return invoices;
    }
    
    public List<InvoiceDetail> findDetailsByInvoiceId(Long invoiceId) throws SQLException {
        List<InvoiceDetail> details = new ArrayList<>();
        String sql = "SELECT id.*, e.name as equipment_name, e.brand as equipment_brand " +
                     "FROM invoice_details id " +
                     "JOIN equipments e ON id.equipments_id = e.id " +
                     "WHERE id.invoice_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, invoiceId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    InvoiceDetail detail = new InvoiceDetail();
                    detail.setEquipmentId(rs.getLong("equipments_id"));
                    detail.setQuantity(rs.getInt("quantity"));
                    detail.setTimePeriodInDay(rs.getInt("time_period_in_day"));
                    detail.setAmount(rs.getLong("amount"));
                    detail.setEquipmentName(rs.getString("equipment_name"));
                    detail.setEquipmentBrand(rs.getString("equipment_brand"));
                    details.add(detail);
                }
            }
        }
        return details;
    }
    
    public void markAsReturned(Long invoiceId) throws SQLException {
        String sql = "UPDATE invoices SET returned = TRUE WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, invoiceId);
            pstmt.executeUpdate();
            conn.commit();
        }
    }
    
//    add update invoice
    public void updatePaymentStatus(Long invoiceId, String status) throws SQLException {
        String sql = "UPDATE invoices SET payment_status = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setLong(2, invoiceId);

            pstmt.executeUpdate();
            conn.commit();
        }
    }
}