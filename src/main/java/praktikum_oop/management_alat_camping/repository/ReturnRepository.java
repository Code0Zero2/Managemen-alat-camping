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
        
        String sql = "INSERT INTO returns (return_date, worker_id, invoice_id) VALUES (?, ?, ?)";
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
            
            for (ReturnDetail detail : returnObj.getDetails()) {
                detail.setReturnsId(returnId);
                Long returnDetailId = saveReturnDetail(detail);
                
                // Update stock (only add back returned items)
                if (detail.getQuantityReturned() > 0) {
                    equipmentRepo.updateStock(detail.getEquipmentId(), detail.getQuantityReturned());
                }
                
                // Calculate penalties
                totalUnreturned += detail.getQuantityLost() + detail.getQuantityDamaged();
                
                if ((detail.getQuantityLost() > 0 || detail.getQuantityDamaged() > 0) && returnDetailId != null) {
                    if (detail.getQuantityLost() > 0) {
                        Penalty penalty = new Penalty("Lost Equipment", detail.getQuantityLost() * 500000L);
                        savePenalty(returnDetailId, penalty);
                    }
                    if (detail.getQuantityDamaged() > 0) {
                        Penalty penalty = new Penalty("Damaged Equipment", detail.getQuantityDamaged() * 250000L);
                        savePenalty(returnDetailId, penalty);
                    }
                }
            }
            
            // Mark invoice as returned if all items are returned
            if (totalUnreturned == 0) {
                invoiceRepo.markAsReturned(returnObj.getInvoiceId());
            }
            
            DatabaseConfig.commit();
        }
        return returnId;
    }
    
    private Long saveReturnDetail(ReturnDetail detail) throws SQLException {
        String sql = "INSERT INTO returns_detail (returns_id, equipment_id, quantity_returned, quantity_lost, quantity_damaged) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
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
    
    private void savePenalty(Long returnDetailId, Penalty penalty) throws SQLException {
        String sql = "INSERT INTO penalties (returns_detail_id, name, fine) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, returnDetailId);
            pstmt.setString(2, penalty.getName());
            pstmt.setLong(3, penalty.getFine());
            pstmt.executeUpdate();
        }
    }
    
    public List<Return> findAll() throws SQLException {
        List<Return> returns = new ArrayList<>();
        String sql = "SELECT r.*, w.name as worker_name, i.id as invoice_id " +
                     "FROM returns r " +
                     "LEFT JOIN workers w ON r.worker_id = w.user_id " +
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
