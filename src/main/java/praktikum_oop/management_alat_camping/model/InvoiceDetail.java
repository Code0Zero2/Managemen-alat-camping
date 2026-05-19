/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package praktikum_oop.management_alat_camping.model;

/**
 *
 * @author morxidia
 */

public class InvoiceDetail {
    private Long invoiceId;
    private Long equipmentId;
    private Integer quantity;
    private Integer timePeriodInDay;
    private Long amount;
    private String equipmentName;
    private String equipmentBrand;
    
    public Long getInvoiceId() { return invoiceId; }
    public void setInvoiceId(Long invoiceId) { this.invoiceId = invoiceId; }
    public Long getEquipmentId() { return equipmentId; }
    public void setEquipmentId(Long equipmentId) { this.equipmentId = equipmentId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public Integer getTimePeriodInDay() { return timePeriodInDay; }
    public void setTimePeriodInDay(Integer timePeriodInDay) { this.timePeriodInDay = timePeriodInDay; }
    public Long getAmount() { return amount; }
    public void setAmount(Long amount) { this.amount = amount; }
    public String getEquipmentName() { return equipmentName; }
    public void setEquipmentName(String equipmentName) { this.equipmentName = equipmentName; }
    public String getEquipmentBrand() { return equipmentBrand; }
    public void setEquipmentBrand(String equipmentBrand) { this.equipmentBrand = equipmentBrand; }
}
