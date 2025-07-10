package com.mycompany.storeapp.view.component.shop;

import com.mycompany.storeapp.view.component.shop.product.ProductGridComponent;
import com.mycompany.storeapp.view.component.shop.cart.CartComponent;
import com.mycompany.storeapp.controller.admin.CategoryController;
import com.mycompany.storeapp.controller.admin.ColorController;
import com.mycompany.storeapp.controller.admin.SizeController;
import com.mycompany.storeapp.controller.admin.ProductController;
import com.mycompany.storeapp.controller.admin.ProductImageController;
import com.mycompany.storeapp.controller.admin.ProductVariantController;
import com.mycompany.storeapp.model.entity.Category;
import com.mycompany.storeapp.model.entity.Product;
import com.mycompany.storeapp.model.entity.ProductVariant;
import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class POSComponent extends JPanel {
    private static final java.awt.Color BACKGROUND_COLOR = java.awt.Color.WHITE;
    private static final java.awt.Color CARD_BACKGROUND = new java.awt.Color(249, 250, 251);
    private static final java.awt.Color BORDER_COLOR = new java.awt.Color(229, 231, 235);
    private static final java.awt.Color PRIMARY_COLOR = new java.awt.Color(59, 130, 246);
    private static final java.awt.Color SUCCESS_COLOR = new java.awt.Color(16, 185, 129);
    private static final java.awt.Color TEXT_COLOR = new java.awt.Color(55, 65, 81);

    private final CartComponent cartComponent;
    private final CategoryController categoryController;
    private final ProductController productController;
    private final ProductImageController productImageController;
    private final ProductVariantController variantController;
    private final ColorController colorController;
    private final SizeController sizeController;
    private final DecimalFormat currencyFormat;
    private List<Category> categories;
    private List<Product> products;
    private List<Product> displayedProducts;
    private Map<Integer, String> categoryIdToSlugMap;
    private String selectedCategory;
    private static final ConcurrentHashMap<String, ImageIcon> imageCache = new ConcurrentHashMap<>();
    private int pageSize = 12;
    private int currentPage = 1;
    private ProductGridComponent productGridComponent;
    private JFrame parentFrame;

    public POSComponent(JFrame parentFrame, CartComponent cartComponent) {
        this.parentFrame = parentFrame;
        this.cartComponent = cartComponent;
        this.currencyFormat = new DecimalFormat("#,###,### ₫");
        this.categoryController = new CategoryController();
        this.productController = new ProductController();
        this.productImageController = new ProductImageController();
        this.variantController = new ProductVariantController();
        this.colorController = new ColorController();
        this.sizeController = new SizeController();
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        selectedCategory = "all";
        initializeData();
        add(new CategoryListComponent(categories, this::updateSelectedCategory), BorderLayout.WEST);
        productGridComponent = new ProductGridComponent(parentFrame, products, displayedProducts, imageCache, currencyFormat, 
            this::addProductToCart, () -> String.valueOf(pageSize), productImageController, variantController, colorController, sizeController);
        add(productGridComponent, BorderLayout.CENTER);
    }

    private void initializeData() {
        categories = new ArrayList<>();
        products = new ArrayList<>();
        displayedProducts = new ArrayList<>();
        categoryIdToSlugMap = new HashMap<>();

        Category allCategory = new Category();
        allCategory.setSlug("all");
        allCategory.setName("Tất cả");
        allCategory.setCategoryId(0);
        categories.add(allCategory);

        List<Category> dbCategories = categoryController.getActiveCategories();
        if (dbCategories != null && !dbCategories.isEmpty()) {
            categories.addAll(dbCategories);
            for (Category cat : dbCategories) {
                categoryIdToSlugMap.put(cat.getCategoryId(), cat.getSlug());
            }
        }

        List<Product> dbProducts = productController.getAllProducts();
        if (dbProducts != null && !dbProducts.isEmpty()) {
            products.addAll(dbProducts);
        }
        displayedProducts.addAll(products);
    }

    private void updateSelectedCategory(String categorySlug) {
        selectedCategory = categorySlug;
        filterProducts();
        productGridComponent.loadVisibleProducts();
    }

    private void filterProducts() {
        displayedProducts.clear();
        if (selectedCategory.equals("all")) {
            displayedProducts.addAll(products);
        } else {
            for (Product product : products) {
                Integer productCategoryId = product.getCategoryId();
                String productSlug = categoryIdToSlugMap.getOrDefault(productCategoryId, "unknown");
                if (productCategoryId != null && productSlug.equals(selectedCategory)) {
                    displayedProducts.add(product);
                }
            }
        }
        currentPage = 1;
    }

    private void addProductToCart(ProductVariant variant) {
        if (variant.getStockQuantity() <= 0) {
            JOptionPane.showMessageDialog(this,
                "Sản phẩm " + variant.getProduct().getName() + " (Màu: " + 
                variant.getColor().getName() + ", Size: " + variant.getSize().getName() + 
                ") đã hết hàng!",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        cartComponent.addItem(variant.getVariantId(), 1); // Sử dụng variantId và số lượng 1
    }
}