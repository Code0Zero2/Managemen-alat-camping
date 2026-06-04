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
    private JTable table; // Promoted to class variable so we can get the selected row

    public CustomerPanel(RentalService service) {
        this.service = service;
        setLayout(new BorderLayout());

        // Initialize table
        model = new DefaultTableModel(new String[]{"ID", "Username", "Full Name", "Email", "Phone"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Initialize buttons in a panel
        JPanel buttonPanel = new JPanel();
        JButton addBtn = new JButton("Add Customer");
        JButton deleteBtn = new JButton("Delete Customer");

        addBtn.addActionListener(e -> this.showAddCustomerDialog());
        deleteBtn.addActionListener(e -> deleteCustomer());

        buttonPanel.add(addBtn);
        buttonPanel.add(deleteBtn);
        add(buttonPanel, BorderLayout.SOUTH);
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

    // NEW: Delete Customer Logic
    private void deleteCustomer() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this customer? All their related data might be affected.", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Long id = (Long) model.getValueAt(selectedRow, 0);
                service.deleteCustomer(id); // Calls the backend
                refreshData();
                JOptionPane.showMessageDialog(this, "Customer deleted successfully!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting customer: " + ex.getMessage());
            }
        }
    }

    public void refreshData() {
        model.setRowCount(0);
        try {
            for (Customer c : service.getAllCustomers()) {
                model.addRow(new Object[]{c.getUserId(), c.getUsername(), c.getFullName(), c.getEmail(), c.getPhone()});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
