/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.model.entity;

/**
 *
 * @author Hi
 */
import java.util.Set;

public class Tag {
    private Integer tagId;
    private String name;
    private String slug;

    // Quan hệ nhiều - nhiều với Product
    private Set<Product> products;

    public Tag() {}

    public Integer getTagId() {
        return tagId;
    }
    public void setTagId(Integer tagId) {
        this.tagId = tagId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }
    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Set<Product> getProducts() {
        return products;
    }
    public void setProducts(Set<Product> products) {
        this.products = products;
    }
}

