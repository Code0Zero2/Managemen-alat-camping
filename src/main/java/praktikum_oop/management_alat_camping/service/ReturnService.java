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
import java.sql.Timestamp;
import java.util.List;

public class ReturnService {
    private ReturnRepository returnRepo;
    private InvoiceRepository invoiceRepo;
    
    public ReturnService() {
        this.returnRepo = new ReturnRepository();
        this.invoiceRepo = new InvoiceRepository();
    }
    
    public Long processReturn(Return returnObj) throws SQLException {
        returnObj.setReturnDate(new Timestamp(System.currentTimeMillis()));
        return returnRepo.processReturn(returnObj);
    }
    
    public List<Return> getAllReturns() throws SQLException {
        return returnRepo.findAll();
    }
    
    public List<Invoice> getUnreturnedInvoices() throws SQLException {
        return invoiceRepo.findUnreturned();
    }
    
    public List<InvoiceDetail> getInvoiceDetails(Long invoiceId) throws SQLException {
        return invoiceRepo.findDetailsByInvoiceId(invoiceId);
    }
}