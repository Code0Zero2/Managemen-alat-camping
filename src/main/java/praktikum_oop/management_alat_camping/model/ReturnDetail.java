/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package praktikum_oop.management_alat_camping.model;

/**
 *
 * @author morxidia
 */

public class ReturnDetail {
    private Long id;
    private Long returnsId;
    private Long equipmentId;
    private Integer quantityReturned;
    private Integer quantityLost;
    private Integer quantityDamaged;
    private String equipmentName;
    private Penalty penalty;
    
    public ReturnDetail() {
        this.quantityReturned = 0;
        this.quantityLost = 0;
        this.quantityDamaged = 0;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getReturnsId() { return returnsId; }
    public void setReturnsId(Long returnsId) { this.returnsId = returnsId; }
    public Long getEquipmentId() { return equipmentId; }
    public void setEquipmentId(Long equipmentId) { this.equipmentId = equipmentId; }
    public Integer getQuantityReturned() { return quantityReturned; }
    public void setQuantityReturned(Integer quantityReturned) { this.quantityReturned = quantityReturned; }
    public Integer getQuantityLost() { return quantityLost; }
    public void setQuantityLost(Integer quantityLost) { this.quantityLost = quantityLost; }
    public Integer getQuantityDamaged() { return quantityDamaged; }
    public void setQuantityDamaged(Integer quantityDamaged) { this.quantityDamaged = quantityDamaged; }
    public String getEquipmentName() { return equipmentName; }
    public void setEquipmentName(String equipmentName) { this.equipmentName = equipmentName; }
    public Penalty getPenalty() { return penalty; }
    public void setPenalty(Penalty penalty) { this.penalty = penalty; }
    
    public int getTotalUnreturned() {
        return quantityLost + quantityDamaged;
    }
}
