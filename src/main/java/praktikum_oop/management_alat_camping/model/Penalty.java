/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package praktikum_oop.management_alat_camping.model;

/**
 *
 * @author morxidia
 */

public class Penalty {
    private Long id;
    private Long returnDetailId;
    private String name;
    private Long fine;
    
    public Penalty() {}
    
    public Penalty(String name, Long fine) {
        this.name = name;
        this.fine = fine;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getReturnDetailId() { return returnDetailId; }
    public void setReturnDetailId(Long returnDetailId) { this.returnDetailId = returnDetailId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Long getFine() { return fine; }
    public void setFine(Long fine) { this.fine = fine; }
}