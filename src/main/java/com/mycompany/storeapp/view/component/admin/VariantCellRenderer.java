package com.mycompany.storeapp.view.component.admin;

import com.mycompany.storeapp.model.entity.ProductVariant;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VariantCellRenderer extends DefaultTableCellRenderer {
    private final JTable table;
    private final List<?> currentData;

    public VariantCellRenderer(JTable table, List<?> currentData) {
        this.table = table;
        this.currentData = currentData;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

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
            return mainPanel;
        }

        JPanel variantPanel = createVariantPanel(variants, row);
        mainPanel.add(variantPanel, BorderLayout.CENTER);

        return mainPanel;
    }

    private List<ProductVariant> getVariantsForRow(int row) {
        if (currentData == null || row >= currentData.size()) {
            return null;
        }

        Object item = currentData.get(row);

        if (item.getClass().getSimpleName().equals("EnhancedProduct")) {
            try {
                java.lang.reflect.Method getVariants = item.getClass().getMethod("getVariants");
                @SuppressWarnings("unchecked")
                List<ProductVariant> variants = (List<ProductVariant>) getVariants.invoke(item);
                return variants;
            } catch (Exception e) {
                System.err.println("Error getting variants: " + e.getMessage());
            }
        }

        return null;
    }

    private JPanel createVariantPanel(List<ProductVariant> variants, int row) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        Map<String, List<ProductVariant>> colorGroups = new HashMap<>();
        for (ProductVariant variant : variants) {
            String colorKey = getColorKey(variant);
            colorGroups.computeIfAbsent(colorKey, k -> new ArrayList<>()).add(variant);
        }

        int colorCount = 0;
        for (Map.Entry<String, List<ProductVariant>> entry : colorGroups.entrySet()) {
            if (colorCount >= 3) {
                break;
            }

            String colorKey = entry.getKey();
            List<ProductVariant> colorVariants = entry.getValue();

            JPanel colorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
            colorPanel.setOpaque(false);

            JComponent colorDisplay = createColorDisplay(colorVariants.get(0), colorKey);
            colorPanel.add(colorDisplay);

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

                sizeLabel.setToolTipText(String.format("Size: %s, Kho: %d", size, variant.getStockQuantity()));

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

        if (colorGroups.size() > 3) {
            JLabel moreLabel = new JLabel("+" + (colorGroups.size() - 3) + " màu khác");
            moreLabel.setFont(new Font("Segoe UI", Font.ITALIC, 10));
            moreLabel.setForeground(new Color(127, 140, 141));
            moreLabel.setHorizontalAlignment(SwingConstants.CENTER);
            moreLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            moreLabel.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));

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

        JPanel colorCircle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color color = parseColor(colorCode);

                g2d.setColor(color);
                g2d.fillOval(2, 2, 16, 16);

                g2d.setColor(new Color(189, 195, 199));
                g2d.drawOval(2, 2, 16, 16);

                g2d.dispose();
            }
        };

        colorCircle.setPreferredSize(new Dimension(20, 20));
        colorCircle.setOpaque(false);

        JLabel colorLabel = new JLabel(colorName);
        colorLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        colorLabel.setForeground(new Color(52, 73, 94));
        colorLabel.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));

        colorPanel.add(colorCircle, BorderLayout.WEST);
        colorPanel.add(colorLabel, BorderLayout.CENTER);

        colorPanel.setToolTipText(String.format("Màu: %s (%s)", colorName, colorCode));

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
            if (colorCode.startsWith("#")) {
                colorCode = colorCode.substring(1);
            }

            if (colorCode.length() == 6) {
                int r = Integer.parseInt(colorCode.substring(0, 2), 16);
                int g = Integer.parseInt(colorCode.substring(2, 4), 16);
                int b = Integer.parseInt(colorCode.substring(4, 6), 16);
                return new Color(r, g, b);
            }
        } catch (Exception e) {
        }

        return new Color(204, 204, 204);
    }

    private int getRowFromComponent(Component component) {
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

        List<String> availableSizes = new ArrayList<>();
        for (ProductVariant variant : variants) {
            String color = variant.getColor() != null ? variant.getColor().getName() : "Mặc định";
            if (color.equals(selectedColor)) {
                String size = variant.getSize() != null ? variant.getSize().getName() : "Free";
                availableSizes.add(size);
            }
        }

        showColorSizeInfo(selectedColor, availableSizes, row);
    }

    private void highlightVariantsBySize(String selectedSize, List<ProductVariant> variants, int row) {
        if (variants == null) return;

        List<String> availableColors = new ArrayList<>();
        for (ProductVariant variant : variants) {
            String size = variant.getSize() != null ? variant.getSize().getName() : "Free";
            if (size.equals(selectedSize)) {
                String color = variant.getColor() != null ? variant.getColor().getName() : "Mặc định";
                availableColors.add(color);
            }
        }

        showSizeColorInfo(selectedSize, availableColors, row);
    }

    private void showColorSizeInfo(String color, List<String> sizes, int row) {
        SwingUtilities.invokeLater(() -> {
            String message = String.format("Màu %s có sizes: %s", color, String.join(", ", sizes));
            JOptionPane.showMessageDialog(table, message, "Thông tin variant", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    private void showSizeColorInfo(String size, List<String> colors, int row) {
        SwingUtilities.invokeLater(() -> {
            String message = String.format("Size %s có màu: %s", size, String.join(", ", colors));
            JOptionPane.showMessageDialog(table, message, "Thông tin variant", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    private void showAllVariants(List<ProductVariant> variants, int row) {
        SwingUtilities.invokeLater(() -> {
            JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(table), "Tất cả variants", true);
            dialog.setLayout(new BorderLayout());

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

            JButton closeButton = new JButton("Đóng");
            closeButton.addActionListener(e -> dialog.dispose());

            JPanel buttonPanel = new JPanel(new FlowLayout());
            buttonPanel.add(closeButton);
            dialog.add(buttonPanel, BorderLayout.SOUTH);

            dialog.pack();
            dialog.setLocationRelativeTo(table);
            dialog.setVisible(true);
        });
    }
}