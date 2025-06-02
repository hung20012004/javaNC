/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.model.entity;

/**
 *
 * @author Hi
 */
public class Material {
    private long materialId;
    private String name;
    private String description;

    public Material() {}

    public Material(long materialId, String name, String description) {
        this.materialId = materialId;
        this.name = name;
        this.description = description;
    }

    public long getMaterialId() { return materialId; }
    public void setMaterialId(long materialId) { this.materialId = materialId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
