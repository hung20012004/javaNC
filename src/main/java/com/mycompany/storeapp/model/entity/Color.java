/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.model.entity;

import java.util.Date;

/**
 *
 * @author Hi
 */
public class Color {

    private long colorId;
    private String name;
    private String description;
    private Date created_at;
    private Date updated_at;

    public Color (int par1, int par2, int par3) {}

    public Color(long colorId, String name, String description) {
        this.colorId = colorId;
        this.name = name;
        this.description = description;
    }

    public long getColorId() { return colorId; }
    public void setColorId(long colorId) { this.colorId = colorId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Date getCreated_at() { return created_at; }
    public void setCreated_at(Date created_at) { this.created_at = created_at; }

    public Date getUpdated_at() { return updated_at; }
    public void setUpdated_at(Date updated_at) { this.updated_at = updated_at; }
}
