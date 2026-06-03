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
            this.showAddCustomerDialog();
        });
        add(addBtn, BorderLayout.SOUTH);
    }
    
    private void showAddCustomerDialog() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentFrame, "Add New Customer", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField usernameField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);
        JTextField fullNameField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JTextField phoneField = new JTextField(15);

        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        dialog.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        dialog.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        dialog.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        dialog.add(fullNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        dialog.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        dialog.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        dialog.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        dialog.add(phoneField, gbc);

        JButton saveBtn = new JButton("Save Customer");
        saveBtn.addActionListener(e -> {
            try {
                Customer cust = new Customer();
                cust.setUsername(usernameField.getText());
                cust.setPassword(new String(passwordField.getPassword()));
                cust.setFullName(fullNameField.getText());
                cust.setEmail(emailField.getText());
                cust.setPhone(phoneField.getText());

                // Note: Ensure your RentalService has an addCustomer method
                service.addCustomer(cust);

                refreshData();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Customer added successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        dialog.add(saveBtn, gbc);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
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