/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package praktikum_oop.management_alat_camping.model;

/**
 *
 * @author morxidia
 */
// model/Equipment.java

import java.sql.Timestamp;

public class Equipment {
    private Long id;
    private String name;
    private String brand;
    private Integer availableStock;
    private Long pricePerDay;
    private String condition;
    private Timestamp addedAt;
    private Long categoryId;
    private String categoryName;
    
    public Equipment() {}
    
    public Equipment(String name, String brand, Integer availableStock, Long pricePerDay, Long categoryId) {
        this.name = name;
        this.brand = brand;
        this.availableStock = availableStock;
        this.pricePerDay = pricePerDay;
        this.categoryId = categoryId;
        this.condition = "GOOD";
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public Integer getAvailableStock() { return availableStock; }
    public void setAvailableStock(Integer availableStock) { this.availableStock = availableStock; }
    public Long getPricePerDay() { return pricePerDay; }
    public void setPricePerDay(Long pricePerDay) { this.pricePerDay = pricePerDay; }
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    public Timestamp getAddedAt() { return addedAt; }
    public void setAddedAt(Timestamp addedAt) { this.addedAt = addedAt; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
}