/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package praktikum_oop.management_alat_camping.view;

/**
 *
 * @author morxidia
 */

import praktikum_oop.management_alat_camping.model.Customer;
import praktikum_oop.management_alat_camping.service.RentalService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class CustomerPanel extends JPanel {
    private RentalService service;
    private DefaultTableModel model;
    
    public CustomerPanel(RentalService service) {
        this.service = service;
        setLayout(new BorderLayout());
        model = new DefaultTableModel(new String[]{"ID", "Username", "Full Name", "Email", "Phone"}, 0);
        add(new JScrollPane(new JTable(model)), BorderLayout.CENTER);
        
        JButton addBtn = new JButton("Add Customer");
        addBtn.addActionListener(e -> {
            // Re-use your showAddCustomerDialog() logic here!
            JOptionPane.showMessageDialog(this, "Add logic goes here"); 
        });
        add(addBtn, BorderLayout.SOUTH);
    }
    
    public void refreshData() {
        model.setRowCount(0);
        try {
            for (Customer c : service.getAllCustomers()) {
                model.addRow(new Object[]{c.getUserId(), c.getUsername(), c.getFullName(), c.getEmail(), c.getPhone()});
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
    }
}