/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package praktikum_oop.management_alat_camping.model;

/**
 *
 * @author morxidia
 */
public class Worker extends User {

    private String name;
    private String phone;
    private String shift;
    private Long divisionId;
    private Boolean active;
    private String divisionName;

    public Worker() {
        super(); // Calls the User default constructor
        this.active = true;
    }

    // Maps the old userId methods to the inherited 'id' from User
    public Long getUserId() {
        return getId();

    }

    public void setUserId(Long userId) {
        setId(userId);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public Long getDivisionId() {
        return divisionId;
    }

    public void setDivisionId(Long divisionId) {
        this.divisionId = divisionId;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getDivisionName() {
        return divisionName;
    }

    public void setDivisionName(String divisionName) {
        this.divisionName = divisionName;
    }

    @Override
    public String getRole() {
        return "Staff";
    }
}