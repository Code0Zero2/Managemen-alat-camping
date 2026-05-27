/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package praktikum_oop.management_alat_camping.model;

/**
 *
 * @author morxidia
 */

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class Invoice {
    private Long id;
    private Long userId;
    private Long workerId;
    private Long totalAmount;
    private String paymentStatus;
    private Date rentDate;
    private Date expectedReturnDate;
    private Boolean returned;
    private List<InvoiceDetail> details;
    private String customerName;
    private String workerName;
    
    public Invoice() {
        this.details = new ArrayList<>();
        this.paymentStatus = "PENDING";
        this.returned = false;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getWorkerId() { return workerId; }
    public void setWorkerId(Long workerId) { this.workerId = workerId; }
    public Long getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Long totalAmount) { this.totalAmount = totalAmount; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public Date getRentDate() { return rentDate; }
    public void setRentDate(Date rentDate) { this.rentDate = rentDate; }
    public Date getExpectedReturnDate() { return expectedReturnDate; }
    public void setExpectedReturnDate(Date expectedReturnDate) { this.expectedReturnDate = expectedReturnDate; }
    public Boolean getReturned() { return returned; }
    public void setReturned(Boolean returned) { this.returned = returned; }
    public List<InvoiceDetail> getDetails() { return details; }
    public void setDetails(List<InvoiceDetail> details) { this.details = details; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getWorkerName() { return workerName; }
    public void setWorkerName(String workerName) { this.workerName = workerName; }
}
