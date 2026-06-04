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
    private JTable table; // Promoted to class variable so we can get the selected row

    public WorkerPanel(RentalService service) {
        this.service = service;
        setLayout(new BorderLayout());

        // Initialize table
        model = new DefaultTableModel(new String[]{"ID", "Username", "Name", "Shift", "Phone", "Active"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Initialize buttons in a panel
        JPanel buttonPanel = new JPanel();
        JButton addBtn = new JButton("Add Worker");
        JButton deleteBtn = new JButton("Delete Worker");

        addBtn.addActionListener(e -> showAddWorkerDialog());
        deleteBtn.addActionListener(e -> deleteWorker());

        buttonPanel.add(addBtn);
        buttonPanel.add(deleteBtn);
        add(buttonPanel, BorderLayout.SOUTH);
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
        JComboBox<String> shiftCombo = new JComboBox<>(new String[]{"Morning", "Evening", "Night"});

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

    // NEW: Delete Worker Logic
    private void deleteWorker() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a worker to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this worker?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Long id = (Long) model.getValueAt(selectedRow, 0);
                service.deleteWorker(id); // Calls the backend
                refreshData();
                JOptionPane.showMessageDialog(this, "Worker deleted successfully!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting worker (They may be tied to existing invoices/returns): " + ex.getMessage());
            }
        }
    }

    public void refreshData() {
        model.setRowCount(0);
        try {
            for (Worker w : service.getAllWorkers()) {
                model.addRow(new Object[]{w.getId(), w.getUsername(), w.getName(), w.getShift(), w.getPhone(), w.getActive()});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
