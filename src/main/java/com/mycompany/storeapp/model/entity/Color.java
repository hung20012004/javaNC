/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.model.entity;

/**
 *
 * @author Hi
 */
public class Color {
    private long colorId;
    private String name;
    private String description;

    public Color() {}

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
}
