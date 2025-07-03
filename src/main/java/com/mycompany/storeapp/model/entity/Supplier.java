/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.model.entity;

/**
 *
 * @author Hi
 */
import java.time.LocalDateTime;
import java.util.Date;

public class Supplier {
private Integer supplierId;
    private String name;
    private String contactName;
    private String phone;
    private String email;
    private String address;
    private String description;
    private String logoUrl;
    private Boolean isActive;
    private Date createdAt;
    private Date updatedAt;

    public Supplier() {}

    // Getters & Setters
    public Integer getSupplierId() { return supplierId; }
    public void setSupplierId(Integer supplierId) { this.supplierId = supplierId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    public String getFormattedPhone() {
        return this.phone;
    }

    public boolean matchesSearch(String search) {
        if (search == null || search.isEmpty()) return true;
        String lower = search.toLowerCase();
        return (name != null && name.toLowerCase().contains(lower)) ||
               (contactName != null && contactName.toLowerCase().contains(lower)) ||
               (email != null && email.toLowerCase().contains(lower)) ||
               (phone != null && phone.toLowerCase().contains(lower));
    }
}
