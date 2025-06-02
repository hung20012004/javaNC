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
import java.util.ArrayList;
import java.util.List;

public class InventoryCheck {
    private int checkId;
    private int createBy; // ID người tạo (tương ứng với User)
    private LocalDateTime checkDate;
    private String status;
    private String note;

    // Quan hệ với User (tùy ý nếu cần liên kết)
    private User createdByUser;

    // Quan hệ với danh sách chi tiết kiểm kho
    private List<InventoryCheckDetail> details = new ArrayList<>();

    public InventoryCheck() {}

    // Getters và Setters

    public int getCheckId() {
        return checkId;
    }

    public void setCheckId(int checkId) {
        this.checkId = checkId;
    }

    public int getCreateBy() {
        return createBy;
    }

    public void setCreateBy(int createBy) {
        this.createBy = createBy;
    }

    public LocalDateTime getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(LocalDateTime checkDate) {
        this.checkDate = checkDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public User getCreatedByUser() {
        return createdByUser;
    }

    public void setCreatedByUser(User createdByUser) {
        this.createdByUser = createdByUser;
    }

    public List<InventoryCheckDetail> getDetails() {
        return details;
    }

    public void setDetails(List<InventoryCheckDetail> details) {
        this.details = details;
    }

    // Helper method: tổng số sản phẩm được kiểm
    public int getTotalProducts() {
        return details != null ? details.size() : 0;
    }

    // Helper method: tổng chênh lệch số lượng
    public int getTotalDifference() {
        int sum = 0;
        if (details != null) {
            for (InventoryCheckDetail detail : details) {
                sum += detail.getDifference();
            }
        }
        return sum;
    }

    // Kiểm tra trạng thái
    public boolean isDraft() {
        return "draft".equalsIgnoreCase(status);
    }

    public boolean isCompleted() {
        return "completed".equalsIgnoreCase(status);
    }

    // Thay đổi trạng thái
    public void markAsCompleted() {
        this.status = "completed";
        // Nếu cần lưu vào CSDL thì gọi hàm DAO để update
    }

    public void markAsDraft() {
        this.status = "draft";
        // Nếu cần lưu vào CSDL thì gọi hàm DAO để update
    }
}

