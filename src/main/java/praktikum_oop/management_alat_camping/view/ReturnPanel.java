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
import praktikum_oop.management_alat_camping.service.ReturnService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ReturnPanel extends JPanel {
    private ReturnService returnService;
    private Long currentWorkerId;
    
    private Long selectedInvoiceId = null;
    private JTextField invoiceField;
    
    private DefaultTableModel returnModel;
    private JTable returnTable;
    private DefaultTableModel historyModel;

    public ReturnPanel(ReturnService returnService, Long currentWorkerId) {
        this.returnService = returnService;
        this.currentWorkerId = currentWorkerId;
        setLayout(new BorderLayout(10, 10));
        initUI();
    }

    private void initUI() {
        JPanel processPanel = new JPanel(new BorderLayout());
        processPanel.setBorder(BorderFactory.createTitledBorder("Process New Return"));

        // 1. New Invoice Selection Workflow
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.add(new JLabel("Select Invoice: "));
        invoiceField = new JTextField(25);
        invoiceField.setEditable(false);
        JButton searchInvoiceBtn = new JButton("Search Invoice...");
        JButton processBtn = new JButton("Process Return Data");
        
        controlPanel.add(invoiceField);
        controlPanel.add(searchInvoiceBtn);
        controlPanel.add(processBtn);
        processPanel.add(controlPanel, BorderLayout.NORTH);

        searchInvoiceBtn.addActionListener(e -> {
            InvoiceSelectionDialog dialog = new InvoiceSelectionDialog((Frame) SwingUtilities.getWindowAncestor(this), returnService);
            dialog.setVisible(true);
            Invoice selected = dialog.getSelectedInvoice();
            if (selected != null) {
                selectedInvoiceId = selected.getId();
                invoiceField.setText("ID: " + selected.getId() + " - " + selected.getCustomerName());
                loadInvoiceDetailsToGrid(selectedInvoiceId);
            }
        });

        // 2. Return Grid
        returnModel = new DefaultTableModel(new String[]{"Equipment", "Rented", "Returned", "Lost", "Damaged"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return col > 1; } // Lock Eq and Rented
        };
        returnTable = new JTable(returnModel);
        JScrollPane processScroll = new JScrollPane(returnTable);
        processScroll.setPreferredSize(new Dimension(800, 150));
        processPanel.add(processScroll, BorderLayout.CENTER);
        add(processPanel, BorderLayout.NORTH);

        // 3. History
        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBorder(BorderFactory.createTitledBorder("Return History"));
        historyModel = new DefaultTableModel(new String[]{"Return ID", "Date", "Worker Name", "Invoice ID"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        historyPanel.add(new JScrollPane(new JTable(historyModel)), BorderLayout.CENTER);
        add(historyPanel, BorderLayout.CENTER);

        // 4. Process Logic
        processBtn.addActionListener(e -> {
            if (returnTable.isEditing()) returnTable.getCellEditor().stopCellEditing();
            if (selectedInvoiceId == null || returnModel.getRowCount() == 0) return;

            Return returnObj = new Return();
            returnObj.setInvoiceId(selectedInvoiceId);
            returnObj.setWorkerId(currentWorkerId);

            try {
                List<InvoiceDetail> origDetails = returnService.getInvoiceDetails(selectedInvoiceId);
                for (int i = 0; i < origDetails.size(); i++) {
                    int returned = Integer.parseInt(returnModel.getValueAt(i, 2).toString());
                    int lost = Integer.parseInt(returnModel.getValueAt(i, 3).toString());
                    int damaged = Integer.parseInt(returnModel.getValueAt(i, 4).toString());
                    int rented = Integer.parseInt(returnModel.getValueAt(i, 1).toString());

                    if (returned + lost + damaged != rented) {
                        JOptionPane.showMessageDialog(this, "Sum must equal rented qty for " + returnModel.getValueAt(i, 0));
                        return;
                    }

                    ReturnDetail detail = new ReturnDetail();
                    detail.setEquipmentId(origDetails.get(i).getEquipmentId());
                    detail.setQuantityReturned(returned);
                    detail.setQuantityLost(lost);
                    detail.setQuantityDamaged(damaged);
                    returnObj.getDetails().add(detail);
                }

                returnService.processReturn(returnObj);
                JOptionPane.showMessageDialog(this, "Return Successfully Processed!");
                
                selectedInvoiceId = null;
                invoiceField.setText("");
                returnModel.setRowCount(0);
                refreshData();

            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: Check numbers!"); }
        });
    }

    private void loadInvoiceDetailsToGrid(Long invoiceId) {
        returnModel.setRowCount(0);
        try {
            List<InvoiceDetail> details = returnService.getInvoiceDetails(invoiceId);
            for (InvoiceDetail detail : details) {
                // Auto-fill returned amount for convenience
                returnModel.addRow(new Object[]{detail.getEquipmentName() + " " + detail.getEquipmentBrand(), detail.getQuantity(), detail.getQuantity(), 0, 0});
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    public void refreshData() {
        historyModel.setRowCount(0);
        try {
            for (Return r : returnService.getAllReturns()) {
                historyModel.addRow(new Object[]{r.getId(), r.getReturnDate(), r.getWorkerName(), r.getInvoiceId()});
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
    }
}