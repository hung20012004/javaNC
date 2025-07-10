package com.mycompany.storeapp.view.component.shop;

import com.mycompany.storeapp.controller.admin.ColorController;
import com.mycompany.storeapp.controller.admin.SizeController;
import com.mycompany.storeapp.controller.admin.ProductVariantController;
import com.mycompany.storeapp.model.entity.Product;
import com.mycompany.storeapp.model.entity.ProductColor;
import com.mycompany.storeapp.model.entity.Size;
import com.mycompany.storeapp.model.entity.ProductVariant;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.text.DecimalFormat;
import java.util.function.Consumer;

public class ProductVariantDialog extends JDialog {
    private static final java.awt.Color BACKGROUND_COLOR = java.awt.Color.WHITE;
    private static final java.awt.Color BORDER_COLOR = new java.awt.Color(229, 231, 235);
    private static final java.awt.Color PRIMARY_COLOR = new java.awt.Color(59, 130, 246);
    private static final java.awt.Color TEXT_COLOR = new java.awt.Color(55, 65, 81);
    private static final java.awt.Color SUCCESS_COLOR = new java.awt.Color(16, 185, 129);

    private final Product product;
    private final DecimalFormat currencyFormat;
    private final Consumer<ProductVariant> addToCartCallback;
    private final ColorController colorController;
    private final SizeController sizeController;
    private final ProductVariantController variantController;
    private JComboBox<String> colorComboBox;
    private JComboBox<String> sizeComboBox;
    private JLabel priceLabel;
    private JLabel stockLabel;
    private JButton checkStockButton;
    private JButton addButton;
    private ProductVariant selectedVariant;

    public ProductVariantDialog(JFrame parent, Product product, DecimalFormat currencyFormat, 
                              Consumer<ProductVariant> addToCartCallback, ColorController colorController,
                              SizeController sizeController, ProductVariantController variantController) {
        super(parent, "Chọn biến thể - " + product.getName(), true);
        this.product = product;
        this.currencyFormat = currencyFormat;
        this.addToCartCallback = addToCartCallback;
        this.colorController = colorController;
        this.sizeController = sizeController;
        this.variantController = variantController;
        setLayout(new BorderLayout(10, 10));
        setBackground(BACKGROUND_COLOR);
        setSize(400, 350);
        setLocationRelativeTo(parent);
        initializeComponents();
    }

