package com.mycompany.storeapp.view.component.shop;

import com.mycompany.storeapp.controller.admin.ProductImageController;
import com.mycompany.storeapp.model.entity.Product;
import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import javax.swing.border.EmptyBorder;

public class ProductCard extends JPanel {
    private Product product;
    private DecimalFormat currencyFormat;
    private Consumer<Product> addToCartCallback;
    private ProductImageController productImageController;
    private ConcurrentHashMap<String, ImageIcon> imageCache;
    private List<String> imageUrls;
    private JLabel imageLabel;
    private int currentImageIndex;
    private static final String DEFAULT_IMAGE_URL = "https://via.placeholder.com/100x80?text=No+Image";

    public ProductCard(Product product, DecimalFormat currencyFormat, Consumer<Product> addToCartCallback, 
                      ProductImageController productImageController, ConcurrentHashMap<String, ImageIcon> imageCache) {
        this.product = product;
        this.currencyFormat = currencyFormat;
        this.addToCartCallback = addToCartCallback;
        this.productImageController = productImageController;
        this.imageCache = imageCache;
        this.imageUrls = productImageController.getImageUrlsByProductId(product.getProductId());
        this.currentImageIndex = 0;
        setLayout(new BorderLayout());
        setBackground(new Color(249, 250, 251));
        setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235)));
        setPreferredSize(new Dimension(200, 200));
        System.out.println("ProductCard for product " + product.getProductId() + ": Image URLs = " + imageUrls); // Debug
        initializeComponents();
    }

    private void initializeComponents() {
        // Image display with navigation
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setOpaque(false);

        String imageUrl = imageUrls.isEmpty() ? DEFAULT_IMAGE_URL : imageUrls.get(currentImageIndex);
        imageLabel = new JLabel(imageCache.getOrDefault(imageUrl, new ImageIcon()));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        System.out.println("ProductCard " + product.getProductId() + ": Displaying image " + imageUrl); // Debug

        JButton prevButton = new JButton("◄");
        prevButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        prevButton.setBackground(new Color(59, 130, 246));
        prevButton.setForeground(Color.WHITE);
        prevButton.setFocusPainted(false);
        prevButton.setEnabled(!imageUrls.isEmpty() && imageUrls.size() > 1);
        prevButton.addActionListener(e -> {
            if (currentImageIndex > 0) {
                currentImageIndex--;
                updateImage();
            }
        });

        JButton nextButton = new JButton("►");
        nextButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        nextButton.setBackground(new Color(59, 130, 246));
        nextButton.setForeground(Color.WHITE);
        nextButton.setFocusPainted(false);
        nextButton.setEnabled(!imageUrls.isEmpty() && imageUrls.size() > 1);
        nextButton.addActionListener(e -> {
            if (currentImageIndex < imageUrls.size() - 1) {
                currentImageIndex++;
                updateImage();
            }
        });

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        navPanel.setOpaque(false);
        navPanel.add(prevButton);
        navPanel.add(nextButton);

        imagePanel.add(imageLabel, BorderLayout.CENTER);
        imagePanel.add(navPanel, BorderLayout.SOUTH);

        // Product info
        JLabel nameLabel = new JLabel(product.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        nameLabel.setForeground(new Color(55, 65, 81));
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);

        double displayPrice = product.getSalePrice() > 0 ? product.getSalePrice() : product.getPrice();
        JLabel priceLabel = new JLabel(currencyFormat.format(displayPrice));
        priceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        priceLabel.setForeground(new Color(16, 185, 129));
        priceLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton addButton = new JButton("Thêm");
        addButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        addButton.setBackground(new Color(59, 130, 246));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.addActionListener(e -> addToCartCallback.accept(product));

        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        infoPanel.add(nameLabel);
        infoPanel.add(priceLabel);

        add(infoPanel, BorderLayout.NORTH);
        add(imagePanel, BorderLayout.CENTER);
        add(addButton, BorderLayout.SOUTH);
    }

    private void updateImage() {
        String imageUrl = imageUrls.isEmpty() ? DEFAULT_IMAGE_URL : imageUrls.get(currentImageIndex);
        imageLabel.setIcon(imageCache.getOrDefault(imageUrl, new ImageIcon()));
        System.out.println("ProductCard " + product.getProductId() + ": Updated to image " + imageUrl); // Debug
    }
}