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
import praktikum_oop.management_alat_camping.service.RentalService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;

public class RentalPanel extends JPanel {
    private RentalService rentalService;
    private Long currentWorkerId;
    
    private Long selectedCustomerId = null;
    private JTextField customerField;
    
    private DefaultTableModel availModel;
    private DefaultTableModel cartModel;
    private DefaultTableModel invoiceTableModel;
    private JTable availTable;
    private JTable invoiceTable;

    public RentalPanel(RentalService rentalService, Long currentWorkerId) {
        this.rentalService = rentalService;
        this.currentWorkerId = currentWorkerId;
        setLayout(new BorderLayout(10, 10));
        initUI();
    }

    private void initUI() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Customer:"), gbc);
        
        JPanel customerPickPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        customerField = new JTextField(20);
        customerField.setEditable(false);
        JButton searchCustomerBtn = new JButton("Search...");
        customerPickPanel.add(customerField);
        customerPickPanel.add(searchCustomerBtn);
        
        gbc.gridx = 1; formPanel.add(customerPickPanel, gbc);

        searchCustomerBtn.addActionListener(e -> {
            CustomerSelectionDialog dialog = new CustomerSelectionDialog((Frame) SwingUtilities.getWindowAncestor(this), rentalService);
            dialog.setVisible(true);
            Customer selected = dialog.getSelectedCustomer();
            if (selected != null) {
                selectedCustomerId = selected.getUserId();
                customerField.setText(selected.getFullName());
            }
        });

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2; formPanel.add(new JLabel("Available Equipment:"), gbc);

        gbc.gridy = 2;
        cartModel = new DefaultTableModel(new String[]{"Eq ID", "Equipment", "Quantity", "Days", "Amount"}, 0);
        availModel = new DefaultTableModel(new String[]{"ID", "Name", "Brand", "Available", "Price/Day"}, 0);
        availTable = new JTable(availModel);
        JScrollPane eqScroll = new JScrollPane(availTable);
        eqScroll.setPreferredSize(new Dimension(600, 120));
        formPanel.add(eqScroll, gbc);
        
        gbc.gridy = 3; formPanel.add(new JLabel("Rental Items (Cart):"), gbc);

        gbc.gridy = 4;
        JTable cartTable = new JTable(cartModel);
        JScrollPane cartScroll = new JScrollPane(cartTable);
        cartScroll.setPreferredSize(new Dimension(600, 100));
        formPanel.add(cartScroll, gbc);

        gbc.gridy = 5;
        JPanel rentalButtons = new JPanel();
        JTextField quantityField = new JTextField(5);
        JTextField daysField = new JTextField(5);
        JButton addToCartBtn = new JButton("Add to Cart");
        JButton removeBtn = new JButton("Remove");
        JButton processBtn = new JButton("Process Rental");
        
        rentalButtons.add(new JLabel("Qty:")); rentalButtons.add(quantityField);
        rentalButtons.add(new JLabel("Days:")); rentalButtons.add(daysField);
        rentalButtons.add(addToCartBtn); rentalButtons.add(removeBtn); rentalButtons.add(processBtn);
        formPanel.add(rentalButtons, gbc);

        add(formPanel, BorderLayout.NORTH);

