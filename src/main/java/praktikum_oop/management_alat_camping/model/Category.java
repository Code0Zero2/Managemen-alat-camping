/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package praktikum_oop.management_alat_camping.model;

/**
 *
 * @author morxidia
 */
// model/Category.java
public class Category {
    private Long id;
    private String name;
    private String description;
    
    public Category() {}
    
    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    @Override
    public String toString() {
        return name;
    }
}