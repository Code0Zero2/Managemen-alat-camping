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
    private JTable returnHistoryTable;
    private JTable customerTable; // NEW: Table for Customers
    private JTable workerTable; // NEW: Table for Workers

    private DefaultTableModel equipmentTableModel;
    private DefaultTableModel invoiceTableModel;
    private DefaultTableModel returnHistoryTableModel;
    private DefaultTableModel customerTableModel; // NEW: Model for Customers
    private DefaultTableModel workerTableModel; // NEW: Model for Workers

    private Long currentWorkerId;
    private String currentWorkerName;

    public MainFrame(Worker loggedInWorker) {
        this.currentWorkerId = loggedInWorker.getId();
        this.currentWorkerName = loggedInWorker.getName();
        rentalService = new RentalService();
        returnService = new ReturnService();
        initUI();
        loadEquipmentData();
        loadInvoiceData();
        loadReturnHistoryData();
        loadCustomerData(); // NEW: Load initial customer data
        loadWorkerData(); // NEW: Load initial worker data
        setVisible(true);
    }

    private void initUI() {
        setTitle("Camping Equipment Rental - Logged in as: " + currentWorkerName);
        setSize(1300, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Equipment Management", createEquipmentPanel());
        tabbedPane.addTab("Rental Management", createRentalPanel());
        tabbedPane.addTab("Process Returns", createReturnPanel());
        tabbedPane.addTab("Customer Management", createCustomerPanel()); // NEW TAB
        tabbedPane.addTab("Admin/Worker Management", createWorkerPanel()); // NEW TAB

        add(tabbedPane);
    }

    // ==========================================
    // EQUIPMENT PANEL
    // ==========================================
    private JPanel createEquipmentPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = { "ID", "Name", "Brand", "Category", "Available Stock", "Price/Day", "Condition" };
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
        JComboBox<String> conditionCombo = new JComboBox<>(new String[] { "GOOD", "DAMAGED", "MAINTENANCE" });
        JComboBox<String> categoryCombo = new JComboBox<>();
        Map<String, Long> categoryMap = new HashMap<>();

        try {
            for (Category c : rentalService.getAllCategories()) {
                categoryCombo.addItem(c.getName());
                categoryMap.put(c.getName(), c.getId());
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(dialog, "Failed load categories");
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
                String selectedCategory = (String) categoryCombo.getSelectedItem();
                Long categoryId = categoryMap.get(selectedCategory);
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
        JComboBox<String> conditionCombo = new JComboBox<>(new String[] { "GOOD", "DAMAGED", "MAINTENANCE" });
        conditionCombo.setSelectedItem(equipmentTableModel.getValueAt(selectedRow, 6));
        JComboBox<Category> categoryCombo = new JComboBox<>();

        Map<String, Long> categoryMap = new HashMap<>();

        try {
            for (Category c : rentalService.getAllCategories()) {
                categoryCombo.addItem(c);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(dialog, "Failed load categories");
        }

        String currentCategory = (String) equipmentTableModel.getValueAt(selectedRow, 3);
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

    // ==========================================
    // RENTAL PANEL
    // ==========================================
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
        String[] eqColumns = { "ID", "Name", "Brand", "Available", "Price/Day" };
        DefaultTableModel availModel = new DefaultTableModel(eqColumns, 0);
        JTable availTable = new JTable(availModel);
        JScrollPane eqScroll = new JScrollPane(availTable);
        eqScroll.setPreferredSize(new Dimension(600, 120));
        formPanel.add(eqScroll, gbc);

        // Cart
        gbc.gridy = 3;
        formPanel.add(new JLabel("Rental Items:"), gbc);

        gbc.gridy = 4;
        DefaultTableModel cartModel = new DefaultTableModel(new String[] { "Equipment", "Quantity", "Days", "Amount" },
                0);
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

        refreshAvailableEquipment(availModel);

        addToCartBtn.addActionListener(e -> {
            int selectedRow = availTable.getSelectedRow();
            if (selectedRow == -1)
                return;

            try {
                int quantity = Integer.parseInt(quantityField.getText());
                int days = Integer.parseInt(daysField.getText());
                if (quantity <= 0 || days <= 0)
                    throw new NumberFormatException();

                Long equipmentId = (Long) availModel.getValueAt(selectedRow, 0);
                String name = (String) availModel.getValueAt(selectedRow, 1);
                int available = (int) availModel.getValueAt(selectedRow, 3);
                Long pricePerDay = (Long) availModel.getValueAt(selectedRow, 4);

                if (quantity > available) {
                    JOptionPane.showMessageDialog(panel, "Not enough stock!");
                    return;
                }

                long amount = pricePerDay * quantity * days;
                cartModel.addRow(new Object[] { name, quantity, days, amount });
                cartMap.put(cartModel.getRowCount() - 1, new Object[] { equipmentId, name, quantity, days, amount });
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
        String[] invColumns = { "ID", "Customer", "Total", "Status", "Returned", "Rent Date" };
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
                    rentalService.updatePaymentStatus(invoiceId, "PAID");
                    loadInvoices();
                    JOptionPane.showMessageDialog(this, "Status updated to PAID");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Select invoice first");
            }
        });

        cancelButton.addActionListener(e -> {
            int row = invoiceTable.getSelectedRow();
            if (row >= 0) {
                Long invoiceId = (Long) invoiceTableModel.getValueAt(row, 0);
                try {
                    rentalService.updatePaymentStatus(invoiceId, "CANCELLED");
                    loadInvoices();
                    JOptionPane.showMessageDialog(this, "Status updated to CANCELLED");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Select invoice first");
            }
        });

        return panel;
    }

    // RETURNS PANEL
    private JPanel createReturnPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // --- Top section: Process New Return ---
        JPanel processPanel = new JPanel(new BorderLayout());
        processPanel.setBorder(BorderFactory.createTitledBorder("Process New Return"));

        JPanel controlPanel = new JPanel();
        JLabel invoiceLabel = new JLabel("Select Invoice:");
        JComboBox<String> invoiceCombo = new JComboBox<>();
        JButton loadBtn = new JButton("Load Details");
        JButton processBtn = new JButton("Process Return");
        controlPanel.add(invoiceLabel);
        controlPanel.add(invoiceCombo);
        controlPanel.add(loadBtn);
        controlPanel.add(processBtn);
        processPanel.add(controlPanel, BorderLayout.NORTH);

        DefaultTableModel returnModel = new DefaultTableModel(
                new String[] { "Equipment", "Rented", "Returned", "Lost", "Damaged" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Column 0 is "Equipment", Column 1 is "Rented".
                // We lock those by returning false.
                if (column == 0 || column == 1) {
                    return false;
                }
                // Allow editing for "Returned", "Lost", and "Damaged"
                return true;
            }
        };
        JTable returnTable = new JTable(returnModel);
        JScrollPane processScroll = new JScrollPane(returnTable);
        processScroll.setPreferredSize(new Dimension(800, 150));
        processPanel.add(processScroll, BorderLayout.CENTER);

        panel.add(processPanel, BorderLayout.NORTH);

        // --- Bottom section: Return History ---
        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBorder(BorderFactory.createTitledBorder("Return History"));

        String[] historyCols = { "Return ID", "Date", "Worker Name", "Invoice ID" };
        returnHistoryTableModel = new DefaultTableModel(historyCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        returnHistoryTable = new JTable(returnHistoryTableModel);
        historyPanel.add(new JScrollPane(returnHistoryTable), BorderLayout.CENTER);

        panel.add(historyPanel, BorderLayout.CENTER);

        // --- Action Listeners ---
        refreshInvoiceCombo(invoiceCombo);

        loadBtn.addActionListener(e -> {
            if (invoiceCombo.getSelectedIndex() == -1)
                return;

            Long invoiceId = Long.parseLong(invoiceCombo.getSelectedItem().toString().split(" - ")[0]);
            try {
                returnModel.setRowCount(0);
                List<InvoiceDetail> details = returnService.getInvoiceDetails(invoiceId);
                for (InvoiceDetail detail : details) {
                    returnModel.addRow(new Object[] {
                            detail.getEquipmentName() + " " + detail.getEquipmentBrand(),
                            detail.getQuantity(), 0, 0, 0
                    });
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage());
            }
        });

        processBtn.addActionListener(e -> {
            if (returnTable.isEditing()) {
                returnTable.getCellEditor().stopCellEditing();
            }
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
                    int returned = Integer.parseInt(returnModel.getValueAt(i, 2).toString());
                    int lost = Integer.parseInt(returnModel.getValueAt(i, 3).toString());
                    int damaged = Integer.parseInt(returnModel.getValueAt(i, 4).toString());
                    int rented = Integer.parseInt(returnModel.getValueAt(i, 1).toString());

                    if (returned + lost + damaged != rented) {
                        JOptionPane.showMessageDialog(panel,
                                "Sum must equal rented quantity for " + returnModel.getValueAt(i, 0));
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
                loadReturnHistoryData();
                returnModel.setRowCount(0);

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Please enter valid numbers in the grid.");
            }
        });

        return panel;
    }

    // ==========================================
    // CUSTOMER MANAGEMENT PANEL (NEW)
    // ==========================================
    private JPanel createCustomerPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = { "User ID", "Username", "Full Name", "Email", "Phone" };
        customerTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        customerTable = new JTable(customerTableModel);
        panel.add(new JScrollPane(customerTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addBtn = new JButton("Add Customer");
        JButton editBtn = new JButton("Edit Customer");
        JButton deleteBtn = new JButton("Delete Customer");
        JButton refreshBtn = new JButton("Refresh");

        addBtn.addActionListener(e -> showAddCustomerDialog());
        editBtn.addActionListener(e -> showEditCustomerDialog());
        deleteBtn.addActionListener(e -> deleteCustomer());
        refreshBtn.addActionListener(e -> loadCustomerData());

        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void showAddCustomerDialog() {
        JDialog dialog = new JDialog(this, "Add New Customer", true);
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
                rentalService.addCustomer(cust);

                loadCustomerData();
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

    private void showEditCustomerDialog() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer to edit.");
            return;
        }

        Long userId = (Long) customerTableModel.getValueAt(selectedRow, 0);
        String currentUsername = (String) customerTableModel.getValueAt(selectedRow, 1);
        String currentFullName = (String) customerTableModel.getValueAt(selectedRow, 2);
        String currentEmail = (String) customerTableModel.getValueAt(selectedRow, 3);
        String currentPhone = (String) customerTableModel.getValueAt(selectedRow, 4);

        JDialog dialog = new JDialog(this, "Edit Customer Details", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField usernameField = new JTextField(currentUsername, 20);
        JTextField fullNameField = new JTextField(currentFullName, 20);
        JTextField emailField = new JTextField(currentEmail, 20);
        JTextField phoneField = new JTextField(currentPhone, 15);

        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        dialog.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        dialog.add(fullNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        dialog.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        dialog.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        dialog.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        dialog.add(phoneField, gbc);

        JButton saveBtn = new JButton("Save Changes");
        saveBtn.addActionListener(e -> {
            new Thread(() -> {
                try {
                    Customer cust = new Customer();
                    cust.setUserId(userId);
                    cust.setUsername(usernameField.getText());
                    cust.setFullName(fullNameField.getText());
                    cust.setEmail(emailField.getText());
                    cust.setPhone(phoneField.getText());

                    rentalService.updateCustomer(cust);

                    SwingUtilities.invokeLater(() -> {
                        loadCustomerData();
                        dialog.dispose();
                        JOptionPane.showMessageDialog(this, "Customer details updated successfully!");
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
                    });
                }
            }).start();
        });

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        dialog.add(saveBtn, gbc);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void deleteCustomer() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer to delete.");
            return;
        }

        Long userId = (Long) customerTableModel.getValueAt(selectedRow, 0);
        String name = (String) customerTableModel.getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete customer '" + name + "'?\nThis will permanently delete their account.",
                "Confirm Delete Customer",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            new Thread(() -> {
                try {
                    rentalService.deleteCustomer(userId);
                    SwingUtilities.invokeLater(() -> {
                        loadCustomerData();
                        JOptionPane.showMessageDialog(this, "Customer successfully deleted.");
                    });
                } catch (SQLException ex) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this,
                                "Error deleting customer (it might have active invoices): " + ex.getMessage());
                    });
                }
            }).start();
        }
    }

    // ==========================================
    // WORKER/ADMIN MANAGEMENT PANEL (NEW)
    // ==========================================
    private JPanel createWorkerPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = { "User ID", "Username", "Name", "Shift", "Phone", "Active" };
        workerTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        workerTable = new JTable(workerTableModel);
        panel.add(new JScrollPane(workerTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addBtn = new JButton("Add Admin/Worker");
        JButton refreshBtn = new JButton("Refresh");

        addBtn.addActionListener(e -> showAddWorkerDialog());
        refreshBtn.addActionListener(e -> loadWorkerData());

        buttonPanel.add(addBtn);
        buttonPanel.add(refreshBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void showAddWorkerDialog() {
        JDialog dialog = new JDialog(this, "Add New Worker", true);
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

                // Note: Ensure your RentalService has an addWorker method
                rentalService.addWorker(worker);

                loadWorkerData();
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

    // ==========================================
    // DATA LOADING METHODS
    // ==========================================
    private void loadEquipmentData() {
        equipmentTableModel.setRowCount(0);
        new Thread(() -> {
            try {
                List<Equipment> list = rentalService.getAllEquipment();
                SwingUtilities.invokeLater(() -> {
                    for (Equipment eq : list) {
                        equipmentTableModel.addRow(new Object[] {
                                eq.getId(), eq.getName(), eq.getBrand(), eq.getCategoryName(),
                                eq.getAvailableStock(), eq.getPricePerDay(), eq.getCondition()
                        });
                    }
                });
            } catch (SQLException ex) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                });
            }
        }).start();
    }

    private void loadInvoiceData() {
        invoiceTableModel.setRowCount(0);
        new Thread(() -> {
            try {
                List<Invoice> list = rentalService.getAllInvoices();
                SwingUtilities.invokeLater(() -> {
                    for (Invoice inv : list) {
                        invoiceTableModel.addRow(new Object[] {
                                inv.getId(), inv.getCustomerName(), inv.getTotalAmount(),
                                inv.getPaymentStatus(), inv.getReturned(), inv.getRentDate()
                        });
                    }
                });
            } catch (SQLException ex) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                });
            }
        }).start();
    }

    private void loadReturnHistoryData() {
        returnHistoryTableModel.setRowCount(0);
        new Thread(() -> {
            try {
                List<Return> list = returnService.getAllReturns();
                SwingUtilities.invokeLater(() -> {
                    for (Return r : list) {
                        returnHistoryTableModel.addRow(new Object[] {
                                r.getId(), r.getReturnDate(), r.getWorkerName(), r.getInvoiceId()
                        });
                    }
                });
            } catch (SQLException ex) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Failed to load Return History: " + ex.getMessage());
                });
            }
        }).start();
    }

    private void loadCustomerData() {
        customerTableModel.setRowCount(0);
        new Thread(() -> {
            try {
                List<Customer> list = rentalService.getAllCustomers();
                SwingUtilities.invokeLater(() -> {
                    for (Customer c : list) {
                        customerTableModel.addRow(new Object[] {
                                c.getUserId(), c.getUsername(), c.getFullName(), c.getEmail(), c.getPhone()
                        });
                    }
                });
            } catch (SQLException ex) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Error loading customers: " + ex.getMessage());
                });
            }
        }).start();
    }

    private void loadWorkerData() {
        workerTableModel.setRowCount(0);
        new Thread(() -> {
            try {
                List<Worker> list = rentalService.getAllWorkers();
                SwingUtilities.invokeLater(() -> {
                    for (Worker w : list) {
                        workerTableModel.addRow(new Object[] {
                                w.getId(), w.getUsername(), w.getName(), w.getShift(), w.getPhone(), w.getActive()
                        });
                    }
                });
            } catch (SQLException ex) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Error loading workers: " + ex.getMessage());
                });
            }
        }).start();
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
                model.addRow(new Object[] { eq.getId(), eq.getName(), eq.getBrand(), eq.getAvailableStock(),
                        eq.getPricePerDay() });
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
        invoiceTableModel.setRowCount(0);
        new Thread(() -> {
            try {
                List<Invoice> invoices = rentalService.getAllInvoices();
                SwingUtilities.invokeLater(() -> {
                    for (Invoice inv : invoices) {
                        invoiceTableModel.addRow(new Object[] {
                                inv.getId(), inv.getCustomerName(), inv.getTotalAmount(),
                                inv.getPaymentStatus(), inv.getReturned(), inv.getRentDate()
                        });
                    }
                });
            } catch (SQLException ex) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Failed load invoices: " + ex.getMessage());
                });
            }
        }).start();
    }
}