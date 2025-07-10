package com.mycompany.storeapp.view.component.shop.product;

import com.mycompany.storeapp.controller.admin.ColorController;
import com.mycompany.storeapp.controller.admin.SizeController;
import com.mycompany.storeapp.controller.admin.ProductImageController;
import com.mycompany.storeapp.controller.admin.ProductVariantController;
import com.mycompany.storeapp.model.entity.Product;
import com.mycompany.storeapp.model.entity.ProductVariant;
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
    private Consumer<ProductVariant> addToCartCallback;
    private ProductImageController productImageController;
    private ConcurrentHashMap<String, ImageIcon> imageCache;
    private List<String> imageUrls;
    private JLabel imageLabel;
    private int currentImageIndex;
    private static final String DEFAULT_IMAGE_URL = "https://via.placeholder.com/100x80?text=No+Image";
    private List<ProductVariant> variants;
    private ProductVariantController variantController;
    private ColorController colorController;
    private SizeController sizeController;
    private JFrame parentFrame;

    public ProductCard(JFrame parentFrame, Product product, DecimalFormat currencyFormat, Consumer<ProductVariant> addToCartCallback, 
                      ProductImageController productImageController, ConcurrentHashMap<String, ImageIcon> imageCache,
                      ProductVariantController variantController, ColorController colorController, SizeController sizeController) {
        this.parentFrame = parentFrame;
        this.product = product;
        this.currencyFormat = currencyFormat;
        this.addToCartCallback = addToCartCallback;
        this.productImageController = productImageController;
        this.imageCache = imageCache;
        this.variantController = variantController;
        this.colorController = colorController;
        this.sizeController = sizeController;
        this.imageUrls = productImageController.getImageUrlsByProductId(product.getProductId());
        this.currentImageIndex = 0;
        setLayout(new BorderLayout());
        setBackground(new java.awt.Color(249, 250, 251));
        setBorder(BorderFactory.createLineBorder(new java.awt.Color(229, 231, 235)));
        setPreferredSize(new Dimension(200, 200));
        initializeComponents();
    }

    private void initializeComponents() {
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setOpaque(false);

        String imageUrl = imageUrls.isEmpty() ? DEFAULT_IMAGE_URL : imageUrls.get(currentImageIndex);
        imageLabel = new JLabel(imageCache.getOrDefault(imageUrl, new ImageIcon()));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton prevButton = new JButton("◄");
        prevButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        prevButton.setBackground(new java.awt.Color(59, 130, 246));
        prevButton.setForeground(java.awt.Color.WHITE);
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
        nextButton.setBackground(new java.awt.Color(59, 130, 246));
        nextButton.setForeground(java.awt.Color.WHITE);
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

        JLabel nameLabel = new JLabel(product.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        nameLabel.setForeground(new java.awt.Color(55, 65, 81));
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);

        double displayPrice = product.getSalePrice() > 0 ? product.getSalePrice() : product.getPrice();
        JLabel priceLabel = new JLabel(currencyFormat.format(displayPrice));
        priceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        priceLabel.setForeground(new java.awt.Color(16, 185, 129));
        priceLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton selectVariantButton = new JButton("Chọn biến thể");
        selectVariantButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        selectVariantButton.setBackground(new java.awt.Color(59, 130, 246));
        selectVariantButton.setForeground(java.awt.Color.WHITE);
        selectVariantButton.setFocusPainted(false);
        selectVariantButton.addActionListener(e -> {
            ProductVariantDialog dialog = new ProductVariantDialog(
                parentFrame,
                product,
                currencyFormat,
                addToCartCallback,
                colorController,
                sizeController,
                variantController
            );
            dialog.setVisible(true);
        });

        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        infoPanel.add(nameLabel);
        infoPanel.add(priceLabel);

        add(infoPanel, BorderLayout.NORTH);
        add(imagePanel, BorderLayout.CENTER);
        add(selectVariantButton, BorderLayout.SOUTH);
    }

    private void updateImage() {
        String imageUrl = imageUrls.isEmpty() ? DEFAULT_IMAGE_URL : imageUrls.get(currentImageIndex);
        imageLabel.setIcon(imageCache.getOrDefault(imageUrl, new ImageIcon()));
    }
}