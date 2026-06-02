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
import praktikum_oop.management_alat_camping.model.Return;
import praktikum_oop.management_alat_camping.model.ReturnDetail;
import praktikum_oop.management_alat_camping.model.Penalty;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReturnRepository {
    private EquipmentRepository equipmentRepo = new EquipmentRepository();
    private InvoiceRepository invoiceRepo = new InvoiceRepository();
    
    public Long processReturn(Return returnObj) throws SQLException {
        Connection conn = DatabaseConfig.getConnection();
        Long returnId = null;
        
        try {
            // Turn off auto-commit so we can group everything into one safe transaction
            conn.setAutoCommit(false); 
            
            // --- A. Insert into returns ---
            String sql = "INSERT INTO `returns` (return_date, worker_id, invoice_id) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setTimestamp(1, returnObj.getReturnDate());
                pstmt.setLong(2, returnObj.getWorkerId());
                pstmt.setLong(3, returnObj.getInvoiceId());
                pstmt.executeUpdate();
                
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) returnId = rs.getLong(1);
                }
            }
            
            if (returnId != null) {
                int totalUnreturned = 0;
                
                // --- B. Process Each Detail ---
                for (ReturnDetail detail : returnObj.getDetails()) {
                    detail.setReturnsId(returnId);
                    
                    // 1. Save Return Detail (Passing the shared connection)
                    Long returnDetailId = saveReturnDetail(conn, detail);
                    
                    // 2. Update Stock (Doing it directly here with the shared connection)
                    if (detail.getQuantityReturned() > 0) {
                        String stockSql = "UPDATE equipments SET available_stock = available_stock + ? WHERE id = ?";
                        try (PreparedStatement stockStmt = conn.prepareStatement(stockSql)) {
                            stockStmt.setInt(1, detail.getQuantityReturned());
                            stockStmt.setLong(2, detail.getEquipmentId());
                            stockStmt.executeUpdate();
                        }
                    }
                    
                    // 3. Calculate and Save Penalties
                    totalUnreturned += detail.getQuantityLost() + detail.getQuantityDamaged();
                    if ((detail.getQuantityLost() > 0 || detail.getQuantityDamaged() > 0) && returnDetailId != null) {
                        if (detail.getQuantityLost() > 0) {
                            savePenalty(conn, returnDetailId, "Lost Equipment", detail.getQuantityLost() * 500000L);
                        }
                        if (detail.getQuantityDamaged() > 0) {
                            savePenalty(conn, returnDetailId, "Damaged Equipment", detail.getQuantityDamaged() * 250000L);
                        }
                    }
                }
                
                // --- C. Mark Invoice as Returned ---
                if (totalUnreturned == 0) {
                    String updateInvoiceSql = "UPDATE invoices SET returned = TRUE WHERE id = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(updateInvoiceSql)) {
                        pstmt.setLong(1, returnObj.getInvoiceId());
                        pstmt.executeUpdate();
                    }
                }
                
                // If everything above succeeded, COMMIT the data to the database!
                conn.commit(); 
            }
            return returnId;
            
        } catch (SQLException e) {
            // If ANYTHING fails, undo everything so we don't get corrupted data
            conn.rollback(); 
            throw e;
        } finally {
            // Always clean up our connection
            conn.setAutoCommit(true);
            conn.close(); 
        }
    }
    
    private Long saveReturnDetail(Connection conn, ReturnDetail detail) throws SQLException {
        String sql = "INSERT INTO returns_detail (returns_id, equipment_id, quantity_returned, quantity_lost, quantity_damaged) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setLong(1, detail.getReturnsId());
            pstmt.setLong(2, detail.getEquipmentId());
            pstmt.setInt(3, detail.getQuantityReturned());
            pstmt.setInt(4, detail.getQuantityLost());
            pstmt.setInt(5, detail.getQuantityDamaged());
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        return null;
    }
    
    private void savePenalty(Connection conn, Long returnDetailId, String name, Long fineAmount) throws SQLException {
        String sql = "INSERT INTO penalties (returns_detail_id, name, fine) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, returnDetailId);
            pstmt.setString(2, name);
            pstmt.setLong(3, fineAmount);
            pstmt.executeUpdate();
        }
    }
    
    public List<Return> findAll() throws SQLException {
        List<Return> returns = new ArrayList<>();
        String sql ="SELECT r.*, w.name as worker_name, i.id as invoice_id "
                    + "FROM `returns` r "
                    + // Added backticks here
                    "LEFT JOIN workers w ON r.worker_id = w.user_id "
                    + "LEFT JOIN invoices i ON r.invoice_id = i.id "
                    + // Assuming your link column is invoice_id
                    "ORDER BY r.return_date DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Return ret = new Return();
                ret.setId(rs.getLong("id"));
                ret.setReturnDate(rs.getTimestamp("return_date"));
                ret.setWorkerId(rs.getLong("worker_id"));
                ret.setInvoiceId(rs.getLong("invoice_id"));
                ret.setWorkerName(rs.getString("worker_name"));
                returns.add(ret);
            }
        }
        return returns;
    }
}
