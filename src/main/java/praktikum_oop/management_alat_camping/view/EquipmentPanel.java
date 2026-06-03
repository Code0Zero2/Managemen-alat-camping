/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package praktikum_oop.management_alat_camping.view;

/**
 *
 * @author morxidia
 */

import praktikum_oop.management_alat_camping.model.Equipment;
import praktikum_oop.management_alat_camping.service.RentalService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class EquipmentPanel extends JPanel {
    private RentalService service;
    private DefaultTableModel model;
    
    public EquipmentPanel(RentalService service) {
        this.service = service;
        setLayout(new BorderLayout());
        model = new DefaultTableModel(new String[]{"ID", "Name", "Brand", "Category", "Stock", "Price", "Condition"}, 0);
        add(new JScrollPane(new JTable(model)), BorderLayout.CENTER);
        
        // Add your buttons (Add, Edit, Delete) to the SOUTH panel here just like before
        // ... (Code omitted for brevity, you can reuse your exact dialog methods here!)
    }
    
    public void refreshData() {
        model.setRowCount(0);
        try {
            for (Equipment eq : service.getAllEquipment()) {
                model.addRow(new Object[]{eq.getId(), eq.getName(), eq.getBrand(), eq.getCategoryName(), eq.getAvailableStock(), eq.getPricePerDay(), eq.getCondition()});
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
    }
}