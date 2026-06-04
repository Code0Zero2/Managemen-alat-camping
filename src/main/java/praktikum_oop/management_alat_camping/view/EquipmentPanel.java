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
import java.util.HashMap;
import java.util.Map;
import praktikum_oop.management_alat_camping.model.Category;

public class EquipmentPanel extends JPanel {
    private RentalService service;
    private DefaultTableModel model;
    private JTable table;
    
    public EquipmentPanel(RentalService service) {
        this.service = service;
        setLayout(new BorderLayout());
        
        // 1. Initialize table model and make it read-only
        model = new DefaultTableModel(new String[]{"ID", "Name", "Brand", "Category", "Stock", "Price", "Condition"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);
        
        // 2. Initialize buttons
        JPanel buttonPanel = new JPanel();
        JButton addBtn = new JButton("Add Equipment");
        JButton editBtn = new JButton("Edit Equipment");
        JButton deleteBtn = new JButton("Delete Equipment");
        JButton refreshBtn = new JButton("Refresh");
        
        addBtn.addActionListener(e -> showAddEquipmentDialog());
        editBtn.addActionListener(e -> showEditEquipmentDialog());
        deleteBtn.addActionListener(e -> deleteEquipment());
        refreshBtn.addActionListener(e -> refreshData());
        
        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    public void refreshData() {
        model.setRowCount(0);
        try {
            for (Equipment eq : service.getAllEquipment()) {
                model.addRow(new Object[]{
                    eq.getId(), eq.getName(), eq.getBrand(), eq.getCategoryName(), 
                    eq.getAvailableStock(), eq.getPricePerDay(), eq.getCondition()
                });
            }
        } catch (SQLException ex) { 
            ex.printStackTrace(); 
        }
    }

    private void showAddEquipmentDialog() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentFrame, "Add Equipment", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField nameField = new JTextField(20);
        JTextField brandField = new JTextField(20);
        JTextField stockField = new JTextField(10);
        JTextField priceField = new JTextField(10);
        JComboBox<String> conditionCombo = new JComboBox<>(new String[]{"GOOD", "DAMAGED", "MAINTENANCE"});
        
        JComboBox<String> categoryCombo = new JComboBox<>();
        Map<String, Long> categoryMap = new HashMap<>();

        try {
            for (Category c : service.getAllCategories()) {
                categoryCombo.addItem(c.getName());
                categoryMap.put(c.getName(), c.getId());
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(dialog, "Failed to load categories");
        }

        gbc.gridx = 0; gbc.gridy = 0; dialog.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; dialog.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; dialog.add(new JLabel("Brand:"), gbc);
        gbc.gridx = 1; dialog.add(brandField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; dialog.add(new JLabel("Stock:"), gbc);
        gbc.gridx = 1; dialog.add(stockField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; dialog.add(new JLabel("Price per Day:"), gbc);
        gbc.gridx = 1; dialog.add(priceField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; dialog.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1; dialog.add(categoryCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 5; dialog.add(new JLabel("Condition:"), gbc);
        gbc.gridx = 1; dialog.add(conditionCombo, gbc);

        JButton saveBtn = new JButton("Save");
        saveBtn.addActionListener(e -> {
            try {
                Equipment eq = new Equipment();
                eq.setName(nameField.getText());
                eq.setBrand(brandField.getText());
                eq.setAvailableStock(Integer.parseInt(stockField.getText()));
                eq.setPricePerDay(Long.parseLong(priceField.getText()));
                eq.setCondition((String) conditionCombo.getSelectedItem());
                
                String selectedCategory = (String) categoryCombo.getSelectedItem();
                eq.setCategoryId(categoryMap.get(selectedCategory));

                service.addEquipment(eq);
                refreshData();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Equipment added successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2; 
        dialog.add(saveBtn, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showEditEquipmentDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an equipment to edit.");
            return;
        }

        Long id = (Long) model.getValueAt(selectedRow, 0);

        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentFrame, "Edit Equipment", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField nameField = new JTextField((String) model.getValueAt(selectedRow, 1), 20);
        JTextField brandField = new JTextField((String) model.getValueAt(selectedRow, 2), 20);
        JTextField stockField = new JTextField(String.valueOf(model.getValueAt(selectedRow, 4)), 10);
        JTextField priceField = new JTextField(String.valueOf(model.getValueAt(selectedRow, 5)), 10);
        
        JComboBox<String> conditionCombo = new JComboBox<>(new String[]{"GOOD", "DAMAGED", "MAINTENANCE"});
        conditionCombo.setSelectedItem(model.getValueAt(selectedRow, 6));
        
        JComboBox<String> categoryCombo = new JComboBox<>();
        Map<String, Long> categoryMap = new HashMap<>();

        try {
            for (Category c : service.getAllCategories()) {
                categoryCombo.addItem(c.getName());
                categoryMap.put(c.getName(), c.getId());
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(dialog, "Failed to load categories");
        }

        String currentCategory = (String) model.getValueAt(selectedRow, 3);
        if (currentCategory != null) {
            categoryCombo.setSelectedItem(currentCategory);
        }

        gbc.gridx = 0; gbc.gridy = 0; dialog.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; dialog.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; dialog.add(new JLabel("Brand:"), gbc);
        gbc.gridx = 1; dialog.add(brandField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; dialog.add(new JLabel("Stock:"), gbc);
        gbc.gridx = 1; dialog.add(stockField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; dialog.add(new JLabel("Price per Day:"), gbc);
        gbc.gridx = 1; dialog.add(priceField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; dialog.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1; dialog.add(categoryCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 5; dialog.add(new JLabel("Condition:"), gbc);
        gbc.gridx = 1; dialog.add(conditionCombo, gbc);

        JButton saveBtn = new JButton("Save");
        saveBtn.addActionListener(e -> {
            try {
                Equipment eq = new Equipment();
                eq.setId(id);
                eq.setName(nameField.getText());
                eq.setBrand(brandField.getText());
                eq.setAvailableStock(Integer.parseInt(stockField.getText()));
                eq.setPricePerDay(Long.parseLong(priceField.getText()));
                eq.setCondition((String) conditionCombo.getSelectedItem());
                
                String selectedCategory = (String) categoryCombo.getSelectedItem();
                if (selectedCategory != null) {
                    eq.setCategoryId(categoryMap.get(selectedCategory));
                }

                service.updateEquipment(eq);
                refreshData();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Equipment updated successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2; 
        dialog.add(saveBtn, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void deleteEquipment() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an equipment to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this equipment?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Long id = (Long) model.getValueAt(selectedRow, 0);
                service.deleteEquipment(id);
                refreshData();
                JOptionPane.showMessageDialog(this, "Equipment deleted!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }
}