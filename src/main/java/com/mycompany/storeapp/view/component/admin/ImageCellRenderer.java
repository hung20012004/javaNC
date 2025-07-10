package com.mycompany.storeapp.view.component.admin;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

import com.mycompany.storeapp.view.component.CustomTable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;

public class ImageCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(true);
        
        if (!isSelected) {
            if (row % 2 == 0) {
                panel.setBackground(Color.WHITE);
            } else {
                panel.setBackground(new Color(248, 249, 250));
            }
        } else {
            panel.setBackground(new Color(217, 237, 247));
        }
        
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        
        String imageUrl = value != null ? value.toString().trim() : "";
        
        if (imageUrl.isEmpty()) {
            imageLabel.setIcon(CustomTable.getDefaultImage());
        } else {
            ImageIcon cachedIcon = CustomTable.getImageCache().get(imageUrl);
            if (cachedIcon != null) {
                imageLabel.setIcon(cachedIcon);
            } else {
                imageLabel.setIcon(CustomTable.getLoadingImage());
                
                SwingWorker<ImageIcon, Void> worker = new SwingWorker<ImageIcon, Void>() {
                    @Override
                    protected ImageIcon doInBackground() throws Exception {
                        return loadImageFromUrl(imageUrl);
                    }
                    
                    @Override
                    protected void done() {
                        try {
                            ImageIcon icon = get();
                            if (icon != null) {
                                CustomTable.getImageCache().put(imageUrl, icon);
                                SwingUtilities.invokeLater(() -> {
                                    table.repaint();
                                });
                            }
                        } catch (Exception e) {
                            System.err.println("Error loading image: " + imageUrl);
                        }
                    }
                };
                worker.execute();
            }
        }
        
        panel.add(imageLabel, BorderLayout.CENTER);
        return panel;
    }
    
    private ImageIcon loadImageFromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            BufferedImage originalImage = ImageIO.read(url);
            
            if (originalImage != null) {
                Image scaledImage = originalImage.getScaledInstance(80, 80, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImage);
            }
        } catch (Exception e) {
            System.err.println("Failed to load image from URL: " + imageUrl);
        }
        
        return CustomTable.getDefaultImage();
    }
}