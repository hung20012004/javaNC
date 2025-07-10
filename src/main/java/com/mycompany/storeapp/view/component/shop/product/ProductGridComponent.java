package com.mycompany.storeapp.view.component.shop.product;

import com.mycompany.storeapp.view.component.shop.product.ProductCard;
import com.mycompany.storeapp.controller.admin.ColorController;
import com.mycompany.storeapp.controller.admin.SizeController;
import com.mycompany.storeapp.controller.admin.ProductImageController;
import com.mycompany.storeapp.controller.admin.ProductVariantController;
import com.mycompany.storeapp.model.entity.Product;
import com.mycompany.storeapp.model.entity.ProductVariant;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import javax.swing.border.EmptyBorder;

public class ProductGridComponent extends JPanel {
    private List<Product> products;
    private List<Product> displayedProducts;
    private ConcurrentHashMap<String, ImageIcon> imageCache;
    private JPanel productPanel;
    private JScrollPane productScrollPane;
    private DecimalFormat currencyFormat;
    private Consumer<ProductVariant> addToCartCallback;
    private java.util.function.Supplier<String> selectedCategorySupplier;
    private int pageSize;
    private int currentPage = 1;
    private ProductImageController productImageController;
    private ProductVariantController variantController;
    private ColorController colorController;
    private SizeController sizeController;
    private JFrame parentFrame;

    public ProductGridComponent(JFrame parentFrame, List<Product> products, List<Product> displayedProducts, 
                              ConcurrentHashMap<String, ImageIcon> imageCache, DecimalFormat currencyFormat, 
                              Consumer<ProductVariant> addToCartCallback, 
                              java.util.function.Supplier<String> pageSizeSupplier, 
                              ProductImageController productImageController,
                              ProductVariantController variantController,
                              ColorController colorController, SizeController sizeController) {
        this.parentFrame = parentFrame;
        this.products = products;
        this.displayedProducts = displayedProducts;
        this.imageCache = imageCache;
        this.currencyFormat = currencyFormat;
        this.addToCartCallback = addToCartCallback;
        this.selectedCategorySupplier = pageSizeSupplier; // Sửa lại để sử dụng pageSizeSupplier
        this.pageSize = Integer.parseInt(pageSizeSupplier.get());
        this.productImageController = productImageController;
        this.variantController = variantController;
        this.colorController = colorController;
        this.sizeController = sizeController;
        setLayout(new BorderLayout());
        setBackground(java.awt.Color.WHITE);
        setBorder(new EmptyBorder(20, 10, 20, 20));
        initializeComponents();
        preloadImagesForPage(currentPage);
    }

    private void initializeComponents() {
        JLabel productLabel = new JLabel("Sản phẩm");
        productLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        productLabel.setForeground(new java.awt.Color(55, 65, 81));
        productLabel.setBorder(new EmptyBorder(0, 0, 15, 0));

        productPanel = new JPanel();
        productPanel.setLayout(new GridLayout(0, 4, 15, 15));
        productPanel.setBackground(java.awt.Color.WHITE);

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

        add(productLabel, BorderLayout.NORTH);
        add(productScrollPane, BorderLayout.CENTER);
    }

    public void loadVisibleProducts() {
        productPanel.removeAll();
        int startIndex = (currentPage - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, displayedProducts.size());
        if (startIndex >= displayedProducts.size()) return;

        List<Product> pageProducts = displayedProducts.subList(startIndex, endIndex);
        for (Product product : pageProducts) {
            try {
                ProductCard productCard = new ProductCard(parentFrame, product, currencyFormat, addToCartCallback, 
                    productImageController, imageCache, variantController, colorController, sizeController);
                productPanel.add(productCard);
            } catch (Exception e) {
                System.err.println("Error creating ProductCard for product " + product.getProductId());
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

    public void preloadImagesForPage(int page) {
        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, products.size());
        if (startIndex >= products.size()) return;

        List<Product> pageProducts = products.subList(startIndex, endIndex);
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                for (Product product : pageProducts) {
                    List<String> imageUrls = productImageController.getImageUrlsByProductId(product.getProductId());
                    for (String url : imageUrls.isEmpty() ? List.of("https://via.placeholder.com/100x80?text=No+Image") : imageUrls) {
                        if (!imageCache.containsKey(url)) {
                            try {
                                ImageIcon icon = new ImageIcon(new URL(url));
                                Image scaledImage = icon.getImage().getScaledInstance(100, 80, Image.SCALE_SMOOTH);
                                imageCache.put(url, new ImageIcon(scaledImage));
                            } catch (Exception e) {
                                try {
                                    ImageIcon defaultIcon = new ImageIcon(new URL("https://via.placeholder.com/100x80?text=No+Image"));
                                    Image scaledImage = defaultIcon.getImage().getScaledInstance(100, 80, Image.SCALE_SMOOTH);
                                    imageCache.put(url, new ImageIcon(scaledImage));
                                } catch (Exception ex) {
                                    System.err.println("Failed to load default image");
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
}