    private void initializeComponents() {
        JPanel mainPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel nameLabel = new JLabel("Sản phẩm:");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        nameLabel.setForeground(TEXT_COLOR);

        JLabel productName = new JLabel(product.getName());
        productName.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        productName.setForeground(TEXT_COLOR);

        JLabel colorLabel = new JLabel("Màu sắc:");
        colorLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        colorLabel.setForeground(TEXT_COLOR);

        colorComboBox = new JComboBox<>();
        colorComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        populateColors();

        JLabel sizeLabel = new JLabel("Kích thước:");
        sizeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        sizeLabel.setForeground(TEXT_COLOR);

        sizeComboBox = new JComboBox<>();
        sizeComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        populateSizes();

        JLabel priceTitleLabel = new JLabel("Giá:");
        priceTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        priceTitleLabel.setForeground(TEXT_COLOR);

        priceLabel = new JLabel("-");
        priceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        priceLabel.setForeground(SUCCESS_COLOR);

        JLabel stockTitleLabel = new JLabel("Tồn kho:");
        stockTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        stockTitleLabel.setForeground(TEXT_COLOR);

        stockLabel = new JLabel("-");
        stockLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        stockLabel.setForeground(TEXT_COLOR);

        mainPanel.add(nameLabel);
        mainPanel.add(productName);
        mainPanel.add(colorLabel);
        mainPanel.add(colorComboBox);
        mainPanel.add(sizeLabel);
        mainPanel.add(sizeComboBox);
        mainPanel.add(priceTitleLabel);
        mainPanel.add(priceLabel);
        mainPanel.add(stockTitleLabel);
        mainPanel.add(stockLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        JButton cancelButton = new JButton("Hủy");
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cancelButton.setBackground(BORDER_COLOR);
        cancelButton.setForeground(TEXT_COLOR);
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(e -> dispose());

        checkStockButton = new JButton("Xem tồn kho");
        checkStockButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        checkStockButton.setBackground(PRIMARY_COLOR);
        checkStockButton.setForeground(java.awt.Color.WHITE);
        checkStockButton.setFocusPainted(false);
        checkStockButton.addActionListener(e -> checkVariantStock());

        addButton = new JButton("Thêm vào giỏ");
        addButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        addButton.setBackground(PRIMARY_COLOR);
        addButton.setForeground(java.awt.Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setEnabled(false);
        addButton.addActionListener(e -> {
            if (selectedVariant != null && selectedVariant.getStockQuantity() > 0) {
                addToCartCallback.accept(selectedVariant);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Sản phẩm đã hết hàng hoặc chưa chọn biến thể!", 
                    "Lỗi", 
                    JOptionPane.WARNING_MESSAGE);
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(checkStockButton);
        buttonPanel.add(addButton);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void populateColors() {
        colorComboBox.addItem("Chọn màu sắc");
        List<ProductColor> colors = colorController.getAllColors();
        if (colors != null) {
            for (ProductColor color : colors) {
                if (!isColorInComboBox(color.getName())) {
                    colorComboBox.addItem(color.getName());
                }
            }
        }
    }

    private void populateSizes() {
        sizeComboBox.addItem("Chọn kích thước");
        List<Size> sizes = sizeController.getAllSizes();
        if (sizes != null) {
            for (Size size : sizes) {
                if (!isSizeInComboBox(size.getName())) {
                    sizeComboBox.addItem(size.getName());
                }
            }
        }
    }

    private boolean isColorInComboBox(String colorName) {
        for (int i = 0; i < colorComboBox.getItemCount(); i++) {
            if (colorComboBox.getItemAt(i).equals(colorName)) {
                return true;
            }
        }
        return false;
    }

    private boolean isSizeInComboBox(String sizeName) {
        for (int i = 0; i < sizeComboBox.getItemCount(); i++) {
            if (sizeComboBox.getItemAt(i).equals(sizeName)) {
                return true;
            }
        }
        return false;
    }

    private void checkVariantStock() {
        String selectedColorName = (String) colorComboBox.getSelectedItem();
        String selectedSizeName = (String) sizeComboBox.getSelectedItem();
        selectedVariant = null;

        if (selectedColorName == null || selectedColorName.equals("Chọn màu sắc") ||
            selectedSizeName == null || selectedSizeName.equals("Chọn kích thước")) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn màu sắc và kích thước!", 
                "Lỗi", 
                JOptionPane.WARNING_MESSAGE);
            priceLabel.setText("-");
            stockLabel.setText("-");
            addButton.setEnabled(false);
            return;
        }

        ProductColor selectedColor = colorController.getAllColors()
            .stream()
            .filter(c -> c.getName().equals(selectedColorName))
            .findFirst()
            .orElse(null);
        Size selectedSize = sizeController.getAllSizes()
            .stream()
            .filter(s -> s.getName().equals(selectedSizeName))
            .findFirst()
            .orElse(null);

        if (selectedColor == null || selectedSize == null) {
            JOptionPane.showMessageDialog(this, 
                "Không tìm thấy màu sắc hoặc kích thước!", 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
            priceLabel.setText("-");
            stockLabel.setText("-");
            addButton.setEnabled(false);
            return;
        }

        selectedVariant = variantController.getVariantByProductColorSize(
            product.getProductId(), 
            selectedColor.getColorId(), 
            selectedSize.getSizeId()
        );

        if (selectedVariant != null) {
            priceLabel.setText(currencyFormat.format(selectedVariant.getPrice()));
            stockLabel.setText(String.valueOf(selectedVariant.getStockQuantity()));
            addButton.setEnabled(selectedVariant.getStockQuantity() > 0);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Không tìm thấy biến thể với màu và kích thước đã chọn!", 
                "Thông báo", 
                JOptionPane.INFORMATION_MESSAGE);
            priceLabel.setText("-");
            stockLabel.setText("-");
            addButton.setEnabled(false);
        }
    }
}