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
//             JOptionPane.showMessageDialog(this, "Add logic goes here"); 
             showAddWorkerDialog();
        });
        add(addBtn, BorderLayout.SOUTH);
    }
    
    private void showAddWorkerDialog() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentFrame, "Add New Worker", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField usernameField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);
        JTextField nameField = new JTextField(20);
        JTextField phoneField = new JTextField(15);
        JComboBox<String> shiftCombo = new JComboBox<>(new String[] { "Morning", "Evening", "Night" });

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
        dialog.add(new JLabel("Real Name:"), gbc);
        gbc.gridx = 1;
        dialog.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        dialog.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        dialog.add(phoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        dialog.add(new JLabel("Shift:"), gbc);
        gbc.gridx = 1;
        dialog.add(shiftCombo, gbc);

        JButton saveBtn = new JButton("Save Worker");
        saveBtn.addActionListener(e -> {
            try {
                Worker worker = new Worker();
                worker.setUsername(usernameField.getText());
                worker.setPassword(new String(passwordField.getPassword()));
                worker.setName(nameField.getText());
                worker.setPhone(phoneField.getText());
                worker.setShift((String) shiftCombo.getSelectedItem());
                worker.setActive(true);
                worker.setDivisionId(1L);

                service.addWorker(worker);

                refreshData();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Worker added successfully!");
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
            for (Worker w : service.getAllWorkers()) {
                model.addRow(new Object[]{w.getId(), w.getUsername(), w.getName(), w.getShift(), w.getPhone(), w.getActive()});
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
    }
}