package com.mycompany.storeapp.view.component.admin;

import com.mycompany.storeapp.model.entity.ProductVariant;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * Custom cell renderer for displaying ProductVariant data in table cells
 * with interactive color and size selection functionality
 */
public class VariantCellRenderer extends DefaultTableCellRenderer {
    
    private final JTable parentTable;
    private final List<?> currentData;
    
    public VariantCellRenderer(JTable parentTable, List<?> currentData) {
        this.parentTable = parentTable;
        this.currentData = currentData;
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        System.out.println("Rendering variant cell for row: " + row + ", column: " + column);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(true);

        if (!isSelected) {
            if (row % 2 == 0) {
                mainPanel.setBackground(Color.WHITE);
            } else {
                mainPanel.setBackground(new Color(248, 249, 250));
            }
        } else {
            mainPanel.setBackground(new Color(217, 237, 247));
        }

        List<ProductVariant> variants = getVariantsForRow(row);

        if (variants == null || variants.isEmpty()) {
            JLabel noVariantLabel = new JLabel("Không có variant");
            noVariantLabel.setForeground(new Color(150, 150, 150));
            noVariantLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            noVariantLabel.setHorizontalAlignment(SwingConstants.CENTER);
            mainPanel.add(noVariantLabel, BorderLayout.CENTER);
            
            System.out.println("No variants found for row " + row);
            return mainPanel;
        }

        System.out.println("Creating variant panel for " + variants.size() + " variants");
        JPanel variantPanel = createVariantPanel(variants, row);
        mainPanel.add(variantPanel, BorderLayout.CENTER);

        return mainPanel;
    }

    private List<ProductVariant> getVariantsForRow(int row) {
        if (currentData == null || row >= currentData.size()) {
            System.out.println("No data for row: " + row);
            return null;
        }

        Object item = currentData.get(row);
        System.out.println("Row " + row + ", Item class: " + item.getClass().getName());

        if (item.getClass().getSimpleName().equals("EnhancedProduct")) {
            try {
                java.lang.reflect.Method getVariants = item.getClass().getMethod("getVariants");
                @SuppressWarnings("unchecked")
                List<ProductVariant> variants = (List<ProductVariant>) getVariants.invoke(item);
                System.out.println("Found " + (variants != null ? variants.size() : 0) + " variants for row " + row);
                return variants;
            } catch (Exception e) {
                System.err.println("Error getting variants for row " + row + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

       try {
            java.lang.reflect.Method getVariants = item.getClass().getMethod("getVariants");
            @SuppressWarnings("unchecked")
            List<ProductVariant> variants = (List<ProductVariant>) getVariants.invoke(item);
            System.out.println("Found " + (variants != null ? variants.size() : 0) + " variants via fallback for row " + row);
            return variants;
        } catch (Exception e) {
            System.err.println("Fallback failed for row " + row + ": " + e.getMessage());
        }

        return null;
    }

    private JPanel createVariantPanel(List<ProductVariant> variants, int row) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        // Group variants by color
        Map<String, List<ProductVariant>> colorGroups = new HashMap<>();
        for (ProductVariant variant : variants) {
            String colorKey = getColorKey(variant);
            colorGroups.computeIfAbsent(colorKey, k -> new ArrayList<>()).add(variant);
        }

        // Create interactive variant display
        int colorCount = 0;
        for (Map.Entry<String, List<ProductVariant>> entry : colorGroups.entrySet()) {
            if (colorCount >= 3) { // Limit to 3 colors for display
                break;
            }

            String colorKey = entry.getKey();
            List<ProductVariant> colorVariants = entry.getValue();

            JPanel colorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
            colorPanel.setOpaque(false);

            // Create color display component
            JComponent colorDisplay = createColorDisplay(colorVariants.get(0), colorKey);
            colorPanel.add(colorDisplay);

            // Size labels (clickable)
            for (ProductVariant variant : colorVariants) {
                String size = variant.getSize() != null ? variant.getSize().getName() : "Free";
                boolean hasStock = variant.getStockQuantity() > 0;

                JLabel sizeLabel = new JLabel(size);
                sizeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                sizeLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                sizeLabel.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
                sizeLabel.setOpaque(true);

                if (hasStock) {
                    sizeLabel.setForeground(new Color(39, 174, 96));
                    sizeLabel.setBackground(new Color(232, 245, 237));
                } else {
                    sizeLabel.setForeground(new Color(189, 195, 199));
                    sizeLabel.setBackground(new Color(245, 245, 245));
                }

                // Add tooltip with stock info
                sizeLabel.setToolTipText(String.format("Size: %s, Kho: %d", size, variant.getStockQuantity()));

                // Add click listener for size
                sizeLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        highlightVariantsBySize(size, variants, row);
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        if (hasStock) {
                            sizeLabel.setBackground(new Color(212, 235, 222));
                        } else {
                            sizeLabel.setBackground(new Color(235, 235, 235));
                        }
                        sizeLabel.repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        if (hasStock) {
                            sizeLabel.setBackground(new Color(232, 245, 237));
                        } else {
                            sizeLabel.setBackground(new Color(245, 245, 245));
                        }
                        sizeLabel.repaint();
                    }
                });

                colorPanel.add(sizeLabel);
            }

            panel.add(colorPanel);
            colorCount++;
        }

