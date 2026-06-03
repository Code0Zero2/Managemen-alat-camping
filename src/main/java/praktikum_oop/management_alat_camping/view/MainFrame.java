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
import java.awt.*;

public class MainFrame extends JFrame {

    private RentalService rentalService;
    private ReturnService returnService;
    private Long currentWorkerId; 
    private String currentWorkerName;
    
    private EquipmentPanel equipmentPanel;
    private RentalPanel rentalPanel;
    private ReturnPanel returnPanel;
    private CustomerPanel customerPanel;
    private WorkerPanel workerPanel;

    public MainFrame(Worker loggedInWorker) {
        this.currentWorkerId = loggedInWorker.getId();
        this.currentWorkerName = loggedInWorker.getName();
        this.rentalService = new RentalService();
        this.returnService = new ReturnService();
        
        setTitle("Camping Rental System - Worker: " + currentWorkerName);
        setSize(1300, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initTabs();
        setVisible(true);
    }

    private void initTabs() {
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Initialize Panels
        equipmentPanel = new EquipmentPanel(rentalService);
        rentalPanel = new RentalPanel(rentalService, currentWorkerId);
        returnPanel = new ReturnPanel(returnService, currentWorkerId);
        customerPanel = new CustomerPanel(rentalService);
        workerPanel = new WorkerPanel(rentalService);
        
        // Add to Tabs
        tabbedPane.addTab("Equipment Management", equipmentPanel);
        tabbedPane.addTab("Process Rentals", rentalPanel);
        tabbedPane.addTab("Process Returns", returnPanel);
        tabbedPane.addTab("Customer List", customerPanel);
        tabbedPane.addTab("Admin/Worker List", workerPanel);
        
        // Ensure data updates when you click a different tab!
        tabbedPane.addChangeListener(e -> {
            int idx = tabbedPane.getSelectedIndex();
            if (idx == 0) equipmentPanel.refreshData();
            else if (idx == 1) rentalPanel.refreshData();
            else if (idx == 2) returnPanel.refreshData();
            else if (idx == 3) customerPanel.refreshData();
            else if (idx == 4) workerPanel.refreshData();
        });
        
        // Load initial data for the first tab
        equipmentPanel.refreshData();
        
        add(tabbedPane, BorderLayout.CENTER);
    }
}