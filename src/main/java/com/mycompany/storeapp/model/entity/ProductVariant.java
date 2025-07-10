package com.mycompany.storeapp.model.entity;

import com.mycompany.storeapp.controller.admin.ColorController;
import com.mycompany.storeapp.controller.admin.SizeController;
import com.mycompany.storeapp.model.dao.ProductDAO;
import com.mycompany.storeapp.config.DatabaseConnection;
import java.math.BigDecimal;
import java.util.List;

public class ProductVariant {
    private int variantId;
    private long productId;
    private long colorId;
    private int sizeId;
    private String imageUrl;
    private int stockQuantity;
    private BigDecimal price;

    // Quan hệ đối tượng
    private Color color;
    private Size size;

    public ProductVariant() {}

    public int getVariantId() {
        return variantId;
    }

    public void setVariantId(int variantId) {
        this.variantId = variantId;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public long getColorId() {
        return colorId;
    }

    public void setColorId(long colorId) {
        this.colorId = colorId;
    }

    public int getSizeId() {
        return sizeId;
    }

    public void setSizeId(int sizeId) {
        this.sizeId = sizeId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Product getProduct() {
        ProductDAO productDAO = new ProductDAO(new DatabaseConnection());
        return productDAO.getProductById(productId);
    }

    public Color getColor() {
        return color;
    }

    public Size getSize() {
         return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public void setColor(Color color) {
        this.color = color;
    }
    
    public String getName() {
        Product product = getProduct();
        return (product != null) ? product.getName() : "Không xác định";
    }
}


