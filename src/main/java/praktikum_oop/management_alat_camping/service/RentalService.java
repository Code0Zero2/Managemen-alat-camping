/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package praktikum_oop.management_alat_camping.service;

/**
 *
 * @author morxidia
 */

import praktikum_oop.management_alat_camping.model.*;
import praktikum_oop.management_alat_camping.repository.*;
import java.sql.SQLException;
import java.util.List;

public class RentalService {
    private CustomerRepository customerRepo;
    private EquipmentRepository equipmentRepo;
    private InvoiceRepository invoiceRepo;
    
    public RentalService() {
        this.customerRepo = new CustomerRepository();
        this.equipmentRepo = new EquipmentRepository();
        this.invoiceRepo = new InvoiceRepository();
    }
    
    public List<Customer> getAllCustomers() throws SQLException {
        return customerRepo.findAll();
    }
    
    public List<Equipment> getAllEquipment() throws SQLException {
        return equipmentRepo.findAll();
    }
    
    public List<Equipment> getAvailableEquipment() throws SQLException {
        return equipmentRepo.findAvailable();
    }
    
    public void addEquipment(Equipment equipment) throws SQLException {
        equipmentRepo.save(equipment);
    }
    
    public void updateEquipment(Equipment equipment) throws SQLException {
        equipmentRepo.update(equipment);
    }
    
    public void deleteEquipment(Long id) throws SQLException {
        equipmentRepo.delete(id);
    }
    
    public Long createRental(Invoice invoice) throws SQLException {
        return invoiceRepo.createInvoice(invoice);
    }
    
    public List<Invoice> getAllInvoices() throws SQLException {
        return invoiceRepo.findAll();
    }
    
    public List<Invoice> getUnreturnedInvoices() throws SQLException {
        return invoiceRepo.findUnreturned();
    }
    
    public List<InvoiceDetail> getInvoiceDetails(Long invoiceId) throws SQLException {
        return invoiceRepo.findDetailsByInvoiceId(invoiceId);
    }
}