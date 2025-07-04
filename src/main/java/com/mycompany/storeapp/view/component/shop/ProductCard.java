package com.mycompany.storeapp.view.component.shop;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;
import java.util.function.Consumer;

public class ProductCard extends JPanel {
    private static final Color CARD_BACKGROUND = new Color(249, 250, 251);
    private static final Color BORDER_COLOR = new Color(229, 231, 235);
    private static final Color PRIMARY_COLOR = new Color(59, 130, 246);
    private static final Color SUCCESS_COLOR = new Color(16, 185, 129);
    private static final Color WARNING_COLOR = new Color(245, 158, 11);
    private static final Color TEXT_COLOR = new Color(55, 65, 81);
    private static final String DEFAULT_IMAGE = "https://via.placeholder.com/100x80?text=No+Image";

    private JLabel imageLabel;
    private List<String> imageUrls;
    private int currentImageIndex;
    private JButton prevButton;
    private JButton nextButton;

    public ProductCard(POSComponent.Product product, DecimalFormat currencyFormat, Consumer<POSComponent.Product> addToCartCallback) {
        this.imageUrls = product.getImages();
        this.currentImageIndex = 0;

        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                new EmptyBorder(12, 12, 12, 12)
        ));
        setPreferredSize(new Dimension(180, 240));
        setMinimumSize(new Dimension(180, 240));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setLayout(new BorderLayout());

        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setOpaque(false);

        prevButton = new JButton("◄");
        prevButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        prevButton.setBackground(Color.WHITE);
        prevButton.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        prevButton.setFocusPainted(false);
        prevButton.setEnabled(imageUrls.size() > 1);
        prevButton.addActionListener(e -> {
            if (currentImageIndex > 0) {
                currentImageIndex--;
                updateImage();
            }
        });

        nextButton = new JButton("►");
        nextButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        nextButton.setBackground(Color.WHITE);
        nextButton.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        nextButton.setFocusPainted(false);
        nextButton.setEnabled(imageUrls.size() > 1);
        nextButton.addActionListener(e -> {
            if (currentImageIndex < imageUrls.size() - 1) {
                currentImageIndex++;
                updateImage();
            }
        });

        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setPreferredSize(new Dimension(100, 80));
        imageLabel.setMinimumSize(new Dimension(100, 80));
        imageLabel.setBackground(CARD_BACKGROUND);
        imageLabel.setOpaque(true);
        imageLabel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        updateImage();

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(prevButton);
        buttonPanel.add(nextButton);

        imagePanel.add(buttonPanel, BorderLayout.NORTH);
        imagePanel.add(imageLabel, BorderLayout.CENTER);

        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(new EmptyBorder(8, 0, 0, 0));

        JLabel nameLabel = new JLabel("<html>" + product.getName() + "</html>");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        nameLabel.setForeground(TEXT_COLOR);
        nameLabel.setHorizontalAlignment(JLabel.CENTER);

        JLabel priceLabel = new JLabel(currencyFormat.format(product.getSalePrice() > 0 ? product.getSalePrice() : product.getPrice()));
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        priceLabel.setForeground(PRIMARY_COLOR);
        priceLabel.setHorizontalAlignment(JLabel.CENTER);

        JLabel stockLabel = new JLabel("Kho: " + product.getStockQuantity());
        stockLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        stockLabel.setForeground(product.getStockQuantity() > 10 ? SUCCESS_COLOR : WARNING_COLOR);
        stockLabel.setHorizontalAlignment(JLabel.CENTER);

        infoPanel.add(nameLabel, BorderLayout.NORTH);
        infoPanel.add(priceLabel, BorderLayout.CENTER);
        infoPanel.add(stockLabel, BorderLayout.SOUTH);

        add(imagePanel, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.SOUTH);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1 && !e.getComponent().equals(prevButton) && !e.getComponent().equals(nextButton)) {
                    addToCartCallback.accept(product);
                }
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(CARD_BACKGROUND);
                repaint();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(Color.WHITE);
                repaint();
            }
        });

        revalidate();
        repaint();
    }

    private void updateImage() {
        imageLabel.setIcon(null);
        imageLabel.setText("Loading...");
        imageLabel.revalidate();
        imageLabel.repaint();
        loadImageAsync();
    }

    private void loadImageAsync() {
        SwingWorker<ImageIcon, Void> worker = new SwingWorker<ImageIcon, Void>() {
            @Override
            protected ImageIcon doInBackground() {
                String url = imageUrls.isEmpty() || currentImageIndex >= imageUrls.size() ? DEFAULT_IMAGE : imageUrls.get(currentImageIndex);
                ImageIcon cachedIcon = POSComponent.imageCache.get(url);
                if (cachedIcon != null) {
                    return cachedIcon;
                }
                try {
                    ImageIcon icon = new ImageIcon(new URL(url));
                    Image scaledImage = icon.getImage().getScaledInstance(100, 80, Image.SCALE_SMOOTH);
                    ImageIcon scaledIcon = new ImageIcon(scaledImage);
                    POSComponent.imageCache.put(url, scaledIcon);
                    return scaledIcon;
                } catch (Exception e) {
                    System.out.println("Failed to load image from " + url + ": " + e.getMessage());
                    try {
                        ImageIcon defaultIcon = new ImageIcon(new URL(DEFAULT_IMAGE));
                        Image scaledImage = defaultIcon.getImage().getScaledInstance(100, 80, Image.SCALE_SMOOTH);
                        POSComponent.imageCache.put(url, new ImageIcon(scaledImage));
                        return new ImageIcon(scaledImage);
                    } catch (Exception ex) {
                        System.out.println("Failed to load default image: " + ex.getMessage());
                        return null;
                    }
                }
            }

            @Override
            protected void done() {
                try {
                    ImageIcon icon = get();
                    SwingUtilities.invokeLater(() -> {
                        if (icon != null) {
                            imageLabel.setIcon(icon);
                            imageLabel.setText(null);
                        } else {
                            imageLabel.setIcon(null);
                            imageLabel.setText("No Image");
                        }
                        imageLabel.revalidate();
                        imageLabel.repaint();
                        updateButtonStates();
                    });
                } catch (Exception e) {
                    System.out.println("Error in SwingWorker: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void updateButtonStates() {
        prevButton.setEnabled(currentImageIndex > 0);
        nextButton.setEnabled(currentImageIndex < imageUrls.size() - 1);
    }
}