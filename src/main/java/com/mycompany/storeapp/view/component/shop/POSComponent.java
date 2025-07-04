package com.mycompany.storeapp.view.component.shop;

import com.mycompany.storeapp.controller.admin.CategoryController;
import com.mycompany.storeapp.controller.admin.ProductController;
import com.mycompany.storeapp.controller.admin.ProductImageController;
import com.mycompany.storeapp.model.entity.Category;
import com.mycompany.storeapp.model.entity.Product;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class POSComponent extends JPanel {
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color CARD_BACKGROUND = new Color(249, 250, 251);
    private static final Color BORDER_COLOR = new Color(229, 231, 235);
    private static final Color PRIMARY_COLOR = new Color(59, 130, 246);
    private static final Color SUCCESS_COLOR = new Color(16, 185, 129);
    private static final Color WARNING_COLOR = new Color(245, 158, 11);
    private static final Color TEXT_COLOR = new Color(55, 65, 81);
    private static final Color SECONDARY_TEXT_COLOR = new Color(107, 114, 128);

    private JTextField barcodeField;
    private JPanel categoryPanel;
    private JPanel productPanel;
    private JScrollPane productScrollPane;
    private CartComponent cartComponent;

    private final CategoryController categoryController;
    private final ProductController productController;
    private final ProductImageController productImageController;

    private List<ProductCategory> categories;
    private List<Product> products;
    private List<Product> displayedProducts;
    private Map<Integer, String> categoryIdToSlugMap;
    private String selectedCategory;
    private DecimalFormat currencyFormat;
    static final ConcurrentHashMap<String, ImageIcon> imageCache = new ConcurrentHashMap<>();
    private int pageSize = 12;
    private int currentPage = 1;

    public POSComponent(CartComponent cartComponent) {
        this.cartComponent = cartComponent;
        this.currencyFormat = new DecimalFormat("#,###,### ₫");
        this.categoryController = new CategoryController();
        this.productController = new ProductController();
        this.productImageController = new ProductImageController();

        try {
            initializeData();
            initializePOS();
            setupComponents();
            setupLayout();
            loadProducts();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khởi tạo giao diện: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initializeData() {
        categories = new ArrayList<>();
        products = new ArrayList<>();
        displayedProducts = new ArrayList<>();
        categoryIdToSlugMap = new HashMap<>();

        categories.add(new ProductCategory("all", "Tất cả", "📦", "#3B82F6"));

        List<Category> dbCategories = categoryController.getActiveCategories();
        System.out.println("Number of categories loaded: " + (dbCategories != null ? dbCategories.size() : "null"));
        if (dbCategories != null && !dbCategories.isEmpty()) {
            for (Category cat : dbCategories) {
                categories.add(new ProductCategory(cat.getSlug(), cat.getName(), "📁", generateRandomColor()));
                categoryIdToSlugMap.put(cat.getCategoryId(), cat.getSlug());
            }
        } else {
            System.out.println("No categories found, using fallback category only.");
        }

        List<com.mycompany.storeapp.model.entity.Product> dbProducts = productController.getAllProducts();
        System.out.println("Number of products loaded: " + (dbProducts != null ? dbProducts.size() : "null"));
        if (dbProducts != null && !dbProducts.isEmpty()) {
            for (com.mycompany.storeapp.model.entity.Product dbProduct : dbProducts) {
                List<String> imageUrls = productImageController.getImageUrlsByProductId(dbProduct.getProductId());
                System.out.println("Product ID " + dbProduct.getProductId() + " has " + imageUrls.size() + " images: " + imageUrls);
                String categorySlug = categoryIdToSlugMap.getOrDefault(dbProduct.getCategoryId(), "unknown");
                products.add(new Product(
                    String.valueOf(dbProduct.getProductId()),
                    dbProduct.getName(),
                    categorySlug,
                    dbProduct.getPrice(),
                    dbProduct.getSalePrice(),
                    imageUrls.isEmpty() ? List.of("https://via.placeholder.com/100x80?text=No+Image") : imageUrls,
                    dbProduct.getStockQuantity()
                ));
            }
            preloadImagesForPage(1);
        } else {
            System.out.println("No products found, product panel will be empty.");
        }
    }

    private void preloadImagesForPage(int page) {
        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, products.size());
        if (startIndex >= products.size()) return;

        List<Product> pageProducts = products.subList(startIndex, endIndex);
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                for (Product product : pageProducts) {
                    for (String url : product.getImages()) {
                        if (!imageCache.containsKey(url)) {
                            try {
                                ImageIcon icon = new ImageIcon(new URL(url));
                                Image scaledImage = icon.getImage().getScaledInstance(100, 80, Image.SCALE_SMOOTH);
                                imageCache.put(url, new ImageIcon(scaledImage));
                                System.out.println("Preloaded image from " + url);
                            } catch (Exception e) {
                                System.out.println("Failed to preload image from " + url + ": " + e.getMessage());
                                try {
                                    ImageIcon defaultIcon = new ImageIcon(new URL("https://via.placeholder.com/100x80?text=No+Image"));
                                    Image scaledImage = defaultIcon.getImage().getScaledInstance(100, 80, Image.SCALE_SMOOTH);
                                    imageCache.put(url, new ImageIcon(scaledImage));
                                } catch (Exception ex) {
                                    System.out.println("Failed to preload default image: " + ex.getMessage());
                                }
                            }
                        }
                    }
                }
                return null;
            }

            @Override
            protected void done() {
                loadVisibleProducts();
            }
        };
        worker.execute();
    }

    private String generateRandomColor() {
        String[] colors = {"#10B981", "#F59E0B", "#EF4444", "#8B5CF6", "#EC4899"};
        return colors[(int) (Math.random() * colors.length)];
    }

    private void initializePOS() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        selectedCategory = "all";
    }

    private void setupComponents() {
        JPanel topPanel = createBarcodePanel();
        JPanel leftPanel = createCategoryPanel();
        JPanel centerPanel = createProductPanel();

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(leftPanel, BorderLayout.WEST);
        contentPanel.add(centerPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private JPanel createBarcodePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                new EmptyBorder(15, 20, 15, 20)
        ));

        JLabel barcodeLabel = new JLabel("📷 Quét mã vạch:");
        barcodeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        barcodeLabel.setForeground(TEXT_COLOR);

        barcodeField = new JTextField();
        barcodeField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        barcodeField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                new EmptyBorder(10, 12, 10, 12)
        ));
        barcodeField.setPreferredSize(new Dimension(300, 40));

        barcodeField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleBarcodeInput();
                }
            }
        });

        JButton manualAddButton = new JButton("➕ Thêm thủ công");
        manualAddButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        manualAddButton.setBackground(PRIMARY_COLOR);
        manualAddButton.setForeground(Color.WHITE);
        manualAddButton.setBorder(new EmptyBorder(10, 15, 10, 15));
        manualAddButton.setFocusPainted(false);
        manualAddButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        manualAddButton.addActionListener(e -> showManualAddDialog());

        JPanel leftSection = new JPanel(new BorderLayout());
        leftSection.setOpaque(false);
        leftSection.add(barcodeLabel, BorderLayout.NORTH);
        leftSection.add(Box.createVerticalStrut(8), BorderLayout.CENTER);

        JPanel inputSection = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        inputSection.setOpaque(false);
        inputSection.add(barcodeField);
        inputSection.add(Box.createHorizontalStrut(10));
        inputSection.add(manualAddButton);

        panel.add(leftSection, BorderLayout.NORTH);
        panel.add(inputSection, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createCategoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 10));
        panel.setPreferredSize(new Dimension(200, 0));

        JLabel categoryLabel = new JLabel("Danh mục sản phẩm");
        categoryLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        categoryLabel.setForeground(TEXT_COLOR);
        categoryLabel.setBorder(new EmptyBorder(0, 0, 15, 0));

        categoryPanel = new JPanel();
        categoryPanel.setLayout(new BoxLayout(categoryPanel, BoxLayout.Y_AXIS));
        categoryPanel.setBackground(BACKGROUND_COLOR);

        for (ProductCategory category : categories) {
            JButton categoryButton = createCategoryButton(category);
            categoryPanel.add(categoryButton);
            categoryPanel.add(Box.createVerticalStrut(8));
        }

        JScrollPane categoryScrollPane = new JScrollPane(categoryPanel);
        categoryScrollPane.setBorder(null);
        categoryScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        categoryScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        panel.add(categoryLabel, BorderLayout.NORTH);
        panel.add(categoryScrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JButton createCategoryButton(ProductCategory category) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout());
        button.setBackground(category.getId().equals(selectedCategory) ? PRIMARY_COLOR : Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                new EmptyBorder(12, 15, 12, 15)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(170, 50));
        button.setMaximumSize(new Dimension(170, 50));

        JLabel iconLabel = new JLabel(category.getIcon());
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));

        JLabel nameLabel = new JLabel(category.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        nameLabel.setForeground(category.getId().equals(selectedCategory) ? Color.WHITE : TEXT_COLOR);

        JPanel textPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        textPanel.setOpaque(false);
        textPanel.add(iconLabel);
        textPanel.add(nameLabel);

        button.add(textPanel, BorderLayout.CENTER);

        button.addActionListener(e -> {
            selectedCategory = category.getId();
            refreshCategoryButtons();
            filterProducts();
        });

        return button;
    }

    private JPanel createProductPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 10, 20, 20));

        JLabel productLabel = new JLabel("Sản phẩm");
        productLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        productLabel.setForeground(TEXT_COLOR);
        productLabel.setBorder(new EmptyBorder(0, 0, 15, 0));

        productPanel = new JPanel();
        productPanel.setLayout(new GridLayout(0, 4, 15, 15));
        productPanel.setBackground(BACKGROUND_COLOR);

        productScrollPane = new JScrollPane(productPanel);
        productScrollPane.setBorder(null);
        productScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        productScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        productScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        productScrollPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                loadVisibleProducts();
            }
        });

        panel.add(productLabel, BorderLayout.NORTH);
        panel.add(productScrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void setupLayout() {
        refreshCategoryButtons();
        filterProducts();
    }

    private void loadProducts() {
        displayedProducts.clear();
        displayedProducts.addAll(products);
        loadVisibleProducts();
    }

    private void filterProducts() {
        displayedProducts.clear();
        if (selectedCategory.equals("all")) {
            displayedProducts.addAll(products);
        } else {
            for (Product product : products) {
                if (product.getCategory().equals(selectedCategory)) {
                    displayedProducts.add(product);
                }
            }
        }
        currentPage = 1;
        loadVisibleProducts();
    }

    private void loadVisibleProducts() {
        productPanel.removeAll();
        int startIndex = (currentPage - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, displayedProducts.size());
        if (startIndex >= displayedProducts.size()) return;

        List<Product> pageProducts = displayedProducts.subList(startIndex, endIndex);
        for (Product product : pageProducts) {
            try {
                ProductCard productCard = new ProductCard(product, currencyFormat, this::addProductToCart);
                productPanel.add(productCard);
            } catch (Exception e) {
                System.out.println("Error creating ProductCard for product " + product.getName() + ": " + e.getMessage());
            }
        }

        SwingUtilities.invokeLater(() -> {
            int panelWidth = productScrollPane.getViewport().getWidth();
            int cardWidth = 200;
            int columns = Math.max(1, panelWidth / cardWidth);
            productPanel.setLayout(new GridLayout(0, columns, 15, 15));
            productPanel.revalidate();
            productPanel.repaint();
        });
    }

    private void refreshCategoryButtons() {
        categoryPanel.removeAll();
        for (ProductCategory category : categories) {
            JButton categoryButton = createCategoryButton(category);
            categoryPanel.add(categoryButton);
            categoryPanel.add(Box.createVerticalStrut(8));
        }
        categoryPanel.revalidate();
        categoryPanel.repaint();
    }

    private void handleBarcodeInput() {
        String barcode = barcodeField.getText().trim();
        if (barcode.isEmpty()) return;

        Product foundProduct = findProductByBarcode(barcode);
        if (foundProduct != null) {
            addProductToCart(foundProduct);
            barcodeField.setText("");
            barcodeField.requestFocus();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Không tìm thấy sản phẩm với mã: " + barcode,
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            barcodeField.selectAll();
        }
    }

    private Product findProductByBarcode(String barcode) {
        for (Product product : products) {
            if (product.getId().equals(barcode)) {
                return product;
            }
        }
        return null;
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
        loadVisibleProducts();
        showAddToCartNotification(product);
    }

    private void showAddToCartNotification(Product product) {
        JLabel notification = new JLabel("✓ Đã thêm " + product.getName() + " vào giỏ hàng");
        notification.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        notification.setForeground(SUCCESS_COLOR);
        notification.setOpaque(true);
        notification.setBackground(new Color(240, 253, 244));
        notification.setBorder(new EmptyBorder(8, 12, 8, 12));
    }

    private void showManualAddDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm sản phẩm thủ công", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel.add(new JLabel("Sản phẩm:"), gbc);

        JComboBox<Product> productCombo = new JComboBox<>();
        for (Product product : products) {
            if (product.getStockQuantity() > 0) {
                productCombo.addItem(product);
            }
        }
        productCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Product) {
                    Product p = (Product) value;
                    setText(p.getName() + " - " + currencyFormat.format(p.getSalePrice() > 0 ? p.getSalePrice() : p.getPrice()));
                }
                return this;
            }
        });

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(productCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JLabel("Số lượng:"), gbc);

        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        gbc.gridx = 1;
        panel.add(quantitySpinner, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Thêm vào giỏ");
        JButton cancelButton = new JButton("Hủy");

        addButton.setBackground(PRIMARY_COLOR);
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);

        addButton.addActionListener(e -> {
            Product selectedProduct = (Product) productCombo.getSelectedItem();
            int quantity = (Integer) quantitySpinner.getValue();

            if (selectedProduct != null && selectedProduct.getStockQuantity() >= quantity) {
                for (int i = 0; i < quantity; i++) {
                    addProductToCart(selectedProduct);
                }
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog,
                        "Không đủ hàng trong kho!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(buttonPanel, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    public static class Product {
        private String id;
        private String name;
        private String category;
        private double price;
        private double salePrice;
        private List<String> images;
        private int stockQuantity;

        public Product(String id, String name, String category, double price, double salePrice, List<String> images, int stockQuantity) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.price = price;
            this.salePrice = salePrice;
            this.images = images;
            this.stockQuantity = stockQuantity;
        }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }

        public double getSalePrice() { return salePrice; }
        public void setSalePrice(double salePrice) { this.salePrice = salePrice; }

        public List<String> getImages() { return images; }
        public void setImages(List<String> images) { this.images = images; }

        public int getStockQuantity() { return stockQuantity; }
        public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
    }

    public static class ProductCategory {
        private String id;
        private String name;
        private String icon;
        private String color;

        public ProductCategory(String id, String name, String icon, String color) {
            this.id = id;
            this.name = name;
            this.icon = icon;
            this.color = color;
        }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }

        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }
    }
}