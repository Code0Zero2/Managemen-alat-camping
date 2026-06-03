/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package praktikum_oop.management_alat_camping.view;

/**
 *
 * @author morxidia
 */

import praktikum_oop.management_alat_camping.model.Worker;
import praktikum_oop.management_alat_camping.service.RentalService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class WorkerPanel extends JPanel {
    private RentalService service;
    private DefaultTableModel model;
    
    public WorkerPanel(RentalService service) {
        this.service = service;
        setLayout(new BorderLayout());
        model = new DefaultTableModel(new String[]{"ID", "Username", "Name", "Shift", "Phone", "Active"}, 0);
        add(new JScrollPane(new JTable(model)), BorderLayout.CENTER);
        
        JButton addBtn = new JButton("Add Worker");
        addBtn.addActionListener(e -> {
            // Re-use your showAddWorkerDialog() logic here!
             JOptionPane.showMessageDialog(this, "Add logic goes here"); 
        });
        add(addBtn, BorderLayout.SOUTH);
    }
    
    public void refreshData() {
        model.setRowCount(0);
        try {
            for (Worker w : service.getAllWorkers()) {
                model.addRow(new Object[]{w.getId(), w.getUsername(), w.getName(), w.getShift(), w.getPhone(), w.getActive()});
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
    }
}