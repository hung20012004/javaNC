package com.mycompany.storeapp.controller.admin;

import com.mycompany.storeapp.config.DatabaseConnection;
import com.mycompany.storeapp.model.dao.ProductImageDAO;
import com.mycompany.storeapp.model.entity.ProductImage;
import java.util.ArrayList;
import java.util.List;

public class ProductImageController {
    private final ProductImageDAO productImageDAO;

    public ProductImageController() {
        this.productImageDAO = new ProductImageDAO(new DatabaseConnection());
    }

    public List<String> getImageUrlsByProductId(long productId) {
        List<ProductImage> images = productImageDAO.getImagesByProductId(productId);
        List<String> imageUrls = new ArrayList<>();
        for (ProductImage image : images) {
            imageUrls.add(image.getImageUrl());
        }
        return imageUrls;
    }
}