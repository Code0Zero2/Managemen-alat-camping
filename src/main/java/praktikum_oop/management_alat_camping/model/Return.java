/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package praktikum_oop.management_alat_camping.model;

/**
 *
 * @author morxidia
 */

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Return {
    private Long id;
    private Timestamp returnDate;
    private Long workerId;
    private Long invoiceId;
    private List<ReturnDetail> details;
    private String workerName;
    private String invoiceCode;
    
    public Return() {
        this.details = new ArrayList<>();
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Timestamp getReturnDate() { return returnDate; }
    public void setReturnDate(Timestamp returnDate) { this.returnDate = returnDate; }
    public Long getWorkerId() { return workerId; }
    public void setWorkerId(Long workerId) { this.workerId = workerId; }
    public Long getInvoiceId() { return invoiceId; }
    public void setInvoiceId(Long invoiceId) { this.invoiceId = invoiceId; }
    public List<ReturnDetail> getDetails() { return details; }
    public void setDetails(List<ReturnDetail> details) { this.details = details; }
    public String getWorkerName() { return workerName; }
    public void setWorkerName(String workerName) { this.workerName = workerName; }
    public String getInvoiceCode() { return invoiceCode; }
    public void setInvoiceCode(String invoiceCode) { this.invoiceCode = invoiceCode; }
}