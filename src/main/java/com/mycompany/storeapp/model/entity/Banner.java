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

public class Banner {
    private String title;
    private String subtitle;
    private String buttonText;
    private String buttonLink;
    private String imageUrl;
    private boolean isActive;
    private int orderSequence;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public Banner() {}

    // Getters và Setters

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    public String getButtonLink() {
        return buttonLink;
    }

    public void setButtonLink(String buttonLink) {
        this.buttonLink = buttonLink;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getOrderSequence() {
        return orderSequence;
    }

    public void setOrderSequence(int orderSequence) {
        this.orderSequence = orderSequence;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    // Có thể thêm phương thức kiểm tra active theo ngày nếu cần:
    public boolean isCurrentlyActive() {
        LocalDateTime now = LocalDateTime.now();
        boolean startOk = (startDate == null || !startDate.isAfter(now));
        boolean endOk = (endDate == null || !endDate.isBefore(now));
        return isActive && startOk && endOk;
    }
}