        // Add "more" indicator if there are more colors
        if (colorGroups.size() > 3) {
            JLabel moreLabel = new JLabel("+" + (colorGroups.size() - 3) + " màu khác");
            moreLabel.setFont(new Font("Segoe UI", Font.ITALIC, 10));
            moreLabel.setForeground(new Color(127, 140, 141));
            moreLabel.setHorizontalAlignment(SwingConstants.CENTER);
            moreLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            moreLabel.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));

            // Add click listener to show all variants
            moreLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    showAllVariants(variants, row);
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    moreLabel.setForeground(new Color(52, 152, 219));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    moreLabel.setForeground(new Color(127, 140, 141));
                }
            });

            panel.add(moreLabel);
        }

        return panel;
    }

    private String getColorKey(ProductVariant variant) {
        if (variant.getColor() != null) {
            return variant.getColor().getName() + "|" + 
                   (variant.getColor().getDescription() != null ? variant.getColor().getDescription() : "");
        }
        return "Mặc định|#CCCCCC";
    }

    private JComponent createColorDisplay(ProductVariant variant, String colorKey) {
        JPanel colorPanel = new JPanel(new BorderLayout());
        colorPanel.setOpaque(false);
        colorPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        String[] parts = colorKey.split("\\|");
        String colorName = parts[0];
        String colorCode = parts.length > 1 ? parts[1] : "#CCCCCC";

        // Create color circle
        JPanel colorCircle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Parse color code
                Color color = parseColor(colorCode);

                // Draw color circle
                g2d.setColor(color);
                g2d.fillOval(2, 2, 16, 16);

                // Draw border
                g2d.setColor(new Color(189, 195, 199));
                g2d.drawOval(2, 2, 16, 16);

                g2d.dispose();
            }
        };

        colorCircle.setPreferredSize(new Dimension(20, 20));
        colorCircle.setOpaque(false);

        // Create color name label
        JLabel colorLabel = new JLabel(colorName);
        colorLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        colorLabel.setForeground(new Color(52, 73, 94));
        colorLabel.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));

        colorPanel.add(colorCircle, BorderLayout.WEST);
        colorPanel.add(colorLabel, BorderLayout.CENTER);

        // Add tooltip
        colorPanel.setToolTipText(String.format("Màu: %s (%s)", colorName, colorCode));

        // Add click listener
        colorPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                highlightVariantsByColor(colorName, getVariantsForRow(getRowFromComponent(colorPanel)), 
                                       getRowFromComponent(colorPanel));
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                colorPanel.setOpaque(true);
                colorPanel.setBackground(new Color(230, 230, 230));
                colorPanel.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                colorPanel.setOpaque(false);
                colorPanel.repaint();
            }
        });

        return colorPanel;
    }

    private Color parseColor(String colorCode) {
        try {
            // Remove # if present
            if (colorCode.startsWith("#")) {
                colorCode = colorCode.substring(1);
            }

            // Parse hex color
            if (colorCode.length() == 6) {
                int r = Integer.parseInt(colorCode.substring(0, 2), 16);
                int g = Integer.parseInt(colorCode.substring(2, 4), 16);
                int b = Integer.parseInt(colorCode.substring(4, 6), 16);
                return new Color(r, g, b);
            }
        } catch (Exception e) {
            // Fall back to default color
        }

        return new Color(204, 204, 204); // Default gray
    }

    private int getRowFromComponent(Component component) {
        // Find the row by traversing up the component hierarchy
        Component parent = component;
        while (parent != null && !(parent instanceof JTable)) {
            parent = parent.getParent();
        }

        if (parent instanceof JTable) {
            JTable table = (JTable) parent;
            Point point = SwingUtilities.convertPoint(component, new Point(0, 0), table);
            return table.rowAtPoint(point);
        }

        return -1;
    }

    private void highlightVariantsByColor(String selectedColor, List<ProductVariant> variants, int row) {
        if (variants == null) return;

        // Get all sizes available for the selected color
        List<String> availableSizes = new ArrayList<>();
        for (ProductVariant variant : variants) {
            String color = variant.getColor() != null ? variant.getColor().getName() : "Mặc định";
            if (color.equals(selectedColor)) {
                String size = variant.getSize() != null ? variant.getSize().getName() : "Free";
                availableSizes.add(size);
            }
        }

        // Show tooltip or highlight effect
        showColorSizeInfo(selectedColor, availableSizes, row);
    }

    private void highlightVariantsBySize(String selectedSize, List<ProductVariant> variants, int row) {
        if (variants == null) return;

        // Get all colors available for the selected size
        List<String> availableColors = new ArrayList<>();
        for (ProductVariant variant : variants) {
            String size = variant.getSize() != null ? variant.getSize().getName() : "Free";
            if (size.equals(selectedSize)) {
                String color = variant.getColor() != null ? variant.getColor().getName() : "Mặc định";
                availableColors.add(color);
            }
        }

        // Show tooltip or highlight effect
        showSizeColorInfo(selectedSize, availableColors, row);
    }

    private void showColorSizeInfo(String color, List<String> sizes, int row) {
        SwingUtilities.invokeLater(() -> {
            String message = String.format("Màu %s có sizes: %s", color, String.join(", ", sizes));
            JOptionPane.showMessageDialog(parentTable, message, "Thông tin variant", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    private void showSizeColorInfo(String size, List<String> colors, int row) {
        SwingUtilities.invokeLater(() -> {
            String message = String.format("Size %s có màu: %s", size, String.join(", ", colors));
            JOptionPane.showMessageDialog(parentTable, message, "Thông tin variant", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    private void showAllVariants(List<ProductVariant> variants, int row) {
        SwingUtilities.invokeLater(() -> {
            // Create a detailed variant display dialog
            JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(parentTable), "Tất cả variants", true);
            dialog.setLayout(new BorderLayout());

            // Create variant table
            String[] columnNames = {"Màu", "Size", "Kho", "Giá", "Trạng thái"};
            Object[][] data = new Object[variants.size()][5];

            for (int i = 0; i < variants.size(); i++) {
                ProductVariant variant = variants.get(i);
                data[i][0] = variant.getColor() != null ? variant.getColor().getName() : "Mặc định";
                data[i][1] = variant.getSize() != null ? variant.getSize().getName() : "Free";
                data[i][2] = variant.getStockQuantity();
                data[i][3] = String.format("%,.0f VNĐ", variant.getPrice());
                data[i][4] = variant.getStockQuantity() > 0 ? "Còn hàng" : "Hết hàng";
            }

            JTable variantTable = new JTable(data, columnNames);
            variantTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            variantTable.setRowHeight(30);
            variantTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

            JScrollPane scrollPane = new JScrollPane(variantTable);
            scrollPane.setPreferredSize(new Dimension(500, 300));

            dialog.add(scrollPane, BorderLayout.CENTER);

            // Add close button
            JButton closeButton = new JButton("Đóng");
            closeButton.addActionListener(e -> dialog.dispose());

            JPanel buttonPanel = new JPanel(new FlowLayout());
            buttonPanel.add(closeButton);
            dialog.add(buttonPanel, BorderLayout.SOUTH);

            dialog.pack();
            dialog.setLocationRelativeTo(parentTable);
            dialog.setVisible(true);
        });
    }
}