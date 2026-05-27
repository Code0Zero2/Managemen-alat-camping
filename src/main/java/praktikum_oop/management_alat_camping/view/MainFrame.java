/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package praktikum_oop.management_alat_camping.view;

/**
 *
 * @author morxidia
 */

import praktikum_oop.management_alat_camping.model.*;
import praktikum_oop.management_alat_camping.service.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

import java.util.List;

public class MainFrame extends JFrame {

    private RentalService rentalService;
    private ReturnService returnService;
    private JTabbedPane tabbedPane;
    private JTable equipmentTable;
    private JTable invoiceTable;
    private DefaultTableModel equipmentTableModel;
    private DefaultTableModel invoiceTableModel;
    private Long currentWorkerId = 2L; // Demo worker ID

    public MainFrame() {
        rentalService = new RentalService();
        returnService = new ReturnService();
        initUI();
        loadEquipmentData();
        loadInvoiceData();
        setVisible(true);
    }

    private void initUI() {
        setTitle("Camping Equipment Rental Management System");
        setSize(1300, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Equipment Management", createEquipmentPanel());
        tabbedPane.addTab("Rental Management", createRentalPanel());
        tabbedPane.addTab("Process Returns", createReturnPanel());

        add(tabbedPane);
    }

    private JPanel createEquipmentPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {"ID", "Name", "Brand", "Category", "Available Stock", "Price/Day", "Condition"};
        equipmentTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        equipmentTable = new JTable(equipmentTableModel);
        JScrollPane scrollPane = new JScrollPane(equipmentTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addBtn = new JButton("Add Equipment");
        JButton editBtn = new JButton("Edit Equipment");
        JButton deleteBtn = new JButton("Delete Equipment");
        JButton refreshBtn = new JButton("Refresh");

        addBtn.addActionListener(e -> showAddEquipmentDialog());
        editBtn.addActionListener(e -> showEditEquipmentDialog());
        deleteBtn.addActionListener(e -> deleteEquipment());
        refreshBtn.addActionListener(e -> loadEquipmentData());

        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void showAddEquipmentDialog() {
        JDialog dialog = new JDialog(this, "Add Equipment", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField nameField = new JTextField(20);
        JTextField brandField = new JTextField(20);
        JTextField stockField = new JTextField(10);
        JTextField priceField = new JTextField(10);
        JComboBox<String> conditionCombo = new JComboBox<>(new String[]{"GOOD", "DAMAGED", "MAINTENANCE"});
        JComboBox<String> categoryCombo =
            new JComboBox<>();

        Map<String, Long> categoryMap =
            new HashMap<>();
        
        try {
            for (Category c : rentalService.getAllCategories()) {
                categoryCombo.addItem(c.getName());
                categoryMap.put( c.getName(), c.getId());
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                dialog,
                "Failed load categories"
            );
        }
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        dialog.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(new JLabel("Brand:"), gbc);
        gbc.gridx = 1;
        dialog.add(brandField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        dialog.add(new JLabel("Stock:"), gbc);
        gbc.gridx = 1;
        dialog.add(stockField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        dialog.add(new JLabel("Price per Day:"), gbc);
        gbc.gridx = 1;
        dialog.add(priceField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        dialog.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1;
        dialog.add(categoryCombo, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        dialog.add(new JLabel("Condition:"), gbc);
        gbc.gridx = 1;
        dialog.add(conditionCombo, gbc);

        JButton saveBtn = new JButton("Save");
        saveBtn.addActionListener(e -> {
            try {
                Equipment eq = new Equipment();
                eq.setName(nameField.getText());
                eq.setBrand(brandField.getText());
                eq.setAvailableStock(Integer.parseInt(stockField.getText()));
                eq.setPricePerDay(Long.parseLong(priceField.getText()));
                eq.setCondition((String) conditionCombo.getSelectedItem());
//                eq.setCategoryId(1L); // Default category
                String selectedCategory =
                    (String) categoryCombo.getSelectedItem();
                Long categoryId =
                    categoryMap.get(selectedCategory);
                eq.setCategoryId(categoryId);

                rentalService.addEquipment(eq);
                loadEquipmentData();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Equipment added successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        dialog.add(saveBtn, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showEditEquipmentDialog() {
        int selectedRow = equipmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an equipment to edit.");
            return;
        }

        Long id = (Long) equipmentTableModel.getValueAt(selectedRow, 0);

        JDialog dialog = new JDialog(this, "Edit Equipment", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField nameField = new JTextField((String) equipmentTableModel.getValueAt(selectedRow, 1), 20);
        JTextField brandField = new JTextField((String) equipmentTableModel.getValueAt(selectedRow, 2), 20);
        JTextField stockField = new JTextField(String.valueOf(equipmentTableModel.getValueAt(selectedRow, 4)), 10);
        JTextField priceField = new JTextField(String.valueOf(equipmentTableModel.getValueAt(selectedRow, 5)), 10);
        JComboBox<String> conditionCombo = new JComboBox<>(new String[]{"GOOD", "DAMAGED", "MAINTENANCE"});
        conditionCombo.setSelectedItem(equipmentTableModel.getValueAt(selectedRow, 6));
        JComboBox<Category> categoryCombo =
            new JComboBox<>();

        Map<String, Long> categoryMap =
            new HashMap<>();
        
        try {
            for (Category c : rentalService.getAllCategories()) {
                categoryCombo.addItem(c);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                dialog,
                "Failed load categories"
            );
        }
        
        String currentCategory = (String) equipmentTableModel.getValueAt(
            selectedRow,3
        );
        for (int i = 0; i < categoryCombo.getItemCount(); i++) {
            Category c = categoryCombo.getItemAt(i);
            if (c.getName().equals(currentCategory)) {
                categoryCombo.setSelectedIndex(i);
                break;
            }
        }
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        dialog.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(new JLabel("Brand:"), gbc);
        gbc.gridx = 1;
        dialog.add(brandField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        dialog.add(new JLabel("Stock:"), gbc);
        gbc.gridx = 1;
        dialog.add(stockField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        dialog.add(new JLabel("Price per Day:"), gbc);
        gbc.gridx = 1;
        dialog.add(priceField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        dialog.add(new JLabel("Category:"), gbc);

        gbc.gridx = 1;
        dialog.add(categoryCombo, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        dialog.add(new JLabel("Condition:"), gbc);
        gbc.gridx = 1;
        dialog.add(conditionCombo, gbc);

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
//                eq.setCategoryId(1L);
                Category selectedCategory = (Category) categoryCombo.getSelectedItem();
                if (selectedCategory != null) {
                    eq.setCategoryId(selectedCategory.getId());
                }

                rentalService.updateEquipment(eq);
                loadEquipmentData();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Equipment updated successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        dialog.add(saveBtn, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void deleteEquipment() {
        int selectedRow = equipmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an equipment to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Long id = (Long) equipmentTableModel.getValueAt(selectedRow, 0);
                rentalService.deleteEquipment(id);
                loadEquipmentData();
                JOptionPane.showMessageDialog(this, "Equipment deleted!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private JPanel createRentalPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Customer selection
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Customer:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> customerCombo = new JComboBox<>();
        loadCustomers(customerCombo);
        formPanel.add(customerCombo, gbc);

        // Available equipment table
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        formPanel.add(new JLabel("Available Equipment:"), gbc);

        gbc.gridy = 2;
        String[] eqColumns = {"ID", "Name", "Brand", "Available", "Price/Day"};
        DefaultTableModel availModel = new DefaultTableModel(eqColumns, 0);
        JTable availTable = new JTable(availModel);
        JScrollPane eqScroll = new JScrollPane(availTable);
        eqScroll.setPreferredSize(new Dimension(600, 120));
        formPanel.add(eqScroll, gbc);

        // Cart
        gbc.gridy = 3;
        formPanel.add(new JLabel("Rental Items:"), gbc);

        gbc.gridy = 4;
        DefaultTableModel cartModel = new DefaultTableModel(new String[]{"Equipment", "Quantity", "Days", "Amount"}, 0);
        JTable cartTable = new JTable(cartModel);
        JScrollPane cartScroll = new JScrollPane(cartTable);
        cartScroll.setPreferredSize(new Dimension(600, 100));
        formPanel.add(cartScroll, gbc);

        // Buttons
        gbc.gridy = 5;
        JPanel rentalButtons = new JPanel();
        JTextField quantityField = new JTextField(5);
        JTextField daysField = new JTextField(5);
        JButton addToCartBtn = new JButton("Add to Cart");
        JButton removeBtn = new JButton("Remove");
        JButton processBtn = new JButton("Process Rental");
        JButton paidButton = new JButton("Mark Paid");
        JButton cancelButton = new JButton("Cancel");

        rentalButtons.add(new JLabel("Qty:"));
        rentalButtons.add(quantityField);
        rentalButtons.add(new JLabel("Days:"));
        rentalButtons.add(daysField);
        rentalButtons.add(addToCartBtn);
        rentalButtons.add(removeBtn);
        rentalButtons.add(processBtn);
        formPanel.add(rentalButtons, gbc);
        
        gbc.gridy = 6;
        JPanel statusPanel = new JPanel();
        statusPanel.add(paidButton);
        statusPanel.add(cancelButton);
        formPanel.add(statusPanel, gbc);

        Map<Integer, Object[]> cartMap = new HashMap<>();

        // Load available equipment
        refreshAvailableEquipment(availModel);

        addToCartBtn.addActionListener(e -> {
            int selectedRow = availTable.getSelectedRow();
            if (selectedRow == -1) {
                return;
            }

            try {
                int quantity = Integer.parseInt(quantityField.getText());
                int days = Integer.parseInt(daysField.getText());
                if (quantity <= 0 || days <= 0) {
                    throw new NumberFormatException();
                }

                Long equipmentId = (Long) availModel.getValueAt(selectedRow, 0);
                String name = (String) availModel.getValueAt(selectedRow, 1);
                int available = (int) availModel.getValueAt(selectedRow, 3);
                Long pricePerDay = (Long) availModel.getValueAt(selectedRow, 4);

                if (quantity > available) {
                    JOptionPane.showMessageDialog(panel, "Not enough stock!");
                    return;
                }

                long amount = pricePerDay * quantity * days;
                cartModel.addRow(new Object[]{name, quantity, days, amount});
                cartMap.put(cartModel.getRowCount() - 1, new Object[]{equipmentId, name, quantity, days, amount});
                availModel.setValueAt(available - quantity, selectedRow, 3);

                quantityField.setText("");
                daysField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Enter valid numbers!");
            }
        });

        removeBtn.addActionListener(e -> {
            int selectedRow = cartTable.getSelectedRow();
            if (selectedRow != -1) {
                Object[] item = cartMap.remove(selectedRow);
                String name = (String) item[1];
                int quantity = (int) item[2];
                for (int i = 0; i < availModel.getRowCount(); i++) {
                    if (availModel.getValueAt(i, 1).equals(name)) {
                        int current = (int) availModel.getValueAt(i, 3);
                        availModel.setValueAt(current + quantity, i, 3);
                        break;
                    }
                }
                cartModel.removeRow(selectedRow);
            }
        });

        processBtn.addActionListener(e -> {
            if (customerCombo.getSelectedIndex() == -1 || cartModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(panel, "Select customer and add items!");
                return;
            }

            try {
                String customerName = (String) customerCombo.getSelectedItem();
                Long customerId = getCustomerIdByName(customerName);

                Invoice invoice = new Invoice();
                invoice.setUserId(customerId);
                invoice.setWorkerId(currentWorkerId);
                invoice.setRentDate(Date.valueOf(LocalDate.now()));
                invoice.setExpectedReturnDate(Date.valueOf(LocalDate.now().plusDays(7)));

                long totalAmount = 0;
                for (int i = 0; i < cartModel.getRowCount(); i++) {
                    Object[] item = cartMap.get(i);
                    InvoiceDetail detail = new InvoiceDetail();
                    detail.setEquipmentId((Long) item[0]);
                    detail.setQuantity((int) item[2]);
                    detail.setTimePeriodInDay((int) item[3]);
                    detail.setAmount((Long) item[4]);
                    invoice.getDetails().add(detail);
                    totalAmount += (Long) item[4];
                }
                invoice.setTotalAmount(totalAmount);

                Long invoiceId = rentalService.createRental(invoice);
                JOptionPane.showMessageDialog(panel, "Rental created! Invoice ID: " + invoiceId);

                cartModel.setRowCount(0);
                cartMap.clear();
                refreshAvailableEquipment(availModel);
                loadInvoiceData();

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage());
            }
        });

        panel.add(formPanel, BorderLayout.NORTH);

        // Invoice history table
        String[] invColumns = {"ID", "Customer", "Total", "Status", "Returned", "Rent Date"};
        invoiceTableModel = new DefaultTableModel(invColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        invoiceTable = new JTable(invoiceTableModel);
        panel.add(new JScrollPane(invoiceTable), BorderLayout.CENTER);
        
        paidButton.addActionListener(e -> {
            int row = invoiceTable.getSelectedRow();
            if (row >= 0) {
                Long invoiceId = (Long) invoiceTableModel.getValueAt(row, 0);
                try {
                    rentalService.updatePaymentStatus(invoiceId,"PAID");
                    loadInvoices();
                    JOptionPane.showMessageDialog(this,"Status updated to PAID");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this,"Error: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this,"Select invoice first");
            }
        });
        
        cancelButton.addActionListener(e -> {
            int row = invoiceTable.getSelectedRow();
            if (row >= 0) {
                Long invoiceId = (Long) invoiceTableModel.getValueAt(row, 0);
                try {
                    rentalService.updatePaymentStatus(invoiceId,"CANCELLED");
                    loadInvoices();
                    JOptionPane.showMessageDialog(this,"Status updated to CANCELLED");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this,"Error: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this,"Select invoice first");
            }
        });
        
        return panel;
    }

    private JPanel createReturnPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel();
        JLabel invoiceLabel = new JLabel("Select Invoice:");
        JComboBox<String> invoiceCombo = new JComboBox<>();
        JButton loadBtn = new JButton("Load Details");
        JButton processBtn = new JButton("Process Return");
        topPanel.add(invoiceLabel);
        topPanel.add(invoiceCombo);
        topPanel.add(loadBtn);
        topPanel.add(processBtn);
        panel.add(topPanel, BorderLayout.NORTH);

        DefaultTableModel returnModel = new DefaultTableModel(new String[]{"Equipment", "Rented", "Returned", "Lost", "Damaged"}, 0);
        JTable returnTable = new JTable(returnModel);
        panel.add(new JScrollPane(returnTable), BorderLayout.CENTER);

        refreshInvoiceCombo(invoiceCombo);

        loadBtn.addActionListener(e -> {
            if (invoiceCombo.getSelectedIndex() == -1) {
                return;
            }

            Long invoiceId = Long.parseLong(invoiceCombo.getSelectedItem().toString().split(" - ")[0]);
            try {
                returnModel.setRowCount(0);
                List<InvoiceDetail> details = returnService.getInvoiceDetails(invoiceId);
                for (InvoiceDetail detail : details) {
                    returnModel.addRow(new Object[]{
                        detail.getEquipmentName() + " " + detail.getEquipmentBrand(),
                        detail.getQuantity(), 0, 0, 0
                    });
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage());
            }
        });

        processBtn.addActionListener(e -> {
            if (invoiceCombo.getSelectedIndex() == -1 || returnModel.getRowCount() == 0) {
                return;
            }

            Long invoiceId = Long.parseLong(invoiceCombo.getSelectedItem().toString().split(" - ")[0]);
            Return returnObj = new Return();
            returnObj.setInvoiceId(invoiceId);
            returnObj.setWorkerId(currentWorkerId);

            try {
                List<InvoiceDetail> originalDetails = returnService.getInvoiceDetails(invoiceId);
                for (int i = 0; i < originalDetails.size(); i++) {
                    int returned = (int) returnModel.getValueAt(i, 2);
                    int lost = (int) returnModel.getValueAt(i, 3);
                    int damaged = (int) returnModel.getValueAt(i, 4);
                    int rented = (int) returnModel.getValueAt(i, 1);

                    if (returned + lost + damaged != rented) {
                        JOptionPane.showMessageDialog(panel, "Sum must equal rented quantity for " + returnModel.getValueAt(i, 0));
                        return;
                    }

                    ReturnDetail detail = new ReturnDetail();
                    detail.setEquipmentId(originalDetails.get(i).getEquipmentId());
                    detail.setQuantityReturned(returned);
                    detail.setQuantityLost(lost);
                    detail.setQuantityDamaged(damaged);
                    returnObj.getDetails().add(detail);
                }

                returnService.processReturn(returnObj);
                JOptionPane.showMessageDialog(panel, "Return processed!");
                refreshInvoiceCombo(invoiceCombo);
                loadInvoiceData();
                returnModel.setRowCount(0);

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage());
            }
        });

        return panel;
    }

    private void loadEquipmentData() {
        equipmentTableModel.setRowCount(0);
        try {
            for (Equipment eq : rentalService.getAllEquipment()) {
                equipmentTableModel.addRow(new Object[]{
                    eq.getId(), eq.getName(), eq.getBrand(), eq.getCategoryName(),
                    eq.getAvailableStock(), eq.getPricePerDay(), eq.getCondition()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void loadInvoiceData() {
        invoiceTableModel.setRowCount(0);
        try {
            for (Invoice inv : rentalService.getAllInvoices()) {
                invoiceTableModel.addRow(new Object[]{
                    inv.getId(), inv.getCustomerName(), inv.getTotalAmount(),
                    inv.getPaymentStatus(), inv.getReturned(), inv.getRentDate()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void loadCustomers(JComboBox<String> combo) {
        try {
            for (Customer c : rentalService.getAllCustomers()) {
                combo.addItem(c.getFullName());
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private Long getCustomerIdByName(String name) throws SQLException {
        for (Customer c : rentalService.getAllCustomers()) {
            if (c.getFullName().equals(name)) {
                return c.getUserId();
            }
        }
        return null;
    }

    private void refreshAvailableEquipment(DefaultTableModel model) {
        model.setRowCount(0);
        try {
            for (Equipment eq : rentalService.getAvailableEquipment()) {
                model.addRow(new Object[]{eq.getId(), eq.getName(), eq.getBrand(), eq.getAvailableStock(), eq.getPricePerDay()});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void refreshInvoiceCombo(JComboBox<String> combo) {
        combo.removeAllItems();
        try {
            for (Invoice inv : rentalService.getUnreturnedInvoices()) {
                combo.addItem(inv.getId() + " - " + inv.getCustomerName());
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void loadInvoices() {
        try {
            invoiceTableModel.setRowCount(0);

            List<Invoice> invoices
                    = rentalService.getAllInvoices();

            for (Invoice inv : invoices) {

                invoiceTableModel.addRow(new Object[]{
                    inv.getId(),
                    inv.getCustomerName(),
                    inv.getTotalAmount(),
                    inv.getPaymentStatus(),
                    inv.getReturned(),
                    inv.getRentDate()
                });
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Failed load invoices: "
                    + ex.getMessage()
            );
        }
    }
}