        // -- Invoice Table Section --
        invoiceTableModel = new DefaultTableModel(new String[]{"ID", "Customer", "Total", "Status", "Returned", "Rent Date"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        invoiceTable = new JTable(invoiceTableModel);
        add(new JScrollPane(invoiceTable), BorderLayout.CENTER);

        // -- NEW: Invoice Status Buttons (Mark Paid / Cancel) --
        JPanel statusPanel = new JPanel();
        JButton paidButton = new JButton("Mark Paid");
        JButton cancelButton = new JButton("Cancel Invoice");
        statusPanel.add(paidButton);
        statusPanel.add(cancelButton);
        add(statusPanel, BorderLayout.SOUTH);

        // ==========================
        // ACTION LISTENERS
        // ==========================

        addToCartBtn.addActionListener(e -> {
            int row = availTable.getSelectedRow();
            if (row == -1) return;
            try {
                int qty = Integer.parseInt(quantityField.getText());
                int days = Integer.parseInt(daysField.getText());
                if (qty <= 0 || days <= 0) throw new NumberFormatException();

                Long eqId = (Long) availModel.getValueAt(row, 0);
                String name = (String) availModel.getValueAt(row, 1);
                int available = (int) availModel.getValueAt(row, 3);
                Long price = (Long) availModel.getValueAt(row, 4);

                if (qty > available) {
                    JOptionPane.showMessageDialog(this, "Not enough stock!");
                    return;
                }

                long amount = price * qty * days;
                cartModel.addRow(new Object[]{eqId, name, qty, days, amount});
                availModel.setValueAt(available - qty, row, 3);
                quantityField.setText(""); daysField.setText("");
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Invalid numbers!"); }
        });

        removeBtn.addActionListener(e -> {
            int row = cartTable.getSelectedRow();
            if (row != -1) {
                String name = (String) cartModel.getValueAt(row, 1);
                int qty = (int) cartModel.getValueAt(row, 2);

                for (int i = 0; i < availModel.getRowCount(); i++) {
                    if (availModel.getValueAt(i, 1).equals(name)) {
                        availModel.setValueAt((int)availModel.getValueAt(i, 3) + qty, i, 3);
                        break;
                    }
                }
                cartModel.removeRow(row);
            }
        });

        processBtn.addActionListener(e -> {
            if (selectedCustomerId == null || cartModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Please select a customer and add items!");
                return;
            }
            try {
                Invoice invoice = new Invoice();
                invoice.setUserId(selectedCustomerId);
                invoice.setWorkerId(currentWorkerId);
                invoice.setRentDate(Date.valueOf(LocalDate.now()));
                invoice.setExpectedReturnDate(Date.valueOf(LocalDate.now().plusDays(7)));

                long totalAmount = 0;
                for (int i = 0; i < cartModel.getRowCount(); i++) {
                    InvoiceDetail detail = new InvoiceDetail();
                    detail.setEquipmentId((Long) cartModel.getValueAt(i, 0)); 
                    detail.setQuantity((int) cartModel.getValueAt(i, 2));
                    detail.setTimePeriodInDay((int) cartModel.getValueAt(i, 3));
                    detail.setAmount((Long) cartModel.getValueAt(i, 4));
                    invoice.getDetails().add(detail);
                    totalAmount += (Long) cartModel.getValueAt(i, 4);
                }
                invoice.setTotalAmount(totalAmount);
                rentalService.createRental(invoice);
                JOptionPane.showMessageDialog(this, "Rental Processed!");

                cartModel.setRowCount(0);
                customerField.setText(""); selectedCustomerId = null;
                refreshData(); 
            } catch (SQLException ex) { JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage()); }
        });

        // -- NEW: Action Listeners for Payment Status --
        paidButton.addActionListener(e -> {
            int row = invoiceTable.getSelectedRow();
            if (row >= 0) {
                Long invoiceId = (Long) invoiceTableModel.getValueAt(row, 0);
                try {
                    rentalService.updatePaymentStatus(invoiceId, "PAID");
                    refreshData();
                    JOptionPane.showMessageDialog(this, "Status updated to PAID");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select an invoice first.");
            }
        });

        cancelButton.addActionListener(e -> {
            int row = invoiceTable.getSelectedRow();
            if (row >= 0) {
                Long invoiceId = (Long) invoiceTableModel.getValueAt(row, 0);
                try {
                    rentalService.updatePaymentStatus(invoiceId, "CANCELLED");
                    refreshData();
                    JOptionPane.showMessageDialog(this, "Status updated to CANCELLED");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select an invoice first.");
            }
        });
    }

    public void refreshData() {
        availModel.setRowCount(0);
        invoiceTableModel.setRowCount(0);
        try {
            for (Equipment eq : rentalService.getAvailableEquipment()) {
                availModel.addRow(new Object[]{eq.getId(), eq.getName(), eq.getBrand(), eq.getAvailableStock(), eq.getPricePerDay()});
            }
            for (Invoice inv : rentalService.getAllInvoices()) {
                invoiceTableModel.addRow(new Object[]{inv.getId(), inv.getCustomerName(), inv.getTotalAmount(), inv.getPaymentStatus(), inv.getReturned(), inv.getRentDate()});
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
    }
}