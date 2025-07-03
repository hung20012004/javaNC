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

public class Banner {
    private long bannerId;
    private String title;
    private String subtitle;
    private String buttonText;
    private String buttonLink;
    private String imageUrl;
    private int isActive; 
    private int orderSequence;
    private Date startDate;
    private Date endDate;
    private Date created_at;
    private Date updated_at;

    public Banner() {}

    public Banner(long bannerId, String title, String subtitle, String buttonText, String buttonLink, String imageUrl, 
                  int isActive, int orderSequence, Date startDate, Date endDate) {
        this.bannerId = bannerId;
        this.title = title;
        this.subtitle = subtitle;
        this.buttonText = buttonText;
        this.buttonLink = buttonLink;
        this.imageUrl = imageUrl;
        this.isActive = isActive;
        this.orderSequence = orderSequence;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public long getBannerId() { return bannerId; }
    public void setBannerId(long bannerId) { this.bannerId = bannerId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }
    
    public String getButtonText() { return buttonText; }
    public void setButtonText(String buttonText) { this.buttonText = buttonText; }
    
    public String getButtonLink() { return buttonLink; }
    public void setButtonLink(String buttonLink) { this.buttonLink = buttonLink; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public int getIsActive() { return isActive; }
    public void setIsActive(int isActive) { this.isActive = isActive; }
    
    public int getOrderSequence() { return orderSequence; }
    public void setOrderSequence(int orderSequence) { this.orderSequence = orderSequence; }
    
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    
    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }
    
    public Date getCreated_at() { return created_at; }
    public void setCreated_at(Date created_at) { this.created_at = created_at; }

    public Date getUpdated_at() { return updated_at; }
    public void setUpdated_at(Date updated_at) { this.updated_at = updated_at; }

//    // Có thể thêm phương thức kiểm tra active theo ngày nếu cần:
//    public boolean isCurrentlyActive() {
//        LocalDateTime now = LocalDateTime.now();
//        boolean startOk = (startDate == null || !startDate.isAfter(now));
//        boolean endOk = (endDate == null || !endDate.isBefore(now));
//        return isActive && startOk && endOk;
//    }
}

