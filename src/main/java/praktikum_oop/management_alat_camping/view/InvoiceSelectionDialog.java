/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package praktikum_oop.management_alat_camping.view;

/**
 *
 * @author morxidia
 */

import praktikum_oop.management_alat_camping.model.Invoice;
import praktikum_oop.management_alat_camping.service.ReturnService;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class InvoiceSelectionDialog extends JDialog {
    private JTable table;
    private DefaultTableModel model;
    private Invoice selectedInvoice = null;
    private List<Invoice> invoices;

    public InvoiceSelectionDialog(Frame parent, ReturnService service) {
        super(parent, "Search & Select Unreturned Invoice", true);
        setSize(600, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        searchPanel.add(new JLabel("Search: "), BorderLayout.WEST);
        JTextField searchField = new JTextField();
        searchPanel.add(searchField, BorderLayout.CENTER);
        add(searchPanel, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"Invoice ID", "Customer Name", "Rent Date", "Total Amount"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        add(new JScrollPane(table), BorderLayout.CENTER);

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            private void filter() {
                String text = searchField.getText();
                sorter.setRowFilter(text.trim().isEmpty() ? null : RowFilter.regexFilter("(?i)" + text));
            }
        });

        JPanel btnPanel = new JPanel();
        JButton selectBtn = new JButton("Select");
        JButton cancelBtn = new JButton("Cancel");
        btnPanel.add(selectBtn); btnPanel.add(cancelBtn);
        add(btnPanel, BorderLayout.SOUTH);

        // Fetch Data BEFORE showing
        try {
            invoices = service.getUnreturnedInvoices();
            for (Invoice i : invoices) {
                model.addRow(new Object[]{i.getId(), i.getCustomerName(), i.getRentDate(), i.getTotalAmount()});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching invoices: " + ex.getMessage());
        }

        cancelBtn.addActionListener(e -> dispose());
        selectBtn.addActionListener(e -> selectAndClose());
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) selectAndClose();
            }
        });
    }

    private void selectAndClose() {
        int row = table.getSelectedRow();
        if (row != -1) {
            Long id = (Long) table.getValueAt(table.convertRowIndexToModel(row), 0);
            selectedInvoice = invoices.stream().filter(i -> i.getId().equals(id)).findFirst().orElse(null);
            dispose();
        }
    }

    public Invoice getSelectedInvoice() { return selectedInvoice; }
}