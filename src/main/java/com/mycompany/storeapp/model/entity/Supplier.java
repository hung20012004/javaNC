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

public class Supplier {
    private int supplierId;
    private String name;
    private String contactName;
    private String phone;
    private String email;
    private String address;
    private String description;
    private String logoUrl;
    private boolean isActive;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Supplier() {
        // Mặc định isActive true khi tạo mới
        this.isActive = true;
    }

    public int getSupplierId() {
        return supplierId;
    }
    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getContactName() {
        return contactName;
    }
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getLogoUrl() {
        return logoUrl;
    }
    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public boolean isActive() {
        return isActive;
    }
    public void setActive(boolean active) {
        isActive = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Phương thức tương tự scopeActive
    public boolean isActiveSupplier() {
        return this.isActive;
    }

    // Phương thức để format phone (nếu cần)
    public String getFormattedPhone() {
        // Thêm logic format số điện thoại nếu muốn
        return this.phone;
    }

    // Phương thức tìm kiếm (ví dụ, có thể dùng bên service)
    public boolean matchesSearch(String search) {
        if (search == null || search.isEmpty()) return true;
        String lower = search.toLowerCase();
        return (name != null && name.toLowerCase().contains(lower)) ||
               (contactName != null && contactName.toLowerCase().contains(lower)) ||
               (email != null && email.toLowerCase().contains(lower)) ||
               (phone != null && phone.toLowerCase().contains(lower));
    }
}
