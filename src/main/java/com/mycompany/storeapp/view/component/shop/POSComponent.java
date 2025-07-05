package com.mycompany.storeapp.view.component.shop;

import com.mycompany.storeapp.controller.admin.CategoryController;
import com.mycompany.storeapp.controller.admin.ProductController;
import com.mycompany.storeapp.controller.admin.ProductImageController;
import com.mycompany.storeapp.model.entity.Category;
import com.mycompany.storeapp.model.entity.Product;
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
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color CARD_BACKGROUND = new Color(249, 250, 251);
    private static final Color BORDER_COLOR = new Color(229, 231, 235);
    private static final Color PRIMARY_COLOR = new Color(59, 130, 246);
    private static final Color SUCCESS_COLOR = new Color(16, 185, 129);
    private static final Color TEXT_COLOR = new Color(55, 65, 81);

    private final CartComponent cartComponent;
    private final CategoryController categoryController;
    private final ProductController productController;
    private final ProductImageController productImageController;
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

    public POSComponent(CartComponent cartComponent) {
        this.cartComponent = cartComponent;
        this.currencyFormat = new DecimalFormat("#,###,### ₫");
        this.categoryController = new CategoryController();
        this.productController = new ProductController();
        this.productImageController = new ProductImageController();
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        selectedCategory = "all";
        initializeData();
        add(new CategoryListComponent(categories, this::updateSelectedCategory), BorderLayout.WEST);
        productGridComponent = new ProductGridComponent(products, displayedProducts, imageCache, currencyFormat, this::addProductToCart, () -> selectedCategory, pageSize, productImageController);
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
        allCategory.setCategoryId(0); // Ensure a unique ID for "all"
        categories.add(allCategory);

        List<Category> dbCategories = categoryController.getActiveCategories();
        if (dbCategories != null && !dbCategories.isEmpty()) {
            categories.addAll(dbCategories);
            for (Category cat : dbCategories) {
                categoryIdToSlugMap.put(cat.getCategoryId(), cat.getSlug());
                System.out.println("Category ID: " + cat.getCategoryId() + ", Slug: " + cat.getSlug()); // Debug
            }
        } else {
            System.out.println("Warning: No categories loaded from database.");
        }

        List<Product> dbProducts = productController.getAllProducts();
        if (dbProducts != null && !dbProducts.isEmpty()) {
            products.addAll(dbProducts);
            for (Product product : products) {
                System.out.println("Product ID: " + product.getProductId() + ", Category ID: " + product.getCategoryId()); // Debug
            }
        } else {
            System.out.println("Warning: No products loaded from database.");
        }
        displayedProducts.addAll(products);
    }

    private void updateSelectedCategory(String categorySlug) {
        selectedCategory = categorySlug;
        System.out.println("Selected Category Slug: " + selectedCategory); // Debug
        filterProducts();
        productGridComponent.loadVisibleProducts(); // Refresh grid
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
        System.out.println("Filtered products: " + displayedProducts.size()); // Debug
        currentPage = 1;
    }

    private void addProductToCart(Product product) {
        if (product.getStockQuantity() <= 0) {
            JOptionPane.showMessageDialog(this,
                    "Sản phẩm " + product.getName() + " đã hết hàng!",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        cartComponent.addItem(product.getName(), currencyFormat.format(product.getSalePrice() > 0 ? product.getSalePrice() : product.getPrice()), 1);
        product.setStockQuantity(product.getStockQuantity() - 1);
    }
}